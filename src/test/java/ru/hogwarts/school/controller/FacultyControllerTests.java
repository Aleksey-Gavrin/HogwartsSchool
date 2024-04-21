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
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyController facultyController;

    ObjectMapper objectMapper = new ObjectMapper();

    Faculty testFaculty;

    @BeforeEach
    void init() {

        Faculty testFaculty1 = new Faculty(1L, "Gryffindor", "Red");
        Faculty testFaculty2 = new Faculty(2L, "Slytherin", "Green");
        Faculty testFaculty3 = new Faculty(3L, "Ravenclaw", "Red");
        testFaculty = testFaculty1;

        Student testStudent1 = new Student(1L, "Harry", 20);
        testStudent1.setFaculty(testFaculty1);
        testFaculty1.setStudents(List.of(testStudent1));

        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(testFaculty1);
        when(facultyRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(testFaculty1));
        ArrayList<Faculty> facultyArrayList1 = new ArrayList<>(List.of(testFaculty2));
        when(facultyRepository.findAllByColor(ArgumentMatchers.eq("Green"))).thenReturn(facultyArrayList1);
        ArrayList<Faculty> facultyArrayList2 = new ArrayList<>(List.of(testFaculty2, testFaculty3));
        when(facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(ArgumentMatchers.eq("Ravenclaw"),
                ArgumentMatchers.eq("Green"))).thenReturn(facultyArrayList2);
    }

    @Test
    void crudFaculty() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(facultyRepository).save(ArgumentMatchers.any(Faculty.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(facultyRepository).findById(ArgumentMatchers.anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(facultyRepository, times(2) ).save(ArgumentMatchers.any(Faculty.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(facultyRepository).deleteById(ArgumentMatchers.anyLong());

    }

    @Test
    void getStudentsByFacultyId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]id").value(1L))
                .andExpect(jsonPath("$[0]name").value("Harry"))
                .andExpect(jsonPath("$[0]age").value(20));
        Mockito.verify(facultyRepository).findById(ArgumentMatchers.anyLong());
    }

    @Test
    void getFacultiesFiltered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filteredByColor?color=Green")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Slytherin"))
                .andExpect(jsonPath("$[0]color").value("Green"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filteredByNameOrColor?name=Ravenclaw&color=Green")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Slytherin"))
                .andExpect(jsonPath("$[0]color").value("Green"))

                .andExpect(jsonPath("$[1]id").value(3L))
                .andExpect(jsonPath("$[1]name").value("Ravenclaw"))
                .andExpect(jsonPath("$[1]color").value("Red"));
    }
}
