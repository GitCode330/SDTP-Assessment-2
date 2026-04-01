package com.example.softwaretesting;

import com.example.softwaretesting.controller.LmsController;
import com.example.softwaretesting.model.*;
import com.example.softwaretesting.service.LmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LmsController.class)
@DisplayName("LmsController Unit Tests")
class LmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LmsService lmsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/students/{id}/enrollments should return 200 with enrollments")
    void testGetStudentEnrollments_Success() throws Exception {
        // Arrange
        Enrollment enrollment = new Enrollment(1L, "Alice Johnson", 1L, "CS101", "Intro to Programming");
        List<Enrollment> enrollments = Arrays.asList(enrollment);

        when(lmsService.getStudentEnrollments(1L)).thenReturn(enrollments);

        // Act & Assert
        mockMvc.perform(get("/api/students/1/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentName", is("Alice Johnson")));
    }

    @Test
    @DisplayName("GET /api/students/{id}/enrollments should return empty array for student with no enrollments")
    void testGetStudentEnrollments_NoEnrollments() throws Exception {
        // Arrange
        when(lmsService.getStudentEnrollments(4L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/students/4/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/students/active should return active students")
    void testGetActiveStudents() throws Exception {
        // Arrange
        Student student1 = new Student(1L, "Alice Johnson", "alice@example.com");
        Student student2 = new Student(2L, "Bob Smith", "bob@example.com");
        List<Student> students = Arrays.asList(student1, student2);

        when(lmsService.getActiveStudents()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/api/students/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")))
                .andExpect(jsonPath("$[1].name", is("Bob Smith")));
    }

    @Test
    @DisplayName("GET /api/instructors/most-active should return most active instructor")
    void testGetMostActiveInstructor() throws Exception {
        // Arrange
        Instructor instructor = new Instructor(1L, "Dr. Emily Brown", "emily@example.com", "CS");
        when(lmsService.getMostActiveInstructor()).thenReturn(Optional.of(instructor));

        // Act & Assert
        mockMvc.perform(get("/api/instructors/most-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Dr. Emily Brown")));
    }

    @Test
    @DisplayName("GET /api/instructors/inactive should return instructors with no enrollments")
    void testGetInstructorsWithNoEnrollments() throws Exception {
        // Arrange
        Instructor instructor = new Instructor(3L, "Dr. Sarah Wilson", "sarah@example.com", "Physics");
        List<Instructor> instructors = Arrays.asList(instructor);

        when(lmsService.getInstructorsWithNoEnrollments()).thenReturn(instructors);

        // Act & Assert
        mockMvc.perform(get("/api/instructors/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Dr. Sarah Wilson")));
    }
}