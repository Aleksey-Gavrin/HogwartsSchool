package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Student not found")
        );
    }

    public Student editStudentInfo(long id, Student student) {
        Student existingStudent = findStudent(id);
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        studentRepository.save(existingStudent);
        return existingStudent;
    }

    public Student removeStudent(long id) {
        Student existingStudent = findStudent(id);
        studentRepository.deleteById(id);
        return existingStudent;
    }

    public Collection<Student> findByAge(int age) {
        if (age <= 0) {
            throw new ItemNotFoundException("invalid age input");
        }
        return studentRepository.findAllByAge(age);
    }
}
