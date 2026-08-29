package com.example.demo.correo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CorreoService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String senderAddress;

    public CorreoService(ObjectProvider<JavaMailSender> mailSenderProvider,
                         @Value("${spring.mail.username:}") String senderAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.senderAddress = senderAddress;
    }

    public void sendAssignedCourses(String correo,
                                    String nombreCompleto,
                                    List<String> cursos) throws MessagingException {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException(
                    "Email is not configured: spring.mail.host is missing, so JavaMailSender is unavailable");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, true, StandardCharsets.UTF_8.name());
        if (!senderAddress.isBlank()) {
            helper.setFrom(senderAddress);
        }
        helper.setTo(correo);
        helper.setSubject("Cursos asignados | Registro de estudiantes");

        String plainText = buildPlainText(nombreCompleto, cursos);
        String htmlBody = buildHtml(nombreCompleto, cursos);
        helper.setText(plainText, htmlBody);
        mailSender.send(message);
    }

    private String buildPlainText(String nombreCompleto, List<String> cursos) {
        StringBuilder body = new StringBuilder("Se le asignaron los siguientes cursos:\n");
        body.append("Estudiante: ").append(nombreCompleto).append("\n\n");
        for (String curso : cursos) {
            body.append("- ").append(curso).append("\n");
        }
        body.append("\nEste mensaje fue generado automáticamente por el sistema de estudiantes.\n");
        return body.toString();
    }

    private String buildHtml(String nombreCompleto, List<String> cursos) {
        StringBuilder items = new StringBuilder();
        for (String curso : cursos) {
            items.append("<li style=\"margin:0 0 10px; padding:12px 14px; ")
                    .append("background:#F5F8F7; border:1px solid #DCE8E3; border-radius:10px; ")
                    .append("color:#16302B; font-size:15px; line-height:1.45;\">")
                    .append(escapeHtml(curso))
                    .append("</li>");
        }

        return "<!doctype html>"
                + "<html lang=\"es\"><body style=\"margin:0; padding:0; background:#F5F2EC; "
                + "font-family:Arial,Helvetica,sans-serif; color:#16302B;\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" "
                + "style=\"background:#F5F2EC; padding:28px 12px;\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" "
                + "style=\"max-width:620px; background:#FFFFFF; border:1px solid #E4DFD4; "
                + "border-radius:16px; overflow:hidden;\">"
                + "<tr><td style=\"background:#16302B; padding:28px 32px;\">"
                + "<div style=\"color:#A9D8C9; font-size:12px; letter-spacing:2px; "
                + "text-transform:uppercase; font-weight:bold;\">Registro de estudiantes</div>"
                + "<h1 style=\"margin:8px 0 0; color:#FFFFFF; font-size:28px; line-height:1.2;\">"
                + "Cursos asignados</h1></td></tr>"
                + "<tr><td style=\"padding:32px;\">"
                + "<p style=\"margin:0 0 10px; font-size:17px;\">Hola, <strong>"
                + escapeHtml(nombreCompleto)
                + "</strong>.</p>"
                + "<p style=\"margin:0 0 24px; color:#53645D; font-size:15px; line-height:1.6;\">"
                + "Se le asignaron los siguientes cursos:</p>"
                + "<ul style=\"list-style:none; margin:0; padding:0;\">"
                + items
                + "</ul>"
                + "<p style=\"margin:24px 0 0; color:#6C7B74; font-size:12px; line-height:1.5;\">"
                + "Si necesitás realizar algún cambio, comunicate con la administración académica.</p>"
                + "</td></tr>"
                + "<tr><td style=\"background:#F5F8F7; padding:18px 32px; color:#6C7B74; "
                + "font-size:12px;\">Este es un mensaje automático; por favor no respondas a este correo.</td></tr>"
                + "</table></td></tr></table></body></html>";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
