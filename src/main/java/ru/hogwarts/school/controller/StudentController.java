package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public Student getStudentInfo(@PathVariable long id) {
        return studentService.findStudent(id);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getFacultyByStudentId(@PathVariable long id) {
        return studentService.findStudent(id).getFaculty();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
       return studentService.addStudent(student);
    }

    @PutMapping("/{id}")
    public Student editStudentInfo(@PathVariable long id, @RequestBody Student student) {
        return studentService.editStudentInfo(id, student);
    }

    @DeleteMapping("/{id}")
    public Student deleteStudent(@PathVariable long id) {
        return studentService.removeStudent(id);
    }

    @GetMapping("/filteredByAge")
    public Collection<Student> getAllStudentsByAge(@RequestParam("age") int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/filteredByAgeBetween")
    public Collection<Student> getAllStudentsByAgeBetween(@RequestParam("ageMin") int min,
                                                          @RequestParam("ageMax") int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/getQty")
    public long getStudentsQty() {
        return studentService.getStudentsQty();
    }

    @GetMapping("/getAvgAge")
    public double getStudentsAvgAge() {
        return studentService.getStudentsAvgAge();
    }

    @GetMapping("/getLastAdded")
    public List<Student> getLastAddedStudents(@RequestParam("quantity") int quantity) {
        return studentService.getLastAddedStudents(quantity);
    }
}
