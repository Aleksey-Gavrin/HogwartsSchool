package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable long id) {
        return studentService.findStudent(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
       return studentService.addStudent(student);
    }

    @PutMapping("{id}")
    public Student editStudentInfo(@PathVariable long id, @RequestBody Student student) {
        return studentService.editStudentInfo(id, student);
    }

    @DeleteMapping("{id}")
    public Student deleteStudent(@PathVariable long id) {
        return studentService.removeStudent(id);
    }

    @GetMapping
    public Collection<Student> getAllStudentsByAge(@RequestParam int age) {
        return studentService.findByAge(age);
    }
}
