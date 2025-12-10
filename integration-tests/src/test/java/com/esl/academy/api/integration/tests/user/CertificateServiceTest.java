package com.esl.academy.api.integration.tests.user;

import com.esl.academy.api.DocumentDto;
import com.esl.academy.api.DocumentDto.AddUpdateDocumentDto;
import com.esl.academy.api.DocumentService;
import com.esl.academy.api.FileType;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.track.TrackDto;
import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.certification.CertificateData;
import com.esl.academy.api.certification.CertificateService;
import com.esl.academy.api.intern.InternDto;
import com.esl.academy.api.intern.InternService;
import com.esl.academy.api.intern.InternStatus;
import com.esl.academy.api.user.UserDto;
import com.esl.academy.api.user.UserService;
import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CertificateServiceTest extends BaseIntegrationTest {

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private InternService internService;

    @MockitoBean
    private TrackService trackService;

    @MockitoBean
    private UserService userService;

    // CertificateService will be autowired with mocked dependencies
    private CertificateService certificateService;

    private UUID testUserId;
    private UUID testTrackId;
    private UUID superAdminId;
    private InternDto testInternDto;
    private UserDto testUserDto;
    private UserDto superAdminDto;
    private TrackDto testTrackDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testTrackId = UUID.randomUUID();
        superAdminId = UUID.randomUUID();

        superAdminDto = new UserDto(
            superAdminId,
            "Admin User",
            "Admin",
            "asmin@tesr.com",
            UserType.SUPER_ADMIN,
            UserStatus.ACTIVE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "",
            null
        );

        testUserDto = new UserDto(
            testUserId,
            "John Doe",
            "John",
            "john.doe@test.com",
            UserType.INTERN,
            UserStatus.ACTIVE,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "",
            null
        );

        testTrackDto = new TrackDto(
            testTrackId,
            "Backend Development",
            "Backend development track",
            OffsetDateTime.now().minusMonths(6),
            OffsetDateTime.now(),
            6,
            false,
            "mock learning focus",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );

        testInternDto = new InternDto(
            testUserId,
            testTrackId,
            null, // No certificate yet
            "John",
            "Doe",
            "abc@email.com",
            UserType.INTERN,
            InternStatus.COMPLETED,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            "",
            null
        );

        // Create CertificateService with mocked dependencies
        certificateService = new CertificateService(
            documentService,
            internService,
            trackService,
            userService
        );

        // Reset all mocks before each test
        reset(documentService, internService, trackService, userService);
    }

    @Test
    void generateCertificatePdf_ShouldGenerateValidPdf() {
        CertificateData certificateData = CertificateData.builder()
            .internName("John Doe")
            .trackName("Backend Development Internship Program")
            .startDate(LocalDate.now().minusMonths(6))
            .endDate(LocalDate.now())
            .supervisorName("Admin User")
            .completionDate(LocalDate.now())
            .build();

        MultipartFile result = certificateService.generateCertificatePdf(certificateData);

        assertThat(result).isNotNull();
        assertThat(result.getOriginalFilename()).contains("John_Doe_Certificate");
        assertThat(result.getOriginalFilename()).endsWith(".pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getSize()).isGreaterThan(0);
        assertThat(result.isEmpty()).isFalse();

        verifyNoInteractions(documentService, internService, trackService, userService);
    }


    @Test
    void generateAndSaveCertificate_ShouldGenerateAndSaveSuccessfully() throws IOException {
        when(userService.getUserById(testUserId)).thenReturn(testUserDto);
        when(trackService.getTrackById(testTrackId)).thenReturn(testTrackDto);
        when(userService.getAllSuperAdmins(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(superAdminDto)));

        DocumentDto dummyDoc = new DocumentDto(
            null, "Certificate.pdf", FileType.PDF,
            "path/to/cert.pdf", 1024L, false, "pdf", "application",
            false, OffsetDateTime.now(), OffsetDateTime.now(), "admin", "admin"
                );

        when(documentService.addDocument(any(AddUpdateDocumentDto.class), any(MultipartFile.class)))
            .thenReturn(dummyDoc);
        certificateService.generateAndSaveCertificate(testInternDto);

        verify(userService).getUserById(testUserId);
        verify(trackService).getTrackById(testTrackId);
        verify(userService).getAllSuperAdmins(any(Pageable.class));
        verify(documentService).addDocument(any(AddUpdateDocumentDto.class), any(MultipartFile.class));
    }

    @Test
    void generateCertificatesForCompletedInterns_ShouldProcessOnlyCompletedInterns() {
        List<InternDto> completedInterns = Arrays.asList(testInternDto);
        Page<InternDto> completedInternsPage = new PageImpl<>(completedInterns);

        when(internService.getInternsByStatus(eq(InternStatus.COMPLETED), any(Pageable.class)))
            .thenReturn(completedInternsPage);
        when(userService.getUserById(testUserId)).thenReturn(testUserDto);
        when(trackService.getTrackById(testTrackId)).thenReturn(testTrackDto);
        when(userService.getAllSuperAdmins(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(superAdminDto)));

        certificateService.generateCertificatesForCompletedInterns();

        verify(internService).getInternsByStatus(eq(InternStatus.COMPLETED), any(Pageable.class));
        verify(userService).getUserById(testUserId);
        verify(trackService).getTrackById(testTrackId);
        verify(userService).getAllSuperAdmins(any(Pageable.class));
    }


    @Test
    void generateCertificatesForCompletedInterns_ShouldHandleNoCompletedInterns() throws IOException {
        Page<InternDto> emptyPage = new PageImpl<>(Collections.emptyList());

        when(internService.getInternsByStatus(eq(InternStatus.COMPLETED), any(Pageable.class)))
            .thenReturn(emptyPage);

        certificateService.generateCertificatesForCompletedInterns();

        verify(internService).getInternsByStatus(eq(InternStatus.COMPLETED), any(Pageable.class));
        verify(userService, never()).getUserById(any());
        verify(trackService, never()).getTrackById(any());
        verify(documentService, never()).addDocument(any(), any());
    }

    @Test
    void generateAndSaveCertificate_ShouldUseCorrectFileType() throws IOException {
        when(userService.getUserById(testUserId)).thenReturn(testUserDto);
        when(trackService.getTrackById(testTrackId)).thenReturn(testTrackDto);
        when(userService.getAllSuperAdmins(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(superAdminDto)));

        certificateService.generateAndSaveCertificate(testInternDto);

        verify(documentService).addDocument(
            argThat(dto -> dto.fileType() == FileType.PDF),
            any(MultipartFile.class)
        );
    }

    @Test
    void generateAndSaveCertificate_ShouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(testUserId))
            .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () ->
            certificateService.generateAndSaveCertificate(testInternDto)
        );

        verify(userService).getUserById(testUserId);
        verifyNoInteractions(documentService);
    }
}
