Based on the code provided and the fixes implemented, here is the explanation of how the two components work.

1. What MatchingIntegrationService Does

This service acts as the bridge and data preparer. Its main job is to translate raw database data (students, grades, preferences) into a mathematical problem that the algorithm can solve.

It performs three key steps in prepareRequestForPack:

Grouping Student Preferences: It looks at student_preferences to see which courses each student wants. It groups these by student code (e.g., "S01" wants "PY").

Calculating Ranks (The Homework Logic): Before sending data to the solver, it decides who deserves the spot more.

It looks up Instructor Preferences (e.g., "For Python, Java grade counts 100%").

It fetches the student's Grades.

It calculates a Weighted Score for every student interested in a course using the calculateStudentScore method.

It sorts the students based on this score. The student with the highest score is placed at the top of the studentRankings list for that course.

Sending the Request: It constructs a MatchRequest object containing:

Students: A list of students and their preferred courses.

Courses: A list of courses, their Capacity (strictly derived from group count), and the Ranked List of students (sorted by grades). It then sends this JSON payload to the StableMatch service using a REST call with resilience patterns (Retry, Circuit Breaker).

2. How the Algorithm Works (StableMatchService)

The algorithm implemented is the Gale-Shapley (Deferred Acceptance) Algorithm. It is "student-proposing," meaning students try to get their top choices, and courses "hold" onto the best students they have seen so far.

Here is the lifecycle inside the solve method:

The Queue: All students start in a queue of "free" students.

The Proposal: The first student in the queue proposes to their favorite course.

The Decision (Course Side):

Case A (Empty Seat): If the course has currentStudents.size() < capacity, the student is tentatively accepted.

Case B (Full - The Battle): If the course is full, the course compares the new student against the worst-ranked student currently holding a seat.

It uses the studentRankings list provided by the Integration Service (which is based on grades).

If New Student is Better: The algorithm kicks out the worst student (who goes back to the free queue) and accepts the new student.

If New Student is Worse: The new student is rejected and stays in the queue to try their next preferred course.

Termination: This repeats until the queue is empty or every unassigned student has been rejected by all their preferred courses.

In Summary:

MatchingIntegrationService determines "Who is better?" (by calculating grades/weights).

StableMatchService determines "Who gets in?" (by resolving conflicts using those rankings and course capacities).

---------------------------------------

### 1. Database Storage for Grades

What was added:

- Entity: A Grade class marked with @Entity to define the database structure.

- Repository: A GradeRepository interface to handle database operations.

- Logic: A check inside the GradeListener to filter courses by type.

How it works:

- Table Creation: The Grade entity defines a table named grades with columns for value, student_id, and course_id.

- Filtering Logic: Inside GradeListener.java, when a message arrives from RabbitMQ, the code looks up the course using courseRepository. It specifically checks if (course.getType() == CourseType.COMPULSORY).

  -   If True: The grade is saved to the grades table via the repository.

  -   If False: The grade is ignored (printed to console but not saved).

### 2. REST Endpoints (GET & CSV Upload)

What was added:

- Controller: GradeController.java with two specific endpoints.

How it works:

- Get All Grades: The @GetMapping endpoint simply calls gradeRepository.findAll() to return the list of persisted grades as JSON.

- CSV Upload: The @PostMapping("/upload") endpoint accepts a file. It uses a BufferedReader to read the CSV line-by-line.

- Parsing: It splits each line by commas to get studentCode, courseCode, and grade.

- Validation: It looks up the Student and Course entities by their codes.

- Compulsory Check: Just like the listener, it verifies c.getType() == CourseType.COMPULSORY before saving the record to the database.

### 3. Dead-Letter Queue (DLQ) Implementation

What was added:

- Configuration (RabbitConfig.java): Definitions for a "Dead Letter Exchange" (DLX), a "Dead Letter Queue" (DLQ), and the arguments linking the main queue to them.

- Failure Logic (GradeListener.java): Code to artificially trigger a failure and a listener to handle the dead letters.

How it works:

- Infrastructure (Wiring):

In RabbitConfig, the main queue() is defined with special arguments: x-dead-letter-exchange (set to grade.dlx) and x-dead-letter-routing-key (set to deadLetter).

This tells RabbitMQ: "If a message in this queue is rejected or fails processing, send it to grade.dlx with the routing key deadLetter."

- Triggering the DLQ:

In GradeListener.receiveMessage, there is logic that checks: if (event.getGrade() < 0).

If the grade is negative, it throws a RuntimeException("Invalid grade!..."). When this exception is thrown, Spring AMQP rejects the message, and RabbitMQ automatically moves it to the DLQ because of the configuration above.

- Handling the Dead Letter:

A second method, processFailedMessages, is annotated with @RabbitListener(queues = RabbitConfig.DLQ_NAME). It listens specifically to the DLQ and prints an alert (ALERT: Message moved to DLQ...) when a failed message arrives.


### DLX stands for Dead Letter Exchange.

It acts as a safety net for messages that cannot be processed successfully by your main queue. Instead of discarding these failed messages, RabbitMQ redirects them to this specific exchange so they can be handled later (e.g., logged, retried, or inspected).

How it works in your project

Based on your code, here is the flow:

- The Setup: In your RabbitConfig.java, you defined the DLX name as "grade.dlx".

- Linking: You configured your main queue (grade-queue) with an argument x-dead-letter-exchange pointing to this DLX. This tells RabbitMQ: "If any message in this queue fails, send it to the DLX".

- The Failure: In GradeListener.java, you explicitly trigger this by throwing an exception when a grade is negative (throw new RuntimeException("Invalid grade!...")).

- The Redirection: When that exception occurs, the message is rejected. RabbitMQ automatically moves it from the main queue -> DLX -> Dead Letter Queue (DLQ) (grade-queue.dlq).

- The Handling: Your second listener (processFailedMessages) watches the DLQ and prints the alert ALERT: Message moved to DLQ.

- Without a DLX, if your code threw an error, the message would either be lost forever or stuck in an infinite retry loop, blocking other messages.

### Grade Processing Flow


    User([User])
    QuickGrade([QuickGrade App])
    RabbitMQ([RabbitMQ])
    Lab4([Lab4 App])
    DLQ([DLQ Handler])
    
    User -->|POST /grade| QuickGrade
    QuickGrade -->|Publish to 'grade-exchange'| RabbitMQ
    RabbitMQ -->|Route to 'grade-queue'| Lab4

    Lab4 -->|Grade < 0?| BadGrade{Grade < 0?}

    BadGrade -->|Yes| Exception[Throw Exception]
    Exception --> DLQ

    BadGrade -->|No| Find[Find Student & Course in DB]

    Find --> IsComp{Course COMPULSORY?}

    IsComp -->|Yes| Save[Save to 'grades' Table]
    IsComp -->|No| Ignore[Log "Ignored"]


            ┌──────────────┐
            │    Person    │ (Abstract)
            │──────────────│
            │ - name       │
            │ - email      │
            └──────────────┘
                     △
            ┌────────┴────────┐
            │                 │
    ┌───────┴──────┐   ┌──────┴────────┐
    │   Student    │   │  Instructor   │
    │──────────────│   │───────────────│
    │ - id         │   │ - id          │
    │ - code       │   │ - courses     │
    │ - year       │   └───────┬───────┘
    │ - courses    │           │
    └──────┬───────┘           │ @OneToMany // one instructor has many courses
           │                   │
           │ @ManyToMany       │
           │                   │
    ┌──────┴──────────────────-┴──────┐
    │          Course                 │
    │─────────────────────────────────│
    │ - id                            │
    │ - code, name, abbr              │
    │ - type (COMPULSORY/OPTIONAL)    │
    │ - groupCount                    │
    │ - description                   │
    │ - instructor  (@ManyToOne)      │
    │ - pack        (@ManyToOne)      │
    │ - students    (@ManyToMany)     │
    └─────────────────┬───────────────┘
                      │ @ManyToOne
                      │
                ┌─────┴─────┐
                │   Pack    │
                │───────────│
                │ - id      │
                │ - year    │
                │ - semester│
                │ - name    │
                │ - courses │
                └───────────┘

    ┌─────────────────────────────────────────────────────────────┐
    │                      CONTROLLER                              │
    │  "I handle HTTP requests and responses"                     │
    │                                                              │
    │  - Receives HTTP requests                                   │
    │  - Converts DTOs ↔ Entities                                 │
    │  - Calls service methods                                    │
    │  - Returns HTTP responses                                   │
    │  - NO business logic                                        │
    │  - NO database access                                       │
    └──────────────────────┬──────────────────────────────────────┘
    │
    ▼
    ┌─────────────────────────────────────────────────────────────┐
    │                       SERVICE                                │
    │  "I enforce business rules and manage transactions"         │
    │                                                              │
    │  - Validates data                                           │
    │  - Enforces business rules                                  │
    │  - Manages transactions                                     │
    │  - Coordinates multiple repositories                        │
    │  - Throws business exceptions                               │
    │  - NO HTTP knowledge                                        │
    └──────────────────────┬──────────────────────────────────────┘
    │
    ▼
    ┌─────────────────────────────────────────────────────────────┐
    │                     REPOSITORY                               │
    │  "I just save and retrieve data"                            │
    │                                                              │
    │  - Talks to database                                        │
    │  - CRUD operations                                          │
    │  - Custom queries                                           │
    │  - NO business logic                                        │
    │  - NO validation                                            │
    └─────────────────────────────────────────────────────────────┘

### **Example Flow: Creating a Student**
```
1. HTTP Request arrives
   POST /api/students
   Body: {"code": "ST9999", "name": "John", ...}
        ↓
        
2. Controller receives request
   @PostMapping("/api/students")
   public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO dto) {
       Student student = mapper.toEntity(dto);  // Convert DTO to Entity
       Student created = studentService.createStudent(student);  // ⭐ Call service
       return ResponseEntity.ok(mapper.toDTO(created));
   }
        ↓
        
3. Service performs business logic
   @Transactional  // ⭐ Transaction starts
   public Student createStudent(Student student) {
       
       // ✅ Check 1: Code unique?
       if (exists) throw DuplicateException;
       
       // ✅ Check 2: Email unique?
       if (exists) throw DuplicateException;
       
       // ✅ Check 3: Year valid?
       if (invalid) throw IllegalArgumentException;
       
       // All checks passed! Save to database
       return studentRepository.save(student);  // ⭐ Call repository
       
   }  // ⭐ Transaction ends (COMMIT if successful)
        ↓
        
4. Repository saves to database
   public interface StudentRepository extends JpaRepository<Student, Long> {
       // save() is inherited from JpaRepository
   }
        ↓
        
5. Hibernate generates SQL
   INSERT INTO students (code, name, email, year) 
   VALUES ('ST9999', 'John', 'john@example.com', 1)
        ↓
        
6. PostgreSQL executes SQL
   Returns generated ID (e.g., 101)
        ↓
        
7. Response flows back
   Database → Hibernate → Repository → Service → Controller → HTTP
   
   HTTP/1.1 201 Created
   {"id": 101, "code": "ST9999", "name": "John", ...}

# How ETag works
First Request: The server returns the resource with an ETag header (e.g., "1").

Subsequent Request: The client sends the If-None-Match: "1" header.

Server Check: request.checkNotModified(etag) returns true if the version hasn't changed.

Response: The server responds with 304 Not Modified and an empty body, saving bandwidth.