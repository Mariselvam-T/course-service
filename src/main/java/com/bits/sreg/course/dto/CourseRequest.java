package com.bits.sreg.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "code is required")
        @Size(max = 20, message = "code must be at most 20 characters")
        String code,

        @NotBlank(message = "title is required")
        @Size(max = 150, message = "title must be at most 150 characters")
        String title,

        @NotNull(message = "credits is required")
        @Min(value = 1, message = "credits must be at least 1")
        @Max(value = 10, message = "credits must be at most 10")
        Integer credits,

        @NotNull(message = "capacity is required")
        @Min(value = 1, message = "capacity must be at least 1")
        Integer capacity) {
}
