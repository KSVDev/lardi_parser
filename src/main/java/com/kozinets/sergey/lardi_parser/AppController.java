package com.kozinets.sergey.lardi_parser;

import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    public AppService getAppService() {
        return appService;
    }

    public AppController setAppService(AppService appService) {
        this.appService = appService;
        return this;
    }

    @PostMapping("/get_new_data")
    List<List<String>> getNewData(@Valid @RequestBody String url) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        return appService.getNewData(url);
    }

    @PostMapping("/set_old_data")
    List<List<String>> setOldData(@Valid @RequestBody String url) throws IOException {
        return appService.setOldData(url);
    }

    @GetMapping("/get_url")
    String getUrl() {
        return appService.getUrl();
    }

    @GetMapping("/get_cargos")
    List<List<String>> getCargos(){
        return appService.getCargos();
    }

    @GetMapping("/get_new_cargos")
    List<List<String>> getNewCargos(){
        return appService.getNewCargos();
    }

    @GetMapping("/off")
    void off(){
        appService.off();
    }

}
