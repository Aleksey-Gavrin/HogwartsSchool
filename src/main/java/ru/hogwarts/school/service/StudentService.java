package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;

@Service
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        logger.debug("StudentRepository Bean is created");
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Invoked method: addStudent()");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Invoked method: findStudent()");
        return studentRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Student with id = " + id + " not found.");
                    return new ItemNotFoundException();
                }
        );
    }

    public Student editStudentInfo(long id, Student student) {
        logger.info("Invoked method: editStudentInfo()");
        Student existingStudent = findStudent(id);
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        studentRepository.save(existingStudent);
        logger.debug("Data for student with id = " + id + " successfully changed");
        return existingStudent;
    }

    public Student removeStudent(long id) {
        logger.info("Invoked method: removeStudent()");
        Student studentToDelete = findStudent(id);
        studentRepository.deleteById(id);
        logger.debug("Student with id = " + id + " successfully deleted");
        return studentToDelete;
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Invoked method: findByAge()");
        if (age <= 0) {
            logger.error("Invalid age input");
            throw new ItemNotFoundException();
        }
        return studentRepository.findAllByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Invoked method: findByAgeBetween()");
        if (min < 0 || max <= 0 || min > max) {
            logger.error("Invalid age input");
            throw new ItemNotFoundException();
        }
        return studentRepository.findAllByAgeBetween(min, max);
    }

    public long getStudentsQty() {
        logger.info("Invoked method: getStudentsQty()");
        return studentRepository.getStudentsQty();
    }

    public double getStudentsAvgAge() {
        logger.info("Invoked method: getStudentsAvgAge()");
        return studentRepository.getStudentsAvgAge();
    }

    public List<Student> getLastAddedStudents(int quantity) {
        logger.info("Invoked method: getLastAddedStudents()");
        return studentRepository.getLastAddedStudents(quantity);
    }

    public void printParallel() {
        logger.info("Invoked method: printParallel()");
        List<Student> students = studentRepository.findAll();

        System.out.println("--".repeat(15));

        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread t1 = new Thread(() ->
        {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        }
        );

        Thread t2 = new Thread(() ->
        {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        }
        );

        t1.start();
        t2.start();
    }

    public void printParallelSync() {
        logger.info("Invoked method: printParallelSync()");
        List<Student> students = studentRepository.findAll();

        System.out.println("--".repeat(15));

        printSync(students.get(0).getName());
        printSync(students.get(1).getName());

        Thread t1 = new Thread(() ->
        {
            printSync(students.get(2).getName());
            printSync(students.get(3).getName());
        }
        );

        Thread t2 = new Thread(() ->
        {
            printSync(students.get(4).getName());
            printSync(students.get(5).getName());
        }
        );

        t1.start();
        t2.start();
    }

    private synchronized void printSync(String str) {
        System.out.println(str);
    }
}
