package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final String portNumber;

    public InfoController(@Value("${server.port:8080}") String portNumber) {
        this.portNumber = portNumber;
    }

    @GetMapping("/port")
    public String getPort() {
        return portNumber;
    }
}
