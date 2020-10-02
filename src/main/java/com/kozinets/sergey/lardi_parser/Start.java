package com.kozinets.sergey.lardi_parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.SECONDS;

@SpringBootApplication
@EnableSwagger2
public class Start {

    public static void main(String[] args) throws InterruptedException, UnsupportedAudioFileException, LineUnavailableException, IOException {
        LocalTime localTimeStart = LocalTime.now();
        System.out.println("START: " + localTimeStart);
        SpringApplication.run(Start.class);

        LocalTime localTimeFinish = LocalTime.now();
        long localTimeDiff = SECONDS.between(localTimeStart, localTimeFinish);
        System.out.println("------------------------");
        System.out.println("FINISH: " + localTimeFinish + ". Duration: " + localTimeDiff + " sec.");
        System.out.println("------------------------");
        System.out.println("Duration: " + localTimeDiff + " sec.");
        System.out.println("------------------------");
    }
}
