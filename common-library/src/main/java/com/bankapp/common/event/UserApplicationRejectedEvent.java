package com.bankapp.common.event;

public record UserApplicationRejectedEvent(String email, Long id) {
}
