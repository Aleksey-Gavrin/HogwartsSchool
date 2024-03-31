package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Faculty not found")
        );
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        Faculty existingFaculty = findFaculty(id);
        existingFaculty.setName(faculty.getName());
        existingFaculty.setColor(faculty.getColor());
        facultyRepository.save(existingFaculty);
        return existingFaculty;
    }

    public Faculty removeFaculty(long id) {
        Faculty existingFaculty = findFaculty(id);
        facultyRepository.deleteById(id);
        return existingFaculty;
    }

    public Collection<Faculty> findByColor(String color) {
        if (color.isBlank()) {
            throw new ItemNotFoundException("invalid color input");
        }
        return facultyRepository.findAllByColor(color);
    }

    public Collection<Faculty> findAllByNameOrColor(String name, String color) {
        return facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }
}
