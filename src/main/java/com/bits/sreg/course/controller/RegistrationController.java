package com.bits.sreg.course.controller;

import com.bits.sreg.course.dto.RegistrationRequest;
import com.bits.sreg.course.dto.RegistrationResponse;
import com.bits.sreg.course.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    /** Register a student to a course. */
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    /** View all registrations. */
    @GetMapping
    public List<RegistrationResponse> getAll() {
        return service.getAll();
    }

    /** View registrations of one course. */
    @GetMapping("/course/{courseId}")
    public List<RegistrationResponse> getByCourse(@PathVariable Long courseId) {
        return service.getByCourse(courseId);
    }

    /** View registrations of one student. */
    @GetMapping("/student/{studentId}")
    public List<RegistrationResponse> getByStudent(@PathVariable Long studentId) {
        return service.getByStudent(studentId);
    }
}
