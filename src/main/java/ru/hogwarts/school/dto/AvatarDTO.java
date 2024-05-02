package ru.hogwarts.school.dto;

public class AvatarDTO {

    String studentName;
    String avatarURL;

    public AvatarDTO() {
    }

    public AvatarDTO(String studentName, String avatarURL) {
        this.studentName = studentName;
        this.avatarURL = avatarURL;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
