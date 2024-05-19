package ru.hogwarts.school.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.SchoolApplication;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FacultyControllerTests {
    @LocalServerPort
    private int port;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void crudFaculty() {

        Faculty testFaculty = new Faculty(0, "Gryffindor", "Red");

        Faculty response = this.restTemplate.postForObject("http://localhost:" + port + "/faculty",
                testFaculty, Faculty.class);
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Gryffindor");

        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty/"
                + response.getId(), String.class)).isNotNull().contains("Gryffindor");

        testFaculty.setColor("Gold");
        RequestEntity<Faculty> requestEntity = new RequestEntity<>(testFaculty, HttpMethod.PUT, null);
        ResponseEntity<Faculty> responseToUpdate = this.restTemplate.exchange("http://localhost:" + port + "/faculty/"
                + response.getId(), HttpMethod.PUT, requestEntity, Faculty.class );
        assertThat(responseToUpdate.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseToUpdate.getBody()).isNotNull();
        assertThat(responseToUpdate.getBody().getColor()).isEqualTo("Gold");

        //Кейс успешного удаления пустого факультета
        this.restTemplate.delete("http://localhost:" + port + "/faculty/" +
                response.getId());
        ResponseEntity<Faculty> responseAfterDelete = this.restTemplate.getForEntity("http://localhost:" + port + "/faculty/" +
                response.getId(), Faculty.class);
        assertThat(responseAfterDelete.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Кейс невозможности удаления факультета при наличии связанных с ним записей студентов
        Faculty slytherin = createFaculty("Slytherin", "Green");
        Student draco = createStudent("Draco", 16, slytherin);
        this.restTemplate.delete("http://localhost:" + port + "/faculty/" +
                slytherin.getId());
        ResponseEntity<Faculty> responseAfterDeleteSlytherin = this.restTemplate.getForEntity("http://localhost:" + port + "/faculty/" +
                slytherin.getId(), Faculty.class);
        assertThat(responseAfterDeleteSlytherin.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(responseAfterDeleteSlytherin.getBody()).isNotNull();
        assertThat(responseAfterDeleteSlytherin.getBody().getColor()).isEqualTo("Green");
    }

    @Test
    void getStudentsByFacultyId () throws JsonProcessingException {
        Faculty testfaculty = createFaculty("Gryffindor", "Red");
        Student student1 = createStudent("Harry", 15, testfaculty);
        Student student2 = createStudent("Ron", 20, testfaculty);
        Student student3 = createStudent("Hermione", 25, testfaculty);
        ResponseEntity<String> jsonResponseToGetStudents = this.restTemplate.getForEntity("http://localhost:" + port + "/faculty/"
                + testfaculty.getId() + "/students", String.class);
        assertThat(jsonResponseToGetStudents.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseToGetStudents.getBody()).isNotNull();
        ArrayList<Student> students = objectMapper.readValue(jsonResponseToGetStudents.getBody(), new TypeReference<>() {});
        assertThat(students.size()).isEqualTo(3);
    }

    @Test
    void getFacultyFiltered() throws JsonProcessingException {
        Faculty faculty1 = createFaculty("Gryffindor", "Blue");
        Faculty faculty2 = createFaculty("Hufflepuff", "Gold");
        Faculty faculty3 = createFaculty("Ravenclaw", "Blue");

        ResponseEntity<String> jsonResponseFilteredByColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByColor?color=Blue", String.class);
        assertThat(jsonResponseFilteredByColor.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByColor.getBody()).isNotNull();

        ArrayList<Faculty> faculties = objectMapper.readValue(jsonResponseFilteredByColor.getBody(), new TypeReference<>() {});
        assertThat(faculties.size()).isEqualTo(2);
        assertThat(faculties.get(0).getColor()).isEqualTo("Blue");
        assertThat(faculties.get(1).getColor()).isEqualTo("Blue");

        String emptyString = " ";

        jsonResponseFilteredByColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByColor?color=" + emptyString, String.class);
        assertThat(jsonResponseFilteredByColor.getStatusCode().is4xxClientError()).isTrue();

        ResponseEntity<String> jsonResponseFilteredByNameOrColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByNameOrColor?name=Hufflepuff", String.class);
        assertThat(jsonResponseFilteredByNameOrColor.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByNameOrColor.getBody()).isNotNull();

        faculties = objectMapper.readValue(jsonResponseFilteredByNameOrColor.getBody(), new TypeReference<>() {});
        assertThat(faculties.size()).isEqualTo(1);
        assertThat(faculties.get(0).getName().contains("Hufflepuff")).isTrue();

        jsonResponseFilteredByNameOrColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByNameOrColor?color=Blue", String.class);
        assertThat(jsonResponseFilteredByNameOrColor.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByNameOrColor.getBody()).isNotNull();

        faculties = objectMapper.readValue(jsonResponseFilteredByNameOrColor.getBody(), new TypeReference<>() {});
        assertThat(faculties.size()).isEqualTo(2);
        assertThat(faculties.get(0).getColor()).isEqualTo("Blue");
        assertThat(faculties.get(1).getColor()).isEqualTo("Blue");

        jsonResponseFilteredByNameOrColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByNameOrColor?name=Hufflepuff&color=Blue", String.class);
        assertThat(jsonResponseFilteredByNameOrColor.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseFilteredByNameOrColor.getBody()).isNotNull();

        faculties = objectMapper.readValue(jsonResponseFilteredByNameOrColor.getBody(), new TypeReference<>() {});
        assertThat(faculties.size()).isEqualTo(3);

        jsonResponseFilteredByNameOrColor = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/filteredByNameOrColor?name=" + emptyString + "&color=" + emptyString, String.class);
        assertThat(jsonResponseFilteredByNameOrColor.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void getLongestFacultyName() throws JsonProcessingException {
        Faculty faculty1 = createFaculty("Gryffindor", "Red");
        Faculty faculty2 = createFaculty("Sly", "Green");
        Faculty faculty3 = createFaculty("Raven", "Blue");

        ResponseEntity<String> jsonResponseGetLongestName = this.restTemplate.getForEntity("http://localhost:"
                + port + "/faculty/getLongestFacultyName", String.class);
        assertThat(jsonResponseGetLongestName.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(jsonResponseGetLongestName.getBody()).isNotNull();

        String longestName = jsonResponseGetLongestName.getBody();

        assertThat(longestName).isEqualTo("Gryffindor");
    }

    private Faculty createFaculty(String name, String color) {
        Faculty testFaculty = new Faculty(0, name, color);
        return this.restTemplate.postForObject("http://localhost:" + port + "/faculty",
                testFaculty, Faculty.class);
    }

    private Student createStudent(String name, int age, Faculty faculty) {
        Student testStudent = new Student(0, name, age);
        testStudent.setFaculty(faculty);
        return this.restTemplate.postForObject("http://localhost:" + port + "/student",
                testStudent, Student.class);
    }


}
