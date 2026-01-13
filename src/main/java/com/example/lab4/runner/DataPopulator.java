package com.example.lab4.runner;

import com.example.lab4.dto.StudentPreferenceDTO;
import com.example.lab4.entity.*;
import com.example.lab4.repository.GradeRepository;
import com.example.lab4.repository.InstructorPreferenceRepository;
import com.example.lab4.repository.PersonRepository;
import com.example.lab4.service.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPopulator implements CommandLineRunner {

    // Services
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final PackService packService;
    private final CourseService courseService;
    private final StudentPreferenceService studentPreferenceService;

    // Repositories (Direct access needed for specific entities not covered by services)
    private final PersonRepository personRepository;
    private final GradeRepository gradeRepository;
    private final InstructorPreferenceRepository instructorPreferenceRepository;

    // Utils
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random random = new Random();

        // ==========================================
        // 1. GENERAL DATA POPULATION (Your Original Code)
        // ==========================================

        // 1. Create an Admin User
        if (personRepository.findByEmail("admin@test.com").isEmpty()) {
            Person admin = new Person(
                    "Administrator",
                    "admin@test.com",
                    passwordEncoder.encode("admin"),
                    Role.ADMIN
            );
            personRepository.save(admin);
            log.info("Created Admin user: admin@test.com / admin");
        }

        // 2. Create Instructors
        List<Instructor> instructors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String email = faker.internet().emailAddress();
            if (personRepository.findByEmail(email).isEmpty()) {
                Instructor instructor = new Instructor(
                        faker.name().fullName(),
                        email,
                        passwordEncoder.encode("password")
                );
                instructors.add(instructorService.createInstructor(instructor));
            }
        }
        log.info("Created {} instructors", instructors.size());

        // 3. Create Packs
        List<Pack> packs = new ArrayList<>();
        for (int year = 1; year <= 3; year++) {
            for (String semester : List.of("Fall", "Spring")) {
                Pack pack = new Pack();
                pack.setYear(year);
                pack.setSemester(semester);
                pack.setName(String.format("Pack-%d-%s", year, semester));
                packs.add(packService.createPack(pack));
            }
        }
        log.info("Created {} packs", packs.size());

        // 4. Create Courses
        String[] courseNames = {"Algorithms", "Databases", "Web Development",
                "Machine Learning", "Networks", "Security"};

        if (!instructors.isEmpty()) {
            for (int i = 0; i < 30; i++) {
                Course course = new Course();
                course.setCode("CS" + (1000 + i));
                course.setAbbr("CS" + i);
                course.setName(courseNames[random.nextInt(courseNames.length)] + " " + i);
                course.setType(random.nextBoolean() ? CourseType.OPTIONAL : CourseType.COMPULSORY);
                course.setInstructor(instructors.get(random.nextInt(instructors.size())));

                if (course.getType() == CourseType.OPTIONAL) {
                    course.setPack(packs.get(random.nextInt(packs.size())));
                }

                course.setGroupCount(random.nextInt(3) + 1);
                course.setDescription(faker.lorem().sentence(10));
                courseService.createCourse(course);
            }
            log.info("Created 30 random courses");
        }

        // 5. Create Students
        for (int i = 0; i < 100; i++) {
            String email = faker.internet().emailAddress();
            if (personRepository.findByEmail(email).isEmpty()) {
                Student student = new Student(
                        "ST" + String.format("%04d", i),
                        faker.name().fullName(),
                        email,
                        passwordEncoder.encode("password"),
                        random.nextInt(3) + 1
                );
                studentService.createStudent(student);
            }
        }
        log.info("Created 100 random students");

        // 6. Create Student Preferences (Random)
        List<Student> allStudents = studentService.findAll();
        List<Course> allCourses = courseService.findAll();
        int prefCount = 0;

        if (!allStudents.isEmpty() && !allCourses.isEmpty() && !packs.isEmpty()) {
            for (int i = 0; i < 50; i++) {
                try {
                    Student s = allStudents.get(random.nextInt(allStudents.size()));
                    Course c = allCourses.get(random.nextInt(allCourses.size()));
                    Pack p = packs.get(random.nextInt(packs.size()));

                    StudentPreferenceDTO dto = new StudentPreferenceDTO();
                    dto.setStudentId(s.getId());
                    dto.setCourseId(c.getId());
                    dto.setPreferenceRank(random.nextInt(5) + 1);
                    dto.setPackName(p.getName());

                    studentPreferenceService.create(dto);
                    prefCount++;
                } catch (Exception e) {
                    // ignore duplicates
                }
            }
        }
        log.info("Created {} random student preferences", prefCount);

        // ==========================================
        // 2. HOMEWORK SPECIFIC DATA (The Battle for Python)
        // ==========================================
        log.info("--- Setting up Homework Scenario (Alice vs Bob) ---");

        if (studentService.findByCode("S01").isEmpty()) {

            // A. Create Instructor for Homework
            Instructor drSmith = new Instructor("Dr. Smith", "smith@university.com", passwordEncoder.encode("pass"));
            if(personRepository.findByEmail("smith@university.com").isEmpty()) {
                drSmith = instructorService.createInstructor(drSmith);
            } else {
                drSmith = (Instructor) personRepository.findByEmail("smith@university.com").get();
            }

            // B. Create Students
            Student alice = new Student("S01", "Alice", "alice@test.com", passwordEncoder.encode("pass"), 3);
            Student bob = new Student("S02", "Bob", "bob@test.com", passwordEncoder.encode("pass"), 3);
            studentService.createStudent(alice);
            studentService.createStudent(bob);

            // C. Create Pack
            Pack packA = new Pack();
            packA.setName("Pack A");
            packA.setSemester("1");
            packA.setYear(3);
            packA = packService.createPack(packA);

            // D. Create Courses
            // Compulsory: Java
            Course javaCourse = new Course();
            javaCourse.setAbbr("JAVA");
            javaCourse.setCode("CS101_HW");
            javaCourse.setName("Java Programming");
            javaCourse.setType(CourseType.COMPULSORY);
            javaCourse.setPack(packA);
            javaCourse.setInstructor(drSmith);
            courseService.createCourse(javaCourse);

            // Optional: Python (Capacity 1)
            Course pythonCourse = new Course();
            pythonCourse.setAbbr("PY");
            pythonCourse.setCode("CS102_HW");
            pythonCourse.setName("Python Scripting");
            pythonCourse.setType(CourseType.OPTIONAL);
            pythonCourse.setGroupCount(1); // Capacity 1 to force competition
            pythonCourse.setPack(packA);
            pythonCourse.setInstructor(drSmith);
            courseService.createCourse(pythonCourse);

            // E. Create Grades (Alice=10, Bob=5)
            // Alice
            Grade g1 = new Grade();
            g1.setStudent(studentService.findByCode("S01").get());
            g1.setCourse(javaCourse);
            g1.setValue(10.0);
            gradeRepository.save(g1);

            // Bob
            Grade g2 = new Grade();
            g2.setStudent(studentService.findByCode("S02").get());
            g2.setCourse(javaCourse);
            g2.setValue(5.0);
            gradeRepository.save(g2);

            // F. Create Instructor Preference (Weighted Logic)
            InstructorPreference pref = new InstructorPreference();
            pref.setOptionalCourse(pythonCourse);
            pref.setCompulsoryCourse(javaCourse);
            pref.setWeight(100.0);
            instructorPreferenceRepository.save(pref);

            // G. Create Student Preferences (Both want Python)
            StudentPreferenceDTO p1 = new StudentPreferenceDTO();
            p1.setStudentId(alice.getId());
            p1.setCourseId(pythonCourse.getId());
            p1.setPreferenceRank(1);
            p1.setPackName("Pack A");
            studentPreferenceService.create(p1);

            StudentPreferenceDTO p2 = new StudentPreferenceDTO();
            p2.setStudentId(bob.getId());
            p2.setCourseId(pythonCourse.getId());
            p2.setPreferenceRank(1);
            p2.setPackName("Pack A");
            studentPreferenceService.create(p2);

            log.info("Homework Scenario Created: Alice (10.0) vs Bob (5.0) for Python.");
        } else {
            log.info("Homework Scenario already exists. Skipping.");
        }

        // ==========================================
        // 3. RUN OPERATIONS TESTS (Your Original Code)
        // ==========================================
        testOperations();
    }

    private void testOperations() {
        log.info("\n=== Testing CRUD Operations ===");

        // Test pagination
        Page<Student> studentsPage = studentService.findAllPaginated(0, 10, "name");
        log.info("Page 1 of students: {} students, total pages: {}",
                studentsPage.getNumberOfElements(), studentsPage.getTotalPages());

        // Test derived query
        List<Student> yearOneStudents = studentService.findByYear(1);
        log.info("Year 1 students: {}", yearOneStudents.size());

        // Test JPQL query
        List<Course> optionalCourses = courseService.findByType(CourseType.OPTIONAL);
        log.info("Optional courses: {}", optionalCourses.size());

        // Test transactional modifying query (Using a safer year range)
        int promoted = studentService.promoteStudents(1, 2);
        log.info("Promoted {} students from year 1 to year 2", promoted);

        // Test Specifications
        Page<Course> searchResults = courseService.searchCourses(
                CourseType.OPTIONAL, 2, null, "Algorithm", 0, 10
        );
        log.info("Search results: {} courses found", searchResults.getTotalElements());
    }
}