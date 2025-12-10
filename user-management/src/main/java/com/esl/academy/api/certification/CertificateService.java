package com.esl.academy.api.certification;

import static com.esl.academy.api.DocumentDto.AddUpdateDocumentDto;
import com.esl.academy.api.DocumentService;
import com.esl.academy.api.FileType;
import com.esl.academy.api.core.exceptions.InternalServerException;
import com.esl.academy.api.track.TrackService;
import com.esl.academy.api.intern.InternDto;
import com.esl.academy.api.intern.InternService;
import com.esl.academy.api.intern.InternStatus;
import com.esl.academy.api.user.UserDto;
import com.esl.academy.api.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService {
    private final DocumentService documentService;
    private final InternService internService;
    private final TrackService trackService;
    private final UserService userService;
    private static final String CERTIFICATE_TEMPLATE = "reports/certificate_template.jrxml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private JasperReport jasperReport;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCertificatesForCompletedInterns() {
        log.info("Starting scheduled certificate generation job at {}", OffsetDateTime.now());

        try {
            List<InternDto> completedInterns = internService.getInternsByStatus(InternStatus.COMPLETED,
                Pageable.unpaged()).toList();
            if (completedInterns.isEmpty()) {
                log.info("No completed interns found for certificate generation");
                return;
            }

            log.info("Found {} completed interns for certificate generation", completedInterns.size());
            int successCount = 0;
            int failureCount = 0;
            for (var internDto : completedInterns) {
                try {
                    if (internDto.certificateId() != null) {
                        continue;
                    }
                    generateAndSaveCertificate(internDto);
                    successCount++;
                    log.info("Certificate generated successfully for intern: {}", internDto.userId());
                } catch (Exception e) {
                    failureCount++;
                    log.error("Failed to generate certificate for intern: {}", internDto.userId(), e);
                }
            }
            log.info("Certificate generation job completed. Success: {}, Failures: {}",
                successCount, failureCount);
        } catch (Exception e) {
            log.error("Error in scheduled certificate generation job", e);
        }
    }

    @Transactional
    public void generateAndSaveCertificate(InternDto intern) {
        log.info("Generating certificate for intern: {}", intern.userId());
        UserDto user = userService.getUserById(intern.userId());
        var track = trackService.getTrackById(intern.trackId());
        var admin = userService.getAllSuperAdmins(Pageable.unpaged()).toList()
            .getFirst();
        var adminName = admin.firstName() + " " + admin.lastName();

        CertificateData certificateData = CertificateData.builder()
            .internName(user.firstName() + " " + user.lastName())
            .trackName(track.name() + " Internship Program")
            .startDate(track.startDate().toLocalDate())
            .endDate(track.endDate().toLocalDate())
            .supervisorName(adminName)
            .completionDate(LocalDate.now())
            .build();

        var multipartFile = generateCertificatePdf(certificateData);
        String fileName = generateFileName(certificateData.internName());
        var addDocumentDto = new AddUpdateDocumentDto(
            fileName,
            FileType.PDF,
            true
        );
        try {
            documentService.addDocument(addDocumentDto, multipartFile);
        } catch (IOException e) {
            throw new InternalServerException(e);
        }
    }

    public MultipartFile generateCertificatePdf(CertificateData certificateData) {
        log.info("Generating certificate PDF for: {}", certificateData.internName());

        try {
            //compile the Jasper report only once
            if (jasperReport == null) {
                try (InputStream templateStream = new ClassPathResource(CERTIFICATE_TEMPLATE).getInputStream()) {
                    jasperReport = JasperCompileManager.compileReport(templateStream);
                }
            }
            Map<String, Object> parameters = prepareParameters(certificateData);
            // Create empty data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of());
            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            // Export to PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            String fileName = generateFileName(certificateData.internName());
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                outputStream.toByteArray(),
                "certificate",
                fileName,
                "application/pdf"
            );
            log.info("Certificate PDF generated successfully for: {}", certificateData.internName());
            return multipartFile;
        } catch (Exception e) {
            log.error("Error generating certificate PDF: {}", e.getMessage(), e);
            throw new InternalServerException("Failed to generate certificate PDF", e);
        }
    }

    private Map<String, Object> prepareParameters(CertificateData data) {
        Map<String, Object> map = new HashMap<>();

        map.put("internName", data.internName());
        map.put("trackName", data.trackName());
        map.put("startDate", data.startDate().format(DATE_FORMATTER));
        map.put("endDate", data.endDate().format(DATE_FORMATTER));
        map.put("completionDate", data.completionDate().format(DATE_FORMATTER));
        map.put("supervisorSign", data.supervisorName());
        map.put("supervisorName", data.supervisorName());
        return map;
    }

    private String generateFileName(String name) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("%s_Certificate_%s.pdf",
            name.replace(" ", "_"),
            timestamp);
    }
}
