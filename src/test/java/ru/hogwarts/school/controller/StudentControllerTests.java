package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    ObjectMapper objectMapper = new ObjectMapper();

    Student testStudent;

    @BeforeEach
    void init() {

        Student testStudent1 = new Student(1L, "Harry", 15);
        Student testStudent2 = new Student(2L, "Hermione", 18);
        Student testStudent3 = new Student(3L, "Ron", 20);
        testStudent = testStudent1;

        Faculty testFaculty = new Faculty(1L, "Gryffindor", "Red");

        testStudent1.setFaculty(testFaculty);
        testStudent2.setFaculty(testFaculty);
        testStudent3.setFaculty(testFaculty);

        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(testStudent1);
        when(studentRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(testStudent1));
        ArrayList<Student> studentArrayList1 = new ArrayList<>(List.of(testStudent2));
        when(studentRepository.findAllByAge(ArgumentMatchers.eq(18))).thenReturn(studentArrayList1);
        ArrayList<Student> studentArrayList2 = new ArrayList<>(List.of(testStudent2, testStudent3));
        when(studentRepository.findAllByAgeBetween(ArgumentMatchers.eq(16), ArgumentMatchers.eq(21)))
                .thenReturn(studentArrayList2);
    }

    @Test
    void crudStudent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).save(ArgumentMatchers.any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).findById(ArgumentMatchers.anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository, times(2) ).save(ArgumentMatchers.any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).deleteById(ArgumentMatchers.anyLong());

    }

    @Test
    void getFacultyByStudentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(studentRepository).findById(ArgumentMatchers.anyLong());
    }

    @Test
    void getStudentsFiltered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filteredByAge?age=18")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Hermione"))
                .andExpect(jsonPath("$[0]age").value(18));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filteredByAgeBetween?ageMin=16&ageMax=21")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Hermione"))
                .andExpect(jsonPath("$[0]age").value(18))

                .andExpect(jsonPath("$[1]id").value(3L))
                .andExpect(jsonPath("$[1]name").value("Ron"))
                .andExpect(jsonPath("$[1]age").value(20));
    }
}
