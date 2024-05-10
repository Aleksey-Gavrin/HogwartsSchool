package ru.hogwarts.school.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.SchoolApplication;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = SchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTests {
    @LocalServerPort
    private int port;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    Faculty testFaculty;
    @BeforeEach
    void setFaculty() {
        Faculty faculty = new Faculty(0, "Gryffindor", "Red");
        this.testFaculty = this.restTemplate.postForObject("http://localhost:" + port + "/faculty",
                faculty, Faculty.class);
    }

    @Test
    void crudStudent() {

        Student testStudent = new Student(0, "Hermione", 16);
        testStudent.setFaculty(testFaculty);

        Student response = this.restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent, Student.class);
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Hermione");

        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/"
                + response.getId(), String.class)).isNotNull().contains("Hermione");

        Faculty responseFaculty = this.restTemplate.getForObject("http://localhost:" + port + "/student/" +
                response.getId() + "/faculty", Faculty.class);
        assertThat(responseFaculty).isNotNull();
        assertThat(responseFaculty.getName()).isEqualTo("Gryffindor");

        testStudent.setAge(20);
        RequestEntity<Student> requestEntity = new RequestEntity<>(testStudent, HttpMethod.PUT, null);
        ResponseEntity<Student> responseToUpdate = this.restTemplate.exchange("http://localhost:" + port + "/student/"
                + response.getId(), HttpMethod.PUT, requestEntity, Student.class );
        assertThat(responseToUpdate.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseToUpdate.getBody()).isNotNull();
        assertThat(responseToUpdate.getBody().getAge()).isEqualTo(20);

        this.restTemplate.delete("http://localhost:" + port + "/student/" +
                response.getId());
        ResponseEntity<Student> responseAfterDelete = this.restTemplate.getForEntity("http://localhost:" + port + "/student/" +
                response.getId(), Student.class);
        assertThat(responseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getStudentsFiltered() throws JsonProcessingException {
        Student student1 = createStudent("Harry", 50);
        Student student2 = createStudent("Ron", 60);
        Student student3 = createStudent("Hermione", 70);

        ResponseEntity<String> jsonResponseFilteredByAge = this.restTemplate.getForEntity("http://localhost:"
                + port + "/student/filteredByAge?age=50", String.class);
        assertThat(jsonResponseFilteredByAge.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByAge.getBody()).isNotNull();

        ArrayList<Student> students = objectMapper.readValue(jsonResponseFilteredByAge.getBody(), new TypeReference<>() {
        });
        assertThat(students.size()).isEqualTo(1);
        assertThat(students.get(0).getName()).isEqualTo("Harry");

        jsonResponseFilteredByAge = this.restTemplate.getForEntity("http://localhost:"
                + port + "/student/filteredByAge?age=0", String.class);
        assertThat(jsonResponseFilteredByAge.getStatusCode().is4xxClientError()).isTrue();

        ResponseEntity<String> jsonResponseFilteredByAgeBetween = this.restTemplate.getForEntity("http://localhost:"
                + port + "/student/filteredByAgeBetween?ageMin=59&ageMax=71", String.class);
        assertThat(jsonResponseFilteredByAgeBetween.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByAgeBetween.getBody()).isNotNull();

        students = objectMapper.readValue(jsonResponseFilteredByAgeBetween.getBody(), new TypeReference<>() {
        });
        assertThat(students.size()).isEqualTo(2);
        assertThat(students.get(0).getName().contains("on")).isTrue();
        assertThat(students.get(1).getName().contains("on")).isTrue();

        jsonResponseFilteredByAgeBetween = this.restTemplate.getForEntity("http://localhost:"
                + port + "/student/filteredByAgeBetween?ageMin=10&ageMax=0", String.class);
        assertThat(jsonResponseFilteredByAgeBetween.getStatusCode().is4xxClientError()).isTrue();
    }

    private Student createStudent(String name, int age) {
        Student testStudent = new Student(0, name, age);
        testStudent.setFaculty(testFaculty);
        return this.restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent, Student.class);
    }


}
