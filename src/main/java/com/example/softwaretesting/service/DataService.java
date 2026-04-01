package com.example.softwaretesting.service;

import com.example.softwaretesting.model.Course;
import com.example.softwaretesting.model.Instructor;
import com.example.softwaretesting.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DataService {

    private final Map<Long, Student> students = new HashMap<>();
    private final Map<Long, Instructor> instructors = new HashMap<>();
    private final Map<Long, Course> courses = new HashMap<>();

    private final AtomicLong studentIdGenerator = new AtomicLong(1);
    private final AtomicLong instructorIdGenerator = new AtomicLong(1);
    private final AtomicLong courseIdGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // Create sample students
        Student student1 = new Student(studentIdGenerator.getAndIncrement(), "Alice Johnson", "alice@example.com");
        Student student2 = new Student(studentIdGenerator.getAndIncrement(), "Bob Smith", "bob@example.com");
        Student student3 = new Student(studentIdGenerator.getAndIncrement(), "Carol Davis", "carol@example.com");
        Student student4 = new Student(studentIdGenerator.getAndIncrement(), "David Wilson", "david@example.com");

        students.put(student1.getId(), student1);
        students.put(student2.getId(), student2);
        students.put(student3.getId(), student3);
        students.put(student4.getId(), student4);

        // Create sample instructors
        Instructor instructor1 = new Instructor(instructorIdGenerator.getAndIncrement(), "Dr. Emily Brown", "emily.brown@example.com", "Computer Science");
        Instructor instructor2 = new Instructor(instructorIdGenerator.getAndIncrement(), "Prof. Michael Lee", "michael.lee@example.com", "Mathematics");
        Instructor instructor3 = new Instructor(instructorIdGenerator.getAndIncrement(), "Dr. Sarah Wilson", "sarah.wilson@example.com", "Physics");
        Instructor instructor4 = new Instructor(instructorIdGenerator.getAndIncrement(), "Prof. Robert Chen", "robert.chen@example.com", "Biology");

        instructors.put(instructor1.getId(), instructor1);
        instructors.put(instructor2.getId(), instructor2);
        instructors.put(instructor3.getId(), instructor3);
        instructors.put(instructor4.getId(), instructor4);

        // Create sample courses with enrollments
        Course course1 = new Course(courseIdGenerator.getAndIncrement(), "Introduction to Programming", "CS101", instructor1.getId(),
                Arrays.asList(1L, 2L, 3L));
        Course course2 = new Course(courseIdGenerator.getAndIncrement(), "Data Structures", "CS201", instructor1.getId(),
                Arrays.asList(1L, 2L));
        Course course3 = new Course(courseIdGenerator.getAndIncrement(), "Calculus I", "MATH101", instructor2.getId(),
                Arrays.asList(2L, 3L));
        Course course4 = new Course(courseIdGenerator.getAndIncrement(), "Physics Fundamentals", "PHYS101", instructor3.getId(),
                Arrays.asList());
        // student4 not have enrollments

        courses.put(course1.getId(), course1);
        courses.put(course2.getId(), course2);
        courses.put(course3.getId(), course3);
        courses.put(course4.getId(), course4);
    }

    public Map<Long, Student> getStudents() {
        return students;
    }

    public Map<Long, Instructor> getInstructors() {
        return instructors;
    }

    public Map<Long, Course> getCourses() {
        return courses;
    }

    public Optional<Student> getStudentById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    public Optional<Instructor> getInstructorById(Long id) {
        return Optional.ofNullable(instructors.get(id));
    }

    public Optional<Course> getCourseById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }
}