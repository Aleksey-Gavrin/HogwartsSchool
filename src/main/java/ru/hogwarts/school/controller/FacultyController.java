package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public Faculty getFacultyInfo(@PathVariable long id) {
        return facultyService.findFaculty(id);
    }

    @GetMapping("/{id}/students")
    public List<Student> getAllStudentsByFacultyId(@PathVariable long id) {
        return facultyService.findFaculty(id).getStudents();
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping("/{id}")
    public Faculty editFacultyInfo(@PathVariable long id, @RequestBody Faculty faculty) {
        return facultyService.editFaculty(id, faculty);
    }

    @DeleteMapping("/{id}")
    public Faculty deleteFaculty(@PathVariable long id) {
        return facultyService.removeFaculty(id);
    }

    @GetMapping("/filteredByColor")
    public Collection<Faculty> getAllFacultiesByColor(@RequestParam String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping("/filteredByNameOrColor")
    public Collection<Faculty> getAllFacultiesByNameOrColor(@RequestParam (required = false) String name,
                                                            @RequestParam (required = false) String color) {
        return facultyService.findAllByNameOrColor(name, color);
    }
}
