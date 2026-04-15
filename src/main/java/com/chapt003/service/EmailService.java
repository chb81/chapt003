package com.chapt003.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.mail.from:noreply@chapt003.com}")
    private String fromEmail;
    
    @Value("${app.mail.from-name:Chapter 003}")
    private String fromName;
    
    public void sendPasswordResetCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("【Chapter 003】密码重置验证码");
            
            String emailContent = buildPasswordResetEmailContent(code);
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            logger.info("Password reset code sent to email: {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("发送邮件失败，请稍后重试");
        } catch (Exception e) {
            logger.error("Unexpected error sending password reset email to: {}", toEmail, e);
            throw new RuntimeException("发送邮件失败，请稍后重试");
        }
    }
    
    private String buildPasswordResetEmailContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <style>" +
                "    body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { text-align: center; margin-bottom: 30px; }" +
                "    .logo { font-size: 24px; font-weight: bold; color: #1890ff; }" +
                "    .content { background: #f9f9f9; padding: 30px; border-radius: 8px; }" +
                "    .code { font-size: 32px; font-weight: bold; color: #1890ff; text-align: center; " +
                "            padding: 20px; background: #fff; border-radius: 8px; margin: 20px 0; }" +
                "    .footer { text-align: center; margin-top: 30px; color: #999; font-size: 12px; }" +
                "    .warning { color: #ff4d4f; font-size: 14px; margin-top: 15px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class=\"container\">" +
                "    <div class=\"header\">" +
                "      <div class=\"logo\">Chapter 003</div>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "      <h2>密码重置验证码</h2>" +
                "      <p>您好！</p>" +
                "      <p>您正在重置 Chapter 003 账户的密码，您的验证码是：</p>" +
                "      <div class=\"code\">" + code + "</div>" +
                "      <p>验证码有效期为 10 分钟，请尽快完成密码重置。</p>" +
                "      <p class=\"warning\">如果您没有请求重置密码，请忽略此邮件，您的账户安全不会受到影响。</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "      <p>此邮件由系统自动发送，请勿直接回复。</p>" +
                "      <p>© 2026 Chapter 003. All rights reserved.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
