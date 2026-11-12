package com.bits.sreg.course.repository;

import com.bits.sreg.course.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByCourseId(Long courseId);

    List<Registration> findByCourseId(Long courseId);

    List<Registration> findByStudentId(Long studentId);
}
