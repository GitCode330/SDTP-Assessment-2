package com.example.softwaretesting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private String courseCode;
}