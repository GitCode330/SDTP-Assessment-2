package com.example.softwaretesting.service;

import com.example.softwaretesting.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LmsService {

    @Autowired
    private DataService dataService;

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Optional<Student> studentOpt = dataService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Student student = studentOpt.get();
        List<Enrollment> enrollments = new ArrayList<>();

        for (Course course : dataService.getCourses().values()) {
            if (course.getStudentIds().contains(studentId)) {
                Enrollment enrollment = new Enrollment(
                        studentId,
                        student.getName(),
                        course.getId(),
                        course.getName(),
                        course.getCode()
                );
                enrollments.add(enrollment);
            }
        }

        return enrollments;
    }

    public List<Student> getActiveStudents() {
        Set<Long> enrolledStudentIds = new HashSet<>();

        for (Course course : dataService.getCourses().values()) {
            enrolledStudentIds.addAll(course.getStudentIds());
        }

        return enrolledStudentIds.stream()
                .map(dataService::getStudentById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<Instructor> getMostActiveInstructor() {
        Map<Long, Integer> instructorEnrollmentCounts = new HashMap<>();

        for (Course course : dataService.getCourses().values()) {
            instructorEnrollmentCounts.merge(
                    course.getInstructorId(),
                    course.getStudentIds().size(),
                    Integer::sum
            );
        }

        return instructorEnrollmentCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> dataService.getInstructorById(entry.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public List<Instructor> getInstructorsWithNoEnrollments() {
        Set<Long> instructorsWithEnrollments = dataService.getCourses().values().stream()
                .filter(course -> !course.getStudentIds().isEmpty())
                .map(Course::getInstructorId)
                .collect(Collectors.toSet());

        return dataService.getInstructors().values().stream()
                .filter(instructor -> !instructorsWithEnrollments.contains(instructor.getId()))
                .collect(Collectors.toList());
    }
}