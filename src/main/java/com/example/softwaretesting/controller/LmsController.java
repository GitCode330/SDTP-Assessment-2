package com.example.softwaretesting.controller;

import com.example.softwaretesting.model.*;
import com.example.softwaretesting.service.LmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LmsController {

    @Autowired
    private LmsService lmsService;

    @GetMapping("/students/{studentId}/enrollments")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(@PathVariable Long studentId) {
        List<Enrollment> enrollments = lmsService.getStudentEnrollments(studentId);

        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/students/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        List<Student> activeStudents = lmsService.getActiveStudents();

        return ResponseEntity.ok(activeStudents);
    }

    @GetMapping("/instructors/most-active")
    public ResponseEntity<Instructor> getMostActiveInstructor() {
        return lmsService.getMostActiveInstructor()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/instructors/inactive")
    public ResponseEntity<List<Instructor>> getInstructorsWithNoEnrollments() {
        List<Instructor> inactiveInstructors = lmsService.getInstructorsWithNoEnrollments();

        return ResponseEntity.ok(inactiveInstructors);
    }
}