package com.example.softwaretesting;

import com.example.softwaretesting.model.Course;
import com.example.softwaretesting.model.Instructor;
import com.example.softwaretesting.model.Student;
import com.example.softwaretesting.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("DataService Tests")
class DataServiceTest {

    @Autowired
    private DataService dataService;

    @BeforeEach
    void setUp() {
        // DataService init is called automatically with @PostConstruct
    }

    @Test
    @DisplayName("Should initialize sample data correctly")
    void testInit_SampleDataLoaded() {
        // Assert
        assertThat(dataService.getStudents()).isNotEmpty();
        assertThat(dataService.getInstructors()).isNotEmpty();
        assertThat(dataService.getCourses()).isNotEmpty();

        // Check specific data counts
        assertThat(dataService.getStudents()).hasSize(4);
        assertThat(dataService.getInstructors()).hasSize(4);
        assertThat(dataService.getCourses()).hasSize(4);
    }

    @Test
    @DisplayName("Should find student by ID")
    void testGetStudentById_Found() {
        // Act
        Optional<Student> student = dataService.getStudentById(1L);

        // Assert
        assertThat(student).isPresent();
        assertThat(student.get().getName()).isEqualTo("Alice Johnson");
    }

    @Test
    @DisplayName("Should return empty for non-existent student")
    void testGetStudentById_NotFound() {
        // Act
        Optional<Student> student = dataService.getStudentById(999L);

        // Assert
        assertThat(student).isEmpty();
    }

    @Test
    @DisplayName("Should find instructor by ID")
    void testGetInstructorById_Found() {
        // Act
        Optional<Instructor> instructor = dataService.getInstructorById(1L);

        // Assert
        assertThat(instructor).isPresent();
        assertThat(instructor.get().getName()).isEqualTo("Dr. Emily Brown");
    }

    @Test
    @DisplayName("Should find course by ID")
    void testGetCourseById_Found() {
        // Act
        Optional<Course> course = dataService.getCourseById(1L);

        // Assert
        assertThat(course).isPresent();
        assertThat(course.get().getName()).isEqualTo("Introduction to Programming");
    }
}