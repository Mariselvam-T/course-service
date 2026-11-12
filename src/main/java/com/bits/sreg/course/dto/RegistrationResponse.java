package com.bits.sreg.course.dto;

import com.bits.sreg.course.entity.Course;
import com.bits.sreg.course.entity.Registration;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long registrationId,
        Long studentId,
        String studentName,
        String studentEmail,
        Long courseId,
        String courseCode,
        String courseTitle,
        LocalDateTime registeredAt) {

    public static RegistrationResponse from(Registration r, Course c) {
        return new RegistrationResponse(r.getId(), r.getStudentId(), r.getStudentName(),
                r.getStudentEmail(), c.getId(), c.getCode(), c.getTitle(), r.getRegisteredAt());
    }
}
