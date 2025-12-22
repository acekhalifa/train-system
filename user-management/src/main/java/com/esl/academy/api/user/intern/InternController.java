package com.esl.academy.api.user.intern;

import com.esl.academy.api.user.InternStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Tag(name = "Intern")
@RestController
@RequestMapping("api/v1/interns")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class InternController {

    private final InternService internService;

    @Operation(summary = "Fetch all interns")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR')")
    @GetMapping
    public Page<InternDto> getAllInterns(Pageable pageable) {
        return internService.getAllInterns(pageable);
    }

    @Operation(summary = "Fetch an intern by id")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPERVISOR') or #userId == principal.authentication.id")
    @GetMapping("{userId}")
    public InternDto getInternById(@PathVariable UUID userId) {
        return internService.getInternById(userId);
    }

    @Operation(summary = "Get paginated list of interns by track")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("track/{trackId}")
    public Page<InternDto> getInternsByTrack(@PathVariable UUID trackId, Pageable pageable) {
        return internService.getInternsByTrack(trackId, pageable);
    }

    @Operation(summary = "Get paginated list of interns by status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("status/{status}")
    public Page<InternDto> getInternsByStatus(@PathVariable InternStatus internStatus, Pageable pageable) {
        return internService.getInternsByStatus(internStatus, pageable);
    }

    @Operation(summary = "Get paginated list of previous interns")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("previous")
    public Page<InternDto> getPreviousInterns(Pageable pageable) {
        return internService.getPreviousInterns(pageable);
    }

    @Operation(summary = "Delete an intern")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntern(@PathVariable UUID userId) {
        internService.deleteIntern(userId);
    }

    @Operation(summary = "Search interns by optional filters: trackId, status, joinedDate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("search")
    public Page<InternDto> searchInterns(@RequestParam(required = false) UUID trackId,
                                         @RequestParam(required = false) InternStatus internStatus,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime joinedDate,
                                         Pageable pageable) {
        return internService.searchInterns(trackId, internStatus, joinedDate, pageable);
    }
}
