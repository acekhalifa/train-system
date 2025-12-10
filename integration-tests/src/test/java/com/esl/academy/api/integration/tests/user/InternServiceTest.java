package com.esl.academy.api.integration.tests.user;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.intern.InternDto;
import com.esl.academy.api.intern.InternDto.UpdateInternTrackDto;
import com.esl.academy.api.intern.InternDto.UpdateInternStatusDto;
import com.esl.academy.api.intern.InternService;
import com.esl.academy.api.intern.InternRepository;
import com.esl.academy.api.intern.InternStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InternServiceTest extends BaseIntegrationTest {

    @Autowired
    private InternService internService;

    @Autowired
    private InternRepository internRepository;

    private UUID existingInternId;
    private UUID backendTrackId;

    @BeforeEach
    void setup() {
        existingInternId = UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13");
        backendTrackId = UUID.fromString("c0eebc94-9c0b-4ef8-bb6d-6bb9bd380a12");
    }

    @Test
    void testUpdateInternTrack() {
        InternDto dto = internService.updateInternTrack(existingInternId, new UpdateInternTrackDto(backendTrackId));
        assertThat(dto.trackId()).isEqualTo(backendTrackId);
    }

    @Test
    void testUpdateInternStatusToActive() {
        InternDto dto = internService.updateInternStatus(existingInternId, new UpdateInternStatusDto(InternStatus.ACTIVE));
        assertThat(dto.internStatus()).isEqualTo(InternStatus.ACTIVE);
    }

    @Test
    void testUpdateInternStatusToDiscontinued() {
        InternDto dto = internService.updateInternStatus(existingInternId, new InternDto.UpdateInternStatusDto(InternStatus.DISCONTINUED));
        assertThat(dto.internStatus()).isEqualTo(InternStatus.DISCONTINUED);
    }

    @Test
    void testGetAllInterns() {
        Page<InternDto> interns = internService.getAllInterns(PageRequest.of(0, 10));
        assertThat(interns.getContent()).isNotEmpty();
    }

    @Test
    void testGetInternByIdFound() {
        InternDto intern = internService.getInternById(existingInternId);
        assertThat(intern).isNotNull();
        assertThat(intern.userId()).isEqualTo(existingInternId);
    }

    @Test
    void testGetInternByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () -> internService.getInternById(randomId));
    }

    @Test
    void testGetInternsByTrack() {
        Page<InternDto> interns = internService.getInternsByTrack(backendTrackId, PageRequest.of(0, 10));
        assertThat(interns.getContent()).isNotEmpty();
        assertThat(interns.getContent().getFirst().trackId()).isEqualTo(backendTrackId);
    }

    @Test
    void testDeleteIntern() {
        internService.deleteIntern(existingInternId);
        InternDto intern = internService.getInternById(existingInternId);
        assertThat(intern.internStatus()).isEqualTo(InternStatus.DISCONTINUED);
    }

    @Test
    void testSearchInterns() {
        Page<InternDto> interns = internService.searchInterns(backendTrackId, InternStatus.ACTIVE, null, PageRequest.of(0, 10));
        assertThat(interns.getContent()).isNotEmpty();
    }
}

