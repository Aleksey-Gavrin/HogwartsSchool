package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/student/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/addAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createAvatar(@RequestParam long studentId, @RequestBody MultipartFile file)
            throws IOException {
        if (file.getSize() > 1000 * 1000) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        avatarService.uploadAvatarByStudentId(studentId, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public void downloadAvatar(@PathVariable long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.getByStudentId(id);
        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {

            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            response.setStatus(200);

            bis.transferTo(bos);
        }
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> getAvatarFromDB(@PathVariable long id) {
        Avatar avatar = avatarService.getByStudentId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteAvatarFromDB(@PathVariable long id) {
        avatarService.removeByStudentId(id);
        return ResponseEntity.ok().build();
    }

}
