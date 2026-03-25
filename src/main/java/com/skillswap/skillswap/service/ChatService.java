package com.skillswap.skillswap.service;

import com.skillswap.skillswap.dtos.response.ChatUserResponse;

import java.util.List;

public interface ChatService {

    List<ChatUserResponse> getChatUsers(String currentUserEmail);
}
