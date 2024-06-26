package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.AvatarDTO;
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
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
        logger.debug("AvatarService Bean is created");
    }

    public void uploadAvatarByStudentId(long studentId, MultipartFile file) throws IOException {

        logger.info("Invoked method: uploadAvatarByStudentId()");

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

                logger.debug("Avatar for student with id = " + studentId + " was successfully upload");
            }
        }
    }

    public Avatar getByStudentId(long studentId) {
        logger.info("Invoked method: getByStudentId()");
        return avatarRepository.findByStudentId(studentId).orElseThrow(
                () -> {
                    logger.error("Avatar for student with id = " + studentId + " not found");
                    return new ItemNotFoundException();
                }
        );
    }

    public void removeByStudentId(long studentId) {
        logger.info("Invoked method: removeByStudentId()");
        avatarRepository.delete(getByStudentId(studentId));
        logger.debug("Avatar for student with id = " + studentId + " was successfully deleted");
    }

    private String getExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreviewForDB(Path filePath) throws IOException {
        logger.info("Invoked method: generateImagePreviewForDB()");
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

            logger.debug("Image preview for DB was successfully created");

            return baos.toByteArray();
        }
    }

    public List<AvatarDTO> getAvatarsPage(int page, int size) {
        logger.info("Invoked method: getAvatarsPage() with parameters: page = " + page + "; size = " + size);
        Pageable p = PageRequest.of(page, size);
        return avatarRepository.findAll(p).getContent().stream()
                .map(avatar -> new AvatarDTO(avatar.getStudent().getName(),
                        "http://localhost:8080/student/avatar/" + avatar.getStudent().getId()))
                .collect(Collectors.toList());
    }
}
