package com.esl.academy.api.invitation;

import com.esl.academy.api.core.exceptions.InternalServerException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${application.email.from-name:TCMP}")
    private String fromName;

    @Async
    public void sendInternInvitation(
        String toEmail,
        String internName,
        String invitationUrl,
        String customMessage) {

        try {
            log.info("Sending intern invitation to: {}", toEmail);

            String subject = "Welcome to TCMP - Complete Your Setup";

            String emailContent = String.format("""
                Hi %s,

                Welcome to the Training & Certificate Management Portal! We're excited to have you join our internship program.

                Message from your supervisor:
                %s

                To complete your setup, please click the link below:
                %s

                This invitation link expires in 7 days.

                Best regards,
                TCMP Team
                """, internName, customMessage, invitationUrl);

            sendEmail(toEmail, subject, emailContent);

            log.info("Intern invitation sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send intern invitation to: {}", toEmail, e);
            throw new InternalServerException("Failed to send invitation email", e);
        }
    }

    @Async
    public void sendSupervisorInvitation(
        String toEmail,
        String supervisorName,
        String invitationUrl) {

        try {
            log.info("Sending supervisor invitation to: {}", toEmail);

            final String subject = "Welcome to TCMP - Complete Your Supervisor Setup";

            final String  emailContent = String.format("""
                Hi %s,

                You've been invited to join TCMP as a Supervisor.

                As a supervisor, you'll be able to:
                - Monitor interns and track their progress
                - Manage training programs
                - Issue certificates
                - Access reports and analytics

                To complete your setup, please click the link below:
                %s

                This invitation link expires in 7 days.

                If you have any questions, please contact the admin team.

                Best regards,
                TCMP Team
                """, supervisorName, invitationUrl);

            sendEmail(toEmail, subject, emailContent);

            log.info("Supervisor invitation sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send supervisor invitation to: {}", toEmail, e);
            throw new InternalServerException("Failed to send invitation email", e);
        }
    }

    @Async
    public void sendSuperAdminInvitation(
        String toEmail,
        String superAdminName,
        String invitationUrl) {

        try {
            log.info("Sending super admin invitation to: {}", toEmail);

            final String subject = "Welcome to TCMP - Complete Your Super Admin Setup";

            final String  emailContent = String.format("""
                Hi %s,

                You've been invited to join TCMP as a Super Admin.

                As a super admin, you'll be able to:
                - Monitor interns and track their progress
                - Monitor training programs
                - Monitor supervisors
                - Access reports and analytics

                To complete your setup, please click the link below:
                %s

                This invitation link expires in 7 days.

                If you have any questions, please contact the admin team.

                Best regards,
                TCMP Team
                """, superAdminName, invitationUrl);

            sendEmail(toEmail, subject, emailContent);

            log.info("Super Admin invitation sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send super admin invitation to: {}", toEmail, e);
            throw new InternalServerException("Failed to send invitation email", e);
        }
    }

    private void sendEmail(String toEmail, String subject, String content){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, false); // false = plain text (not HTML)

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed to send supervisor invitation to: {}", toEmail, e);
            throw new InternalServerException("Failed to send invitation email", e);
        }
    }
}
