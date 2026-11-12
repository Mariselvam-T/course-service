package com.bits.sreg.course.dto;

import com.bits.sreg.course.entity.Course;

public record CourseResponse(
        Long id,
        String code,
        String title,
        int credits,
        int capacity,
        long registered,
        long seatsAvailable) {

    public static CourseResponse from(Course c, long registered) {
        return new CourseResponse(c.getId(), c.getCode(), c.getTitle(), c.getCredits(),
                c.getCapacity(), registered, Math.max(0, c.getCapacity() - registered));
    }
}
