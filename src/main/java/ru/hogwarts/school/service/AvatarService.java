package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exceptions.ItemNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public void uploadAvatarByStudentId(long studentId, MultipartFile file) throws IOException {

        Student student = studentService.findStudent(studentId);

        if (file.getOriginalFilename() != null) {
            Path filePath = Path.of(avatarsDir, studentId + "." +
                    getExtension(file.getOriginalFilename()));
            Files.deleteIfExists(filePath);
            Files.createDirectories(filePath.getParent());

            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {

                bis.transferTo(bos);

                Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
                byte[] preview = generateImagePreviewForDB(filePath);
                avatar.setStudent(student);
                avatar.setFilePath(filePath.toString());
                avatar.setMediaType(file.getContentType());
                avatar.setData(preview);
                avatar.setFileSize(file.getSize());

                avatarRepository.save(avatar);
            }
        }
    }

    public Avatar getByStudentId(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(
                () -> new ItemNotFoundException("Student not found"));
    }

    public void removeByStudentId(long studentId) {
        avatarRepository.delete(getByStudentId(studentId));
    }

    private String getExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreviewForDB(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
             ) {
                BufferedImage image = ImageIO.read(bis);

                int height = image.getHeight() / (image.getWidth() / 100);
                BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }
}
