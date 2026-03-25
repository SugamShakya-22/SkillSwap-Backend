package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.exception.DuplicateResourceException;
import com.skillswap.skillswap.model.User;
import com.skillswap.skillswap.repository.UserRepository;
import com.skillswap.skillswap.service.security.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Override
    public void sendPasswordReset(User user) {
        String code = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(Integer.parseInt(code));
        userRepository.save(user);
        String content = "<p>Hi " + user.getEmail().substring(0, user.getEmail().indexOf("@")) + ",</p>"
                + "<p>Your password reset code is:</p>"
                + "<h3>" + code + "</h3>"
                + "<p>If you didn’t request this, ignore this email.</p>";

        sendVerification(user, content, "Password reset code");
    }

    @Override
    public void sendWelcomeMail(User user){
        String content="<p>Hi " + user.getEmail().substring(0, user.getEmail().indexOf("@")) + ",</p>"
                + "<h3>" + "Welcome to SkillSwap" + "</h3>";
        sendVerification(user, content,"Welcome to SkillSwap");
    }

    @Override
    public void sendPasswordAboutToExpire(User user){
        String content="<p>Hi " + user.getEmail().substring(0, user.getEmail().indexOf("@")) + ",</p>"
                + "<h3>" + "Your password is about to expire" + "</h3>";
        sendVerification(user, content,"Password about to expire");
    }



    @Override
    public boolean verify(String code, User user) {
        return user.getVerificationCode() == Integer.parseInt(code);
    }

    public void sendVerification(User user, String content,String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("sugamshakya62@gmail.com", "SkillSwap");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new DuplicateResourceException("Failed to send email");
        }
    }
}
