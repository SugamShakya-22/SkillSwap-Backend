// src/main/java/com/skillswap/skillswap/dtos/response/UserStatusDTO.java
package com.skillswap.skillswap.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusDTO {
    private Long userId;
    private Boolean isOnline;
}
