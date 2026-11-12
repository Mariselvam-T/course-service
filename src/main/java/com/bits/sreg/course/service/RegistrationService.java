package com.bits.sreg.course.service;

import com.bits.sreg.course.client.StudentClient;
import com.bits.sreg.course.dto.RegistrationRequest;
import com.bits.sreg.course.dto.RegistrationResponse;
import com.bits.sreg.course.dto.StudentDto;
import com.bits.sreg.course.entity.Course;
import com.bits.sreg.course.entity.Registration;
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
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final RegistrationRepository registrationRepository;
    private final CourseRepository courseRepository;
    private final StudentClient studentClient;

    public RegistrationService(RegistrationRepository registrationRepository,
                               CourseRepository courseRepository,
                               StudentClient studentClient) {
        this.registrationRepository = registrationRepository;
        this.courseRepository = courseRepository;
        this.studentClient = studentClient;
    }

    // ---------------- Command ----------------

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        // 1. the course must exist (local database)
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + request.courseId()));

        // 2. the student must exist (REST call to Student Service)
        StudentDto student = studentClient.getStudent(request.studentId());

        // 3. business rules
        if (registrationRepository.existsByStudentIdAndCourseId(student.id(), course.getId())) {
            throw new ConflictException("Student " + student.id() + " is already registered for course "
                    + course.getCode());
        }
        long registered = registrationRepository.countByCourseId(course.getId());
        if (registered >= course.getCapacity()) {
            throw new ConflictException("Course " + course.getCode() + " is full");
        }

        // 4. save the registration
        Registration saved = registrationRepository.save(
                new Registration(student.id(), student.name(), student.email(), course.getId()));
        log.info("Registered student {} for course {}", student.id(), course.getCode());
        return RegistrationResponse.from(saved, course);
    }

    // ---------------- Queries ----------------

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getAll() {
        return registrationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id " + courseId);
        }
        return registrationRepository.findByCourseId(courseId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getByStudent(Long studentId) {
        return registrationRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    private RegistrationResponse toResponse(Registration r) {
        Course course = courseRepository.findById(r.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + r.getCourseId()));
        return RegistrationResponse.from(r, course);
    }
}
