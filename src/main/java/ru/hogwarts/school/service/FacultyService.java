package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.*;

@Service
public class FacultyService {

    private final Map<Long, Faculty> facultyMap = new HashMap<>();
    private static long count = 1L;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(count++);
        facultyMap.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty findFaculty(long id) {
        if (!facultyMap.containsKey(id)) {
            throw new ItemNotFoundException("Faculty not found");
        }
        return facultyMap.get(id);
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        if (!facultyMap.containsKey(id)) {
            throw new ItemNotFoundException("Faculty not found");
        }
        facultyMap.put(id, faculty);
        return faculty;
    }

    public Faculty removeFaculty(long id) {
        if (!facultyMap.containsKey(id)) {
            throw new ItemNotFoundException("Faculty not found");
        }
        return facultyMap.remove(id);
    }

    public Collection<Faculty> findByColor(String color) {
        if (color.isBlank()) {
            throw new ItemNotFoundException("invalid color input");
        }
        List<Faculty> result = new ArrayList<>();
        for (Faculty faculty: facultyMap.values()) {
            if (faculty.getColor().equals(color)) {
                result.add(faculty);
            }
        }
        return result;
    }
}
