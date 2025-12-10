package com.esl.academy.api.intern;

import com.esl.academy.api.core.exceptions.NotFoundException;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserRepository;
import com.esl.academy.api.user.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.esl.academy.api.intern.InternDto.UpdateInternTrackDto;
import com.esl.academy.api.intern.InternDto.UpdateInternStatusDto;

@Service
@Transactional
@RequiredArgsConstructor
public class InternService {

    private final InternRepository internRepository;
    private final UserRepository userRepository;

    public InternDto updateInternTrack(UUID userId, UpdateInternTrackDto dto) {
        Intern intern = internRepository.findByUserIdAndInternStatus(userId, InternStatus.ACTIVE)
            .orElseThrow(() -> new NotFoundException("Intern with ID: " + userId + " not found"));

        intern.setTrackId(dto.trackId());
        return InternMapper.INSTANCE.toDto(internRepository.save(intern));
    }

    public InternDto updateInternStatus(UUID userId, UpdateInternStatusDto dto) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Intern with ID " + userId + " not found"));

        Intern intern = user.getIntern();
        user.getIntern().setInternStatus(dto.internStatus());

        if (dto.internStatus() == InternStatus.ACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
        }

        if (dto.internStatus() == InternStatus.DISCONTINUED) {
            user.setStatus(UserStatus.INACTIVE);
        }

        userRepository.save(user);
        return InternMapper.INSTANCE.toDto(intern);
    }

    public Page<InternDto> getAllInterns(Pageable pageable) {
        return internRepository.findAll(pageable)
            .map(InternMapper.INSTANCE::toDto);
    }

    public InternDto getInternById(UUID userId) {
        Intern intern = internRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Intern with ID " + userId + " not found"));

        return InternMapper.INSTANCE.toDto(intern);
    }

    public Page<InternDto> getInternsByTrack(UUID trackId, Pageable pageable) {
        return internRepository.findByTrackId(trackId, pageable)
            .map(InternMapper.INSTANCE::toDto);
    }

    public Page<InternDto> getInternsByStatus(InternStatus internStatus, Pageable pageable) {
        return internRepository.findByInternStatus(internStatus, pageable)
            .map(InternMapper.INSTANCE::toDto);
    }

    public Page<InternDto> getPreviousInterns(Pageable pageable) {
        return internRepository.findByInternStatus(InternStatus.COMPLETED, pageable)
            .map(InternMapper.INSTANCE::toDto);
    }

    public void deleteIntern(UUID internId) {
        Intern intern = internRepository.findById(internId)
            .orElseThrow(() -> new NotFoundException("Intern with ID: " + internId + " not found"));

        intern.setInternStatus(InternStatus.DISCONTINUED);
        userRepository.findById(internId).ifPresent(user -> {
            user.setStatus(UserStatus.INACTIVE);
        });
    }

    public Page<InternDto> searchInterns(
        UUID trackId,
        InternStatus internStatus,
        OffsetDateTime joinedDate,
        Pageable pageable
    ) {
        Specification<Intern> spec = (root, query, cb) -> null;

        if (trackId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("trackId"), trackId));
        }
        if (internStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("internStatus"), internStatus));
        }
        if (joinedDate != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("joinedDate"), joinedDate));
        }

        return internRepository.findAll(spec, pageable)
            .map(InternMapper.INSTANCE::toDto);
    }


}
