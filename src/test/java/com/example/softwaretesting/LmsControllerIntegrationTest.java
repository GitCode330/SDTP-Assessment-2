package com.example.softwaretesting;

import com.example.softwaretesting.model.Enrollment;
import com.example.softwaretesting.model.Instructor;
import com.example.softwaretesting.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("LmsController Integration Tests")
class LmsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("F1: Should return enrollments for existing student")
    void testGetStudentEnrollments_ExistingStudent() {
        // Act
        ResponseEntity<Enrollment[]> response = restTemplate.getForEntity(
                "/api/students/1/enrollments",
                Enrollment[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Enrollment[] enrollments = response.getBody();
        assertThat(enrollments).isNotEmpty();
        assertThat(enrollments[0].getStudentName()).isEqualTo("Alice Johnson");
    }

    @Test
    @DisplayName("F1: Should return empty array for student with no enrollments")
    void testGetStudentEnrollments_NoEnrollments() {
        // Act
        ResponseEntity<Enrollment[]> response = restTemplate.getForEntity(
                "/api/students/4/enrollments",
                Enrollment[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Enrollment[] enrollments = response.getBody();
        assertThat(enrollments).isNotNull();
        assertThat(enrollments).isEmpty();
    }

    @Test
    @DisplayName("F2: Should return list of active students")
    void testGetActiveStudents() {
        // Act
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "/api/students/active",
                Student[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student[] students = response.getBody();
        assertThat(students).isNotEmpty();
        assertThat(students).allMatch(s -> s.getName() != null);
    }

    @Test
    @DisplayName("F3: Should return most active instructor")
    void testGetMostActiveInstructor() {
        // Act
        ResponseEntity<Instructor> response = restTemplate.getForEntity(
                "/api/instructors/most-active",
                Instructor.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Instructor instructor = response.getBody();
        assertThat(instructor).isNotNull();
        assertThat(instructor.getName()).isEqualTo("Dr. Emily Brown");
    }

    @Test
    @DisplayName("F4: Should return instructors with no enrollments")
    void testGetInstructorsWithNoEnrollments() {
        // Act
        ResponseEntity<Instructor[]> response = restTemplate.getForEntity(
                "/api/instructors/inactive",
                Instructor[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Instructor[] instructors = response.getBody();
        assertThat(instructors).isNotEmpty();
        assertThat(instructors.length).isGreaterThanOrEqualTo(2);
    }
}