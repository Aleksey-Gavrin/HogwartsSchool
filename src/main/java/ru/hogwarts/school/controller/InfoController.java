package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.InfoService;

@RestController
public class InfoController {

    private final String portNumber;
    private final InfoService infoService;

    public InfoController(@Value("${server.port:8080}") String portNumber, InfoService infoService) {
        this.portNumber = portNumber;
        this.infoService = infoService;
    }

    @GetMapping("/port")
    public String getPort() {
        return portNumber;
    }

    @GetMapping("/getSum")
    public long getSum() {
       return infoService.getSumByStream();
    }

    @GetMapping("/getSumOptimised")
    public long getSumOptimised() {
        return infoService.getSumByStreamOptimised();
    }
}
