package com.skillswap.skillswap.service.security;


import com.skillswap.skillswap.model.User;

public interface MailService {
    void sendPasswordReset(User user);
    boolean verify(String code, User user);
     void sendVerification(User user, String content,String subject);
    void sendWelcomeMail(User user);
    void sendPasswordAboutToExpire(User user);
}
