package com.bits.sreg.course.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Shape of the response we read from the Student Service.
 * Fields we do not need (for example createdAt) are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StudentDto(
        Long id,
        String name,
        String email,
        String department) {
}
