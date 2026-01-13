package com.example.lab4.service;

import com.example.lab4.config.RabbitConfig;
import com.example.lab4.dto.GradeEvent;
import com.example.lab4.entity.*;
import com.example.lab4.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GradeListener {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    // actively listens for new messages on grade-queue
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void receiveMessage(GradeEvent event) {
        System.out.println("Processing event: " + event);

        // DLQ logic
        if (event.getGrade() < 0) {
            throw new RuntimeException("Invalid grade! Sending to DLQ...");
        }

        // find entities
        Course course = courseRepository.findByCode(event.getCourseCode())
                .orElseThrow(() -> new RuntimeException("Course not found: " + event.getCourseCode()));

        Student student = studentRepository.findByCode(event.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Student not found: " + event.getStudentCode()));

        // only store if COMPULSORY
        if (course.getType() == CourseType.COMPULSORY) {
            Grade grade = new Grade();
            grade.setStudent(student);
            grade.setCourse(course);
            grade.setValue(event.getGrade());
            gradeRepository.save(grade);
            System.out.println("Saved Compulsory Grade.");
        } else {
            System.out.println("Ignored Optional Course Grade.");
        }
    }

    // catches messages that land in the DLQ and prints an alert
    @RabbitListener(queues = RabbitConfig.DLQ_NAME)
    public void processFailedMessages(GradeEvent failedEvent) {
        System.err.println("ALERT: Message moved to DLQ: " + failedEvent);
    }
}
//package com.example.lab4.service;
//
//import com.example.lab4.dto.GradeEvent;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class GradeListener {
//
//    // matches the queue name defined in quickgrade
//    @RabbitListener(queues = "grade-queue")
//    public void receiveMessage(GradeEvent event) {
//        System.out.println("Received Grade Update: " + event);
//    }
//}