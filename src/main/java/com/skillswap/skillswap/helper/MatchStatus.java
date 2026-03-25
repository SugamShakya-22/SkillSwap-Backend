package com.skillswap.skillswap.helper;

public enum MatchStatus {
    PENDING,                // Request sent, waiting for response
    ACCEPTED,               // Request accepted, swap in progress
    COMPLETION_REQUESTED,   // One user marked as completed
    COMPLETED,              // Both users confirmed completion
    DECLINED                // Request declined
}
