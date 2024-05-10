package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        logger.debug("FacultyRepository Bean is created");
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Invoked method: addFaculty()");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Invoked method: findFaculty()");
        return facultyRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Faculty not found");
                    return new ItemNotFoundException();
                }
        );
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        logger.info("Invoked method: editFaculty()");
        Faculty existingFaculty = findFaculty(id);
        existingFaculty.setName(faculty.getName());
        existingFaculty.setColor(faculty.getColor());
        facultyRepository.save(existingFaculty);
        logger.debug("Data for faculty with id = " + id + " successfully changed");
        return existingFaculty;
    }

    public Faculty removeFaculty(long id) {
        logger.info("Invoked method: removeFaculty()");
        Faculty existingFaculty = findFaculty(id);
        facultyRepository.deleteById(id);
        logger.debug("Faculty with id = " + id + " successfully deleted");
        return existingFaculty;
    }

    public Collection<Faculty> findByColor(String color) {
        logger.info("Invoked method: findByColor()");
        if (color.isBlank()) {
            logger.error("Invalid color input");
            throw new ItemNotFoundException();
        }
        return facultyRepository.findAllByColor(color);
    }

    public Collection<Faculty> findAllByNameOrColor(String name, String color) {
        logger.info("Invoked method: findAllByNameOrColor()");
        if ((name != null && name.isBlank()) || (color != null && color.isBlank())) {
            logger.error("Invalid name or color input");
            throw new ItemNotFoundException();
        }
        return facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }
}
