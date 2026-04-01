package com.example.softwaretesting;

import com.example.softwaretesting.model.*;
import com.example.softwaretesting.service.DataService;
import com.example.softwaretesting.service.LmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LmsService Unit Tests")
class LmsServiceTest {

    @Mock
    private DataService dataService;

    @InjectMocks
    private LmsService lmsService;

    private Map<Long, Student> students;
    private Map<Long, Instructor> instructors;
    private Map<Long, Course> courses;

    @BeforeEach
    void setUp() {
        // Setup test data
        students = new HashMap<>();
        instructors = new HashMap<>();
        courses = new HashMap<>();

        Student student1 = new Student(1L, "Alice Johnson", "alice@example.com");
        Student student2 = new Student(2L, "Bob Smith", "bob@example.com");
        students.put(1L, student1);
        students.put(2L, student2);

        Instructor instructor1 = new Instructor(1L, "Dr. Emily Brown", "emily@example.com", "CS");
        Instructor instructor2 = new Instructor(2L, "Prof. Michael Lee", "michael@example.com", "Math");
        instructors.put(1L, instructor1);
        instructors.put(2L, instructor2);

        Course course1 = new Course(1L, "Introduction to Programming", "CS101", 1L, Arrays.asList(1L, 2L));
        Course course2 = new Course(2L, "Data Structures", "CS201", 1L, Arrays.asList(1L));
        Course course3 = new Course(3L, "Calculus", "MATH101", 2L, Collections.emptyList());
        courses.put(1L, course1);
        courses.put(2L, course2);
        courses.put(3L, course3);
    }

    @Test
    @DisplayName("Should return enrollments for existing student")
    void testGetStudentEnrollments_ExistingStudent() {
        // Arrange
        when(dataService.getStudentById(1L)).thenReturn(Optional.of(students.get(1L)));
        when(dataService.getCourses()).thenReturn(courses);

        // Act
        List<Enrollment> enrollments = lmsService.getStudentEnrollments(1L);

        // Assert
        assertThat(enrollments).isNotNull();
        assertThat(enrollments).hasSize(2);
        assertThat(enrollments.get(0).getStudentName()).isEqualTo("Alice Johnson");
        assertThat(enrollments.get(0).getCourseCode()).isEqualTo("CS101");

        verify(dataService, times(1)).getStudentById(1L);
        verify(dataService, times(1)).getCourses();
    }

    @Test
    @DisplayName("Should return empty list for non-existent student")
    void testGetStudentEnrollments_NonExistentStudent() {
        // Arrange
        when(dataService.getStudentById(99L)).thenReturn(Optional.empty());

        // Act
        List<Enrollment> enrollments = lmsService.getStudentEnrollments(99L);

        // Assert
        assertThat(enrollments).isEmpty();
        verify(dataService, never()).getCourses();
    }

    @Test
    @DisplayName("Should return active students with enrollments")
    void testGetActiveStudents() {
        // Arrange
        when(dataService.getCourses()).thenReturn(courses);
        when(dataService.getStudentById(1L)).thenReturn(Optional.of(students.get(1L)));
        when(dataService.getStudentById(2L)).thenReturn(Optional.of(students.get(2L)));

        // Act
        List<Student> activeStudents = lmsService.getActiveStudents();

        // Assert
        assertThat(activeStudents).hasSize(2);
        assertThat(activeStudents).extracting(Student::getName)
                .containsExactlyInAnyOrder("Alice Johnson", "Bob Smith");
    }

    @Test
    @DisplayName("Should return most active instructor based on enrollments")
    void testGetMostActiveInstructor() {
        // Arrange
        when(dataService.getCourses()).thenReturn(courses);
        when(dataService.getInstructorById(1L)).thenReturn(Optional.of(instructors.get(1L)));

        // Act
        Optional<Instructor> mostActive = lmsService.getMostActiveInstructor();

        // Assert
        assertThat(mostActive).isPresent();
        assertThat(mostActive.get().getName()).isEqualTo("Dr. Emily Brown");
    }

    @Test
    @DisplayName("Should return instructors with no enrollments")
    void testGetInstructorsWithNoEnrollments() {
        // Arrange
        when(dataService.getCourses()).thenReturn(courses);
        when(dataService.getInstructors()).thenReturn(instructors);

        // Act
        List<Instructor> inactiveInstructors = lmsService.getInstructorsWithNoEnrollments();

        // Assert
        assertThat(inactiveInstructors).hasSize(1);
        assertThat(inactiveInstructors.get(0).getName()).isEqualTo("Prof. Michael Lee");
    }

    @Test
    @DisplayName("Edge case: Student with no enrollments")
    void testGetStudentEnrollments_NoEnrollments() {
        // Arrange
        Student student3 = new Student(3L, "Carol Davis", "carol@example.com");
        students.put(3L, student3);

        when(dataService.getStudentById(3L)).thenReturn(Optional.of(student3));
        when(dataService.getCourses()).thenReturn(courses);

        // Act
        List<Enrollment> enrollments = lmsService.getStudentEnrollments(3L);

        // Assert
        assertThat(enrollments).isEmpty();
    }

    @Test
    @DisplayName("Edge case: Instructor with courses but no students")
    void testGetInstructorsWithNoEnrollments_EdgeCase() {
        // Arrange - Add instructor with course that has no students
        Instructor instructor3 = new Instructor(3L, "Dr. Sarah Wilson", "sarah@example.com", "Physics");
        instructors.put(3L, instructor3);

        Course course4 = new Course(4L, "PHYS101", "Physics Fundamentals", 3L, Collections.emptyList());
        courses.put(4L, course4);

        when(dataService.getCourses()).thenReturn(courses);
        when(dataService.getInstructors()).thenReturn(instructors);

        // Act
        List<Instructor> inactiveInstructors = lmsService.getInstructorsWithNoEnrollments();

        // Assert - Should include both instructor2 and instructor3
        assertThat(inactiveInstructors).hasSize(2);
        assertThat(inactiveInstructors).extracting(Instructor::getName)
                .containsExactlyInAnyOrder("Prof. Michael Lee", "Dr. Sarah Wilson");
    }
}