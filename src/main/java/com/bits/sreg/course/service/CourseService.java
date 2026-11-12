package com.bits.sreg.course.service;

import com.bits.sreg.course.dto.CourseRequest;
import com.bits.sreg.course.dto.CourseResponse;
import com.bits.sreg.course.entity.Course;
import com.bits.sreg.course.exception.ConflictException;
import com.bits.sreg.course.exception.ResourceNotFoundException;
import com.bits.sreg.course.repository.CourseRepository;
import com.bits.sreg.course.repository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    public CourseService(CourseRepository courseRepository, RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.registrationRepository = registrationRepository;
    }

    // ---------------- Command ----------------

    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByCode(request.code())) {
            throw new ConflictException("A course with code " + request.code() + " already exists");
        }
        Course saved = courseRepository.save(
                new Course(request.code(), request.title(), request.credits(), request.capacity()));
        log.info("Created course id={} code={}", saved.getId(), saved.getCode());
        return CourseResponse.from(saved, 0);
    }

    // ---------------- Queries ----------------

    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
        return CourseResponse.from(course, registrationRepository.countByCourseId(id));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAll() {
        return courseRepository.findAll().stream()
                .map(c -> CourseResponse.from(c, registrationRepository.countByCourseId(c.getId())))
                .toList();
    }
}
