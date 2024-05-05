SELECT s.name, s.age, f.name FROM student s	LEFT JOIN faculty f ON s.faculty_id = f.id

SELECT s.name, s.age FROM student s JOIN avatar a ON s.id = a.student_id