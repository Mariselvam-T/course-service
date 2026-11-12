package com.bits.sreg.course.client;

import com.bits.sreg.course.dto.StudentDto;
import com.bits.sreg.course.exception.ResourceNotFoundException;
import com.bits.sreg.course.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * REST client used by the Course Service to call the Student Service.
 */
@Component
public class StudentClient {

    private static final Logger log = LoggerFactory.getLogger(StudentClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StudentClient(RestTemplate restTemplate, @Value("${student.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public StudentDto getStudent(Long studentId) {
        String url = baseUrl + "/api/students/{id}";
        log.info("Calling Student Service: GET {}", url.replace("{id}", String.valueOf(studentId)));
        try {
            StudentDto student = restTemplate.getForObject(url, StudentDto.class, studentId);
            if (student == null) {
                throw new ResourceNotFoundException("Student not found with id " + studentId);
            }
            return student;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        } catch (RestClientException e) {
            log.error("Student Service call failed: {}", e.getMessage());
            throw new ServiceUnavailableException("Student Service is not reachable, please try again later");
        }
    }
}
