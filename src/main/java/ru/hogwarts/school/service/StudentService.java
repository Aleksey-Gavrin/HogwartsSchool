package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Student;

import java.util.*;

@Service
public class StudentService {

    private final Map<Long, Student> studentsMap = new HashMap<>();
    private static long count = 1L;

    public Student addStudent(Student student) {
        student.setId(count++);
        studentsMap.put(student.getId(), student);
        return student;
    }

    public Student findStudent(long id) {
        if (!studentsMap.containsKey(id)) {
            throw new ItemNotFoundException("Student not found");
        }
        return studentsMap.get(id);
    }

    public Student editStudentInfo(long id, Student student) {
        if (!studentsMap.containsKey(id)) {
            throw new ItemNotFoundException("Student not found");
        }
        studentsMap.put(id, student);
        return student;
    }

    public Student removeStudent(long id) {
        if (!studentsMap.containsKey(id)) {
            throw new ItemNotFoundException("Student not found");
        }
        return studentsMap.remove(id);
    }

    public Collection<Student> findByAge(int age) {
        if (age <= 0) {
            throw new ItemNotFoundException("invalid age input");
        }
        List<Student> result = new ArrayList<>();
        for (Student student: studentsMap.values()) {
            if (student.getAge() == age) {
                result.add(student);
            }
        }
        return result;
    }
}
