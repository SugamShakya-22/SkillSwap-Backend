// ChatReadReceipt.java
package com.skillswap.skillswap.dtos.response;

public record ChatReadReceipt(
        Long messageId,
        Long readerId
) {}
