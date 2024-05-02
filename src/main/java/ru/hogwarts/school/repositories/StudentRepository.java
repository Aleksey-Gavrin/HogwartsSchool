package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByAge(int age);

    List<Student> findAllByAgeBetween(int min, int max);

    @Query(value = "select count(*) from student s", nativeQuery = true)
    long getStudentsQty();

    @Query(value = "select avg(age) from student s", nativeQuery = true)
    double getStudentsAvgAge();

    @Query(value = "select * from (select * from student s order by id desc limit :quantity) order by id", nativeQuery = true)
    List<Student> getLastAddedStudents (@Param("quantity") int quantity);

}
