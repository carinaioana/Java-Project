//package com.example.lab4.runner;
//
//import com.example.lab4.entity.Student;
//import com.example.lab4.repository.StudentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class SimpleTestRunner implements CommandLineRunner {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("--- CommandLineRunner START ---");
//
//        System.out.println("Saving new student...");
//        Student newStudent = new Student(
//                "S12345",
//                "Carina Sirbu",
//                "csirbu@yahoo.com",
//                3
//        );
//        studentRepository.save(newStudent);
//
//        System.out.println("Fetching all students...");
//        List<Student> students = studentRepository.findAll();
//
//        if (students.isEmpty()) {
//            System.out.println("No students found in the database.");
//        } else {
//            System.out.println("Current students in database:");
//            students.forEach(student -> {
//                System.out.println(" -> " + student.toString());
//            });
//        }
//
////         studentRepository.deleteAll();
////         System.out.println("Cleaned up students table.");
//
//        System.out.println("--- CommandLineRunner END ---");
//    }
//}