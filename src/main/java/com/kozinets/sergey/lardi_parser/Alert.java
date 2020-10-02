package com.kozinets.sergey.lardi_parser;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@Component
public class Alert {
    public static void gav() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        String simpleFileName = "gav.wav";
        String fileName = "";
        File soundFile = null;
        AudioInputStream ais;

        try {
            fileName = Alert.class.getClassLoader().getResource(simpleFileName).getFile();
            System.out.println("try {" + fileName);
            soundFile = ResourceUtils.getFile(fileName);
            ais = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            System.out.println(e.getMessage());
            try {
                fileName = new File(new File(new File(Alert.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).getParent()).getParent() + File.separator + simpleFileName;
                System.out.println("catch (Exception e){" + fileName);
                soundFile = ResourceUtils.getFile(fileName);
                ais = AudioSystem.getAudioInputStream(soundFile);
            } catch (Exception e1){
                return;
            }
        }

        Clip clip = AudioSystem.getClip();
        DataLine.Info info = new DataLine.Info(Clip.class, ais.getFormat());
        clip = (Clip)AudioSystem.getLine(info);
        clip.open(ais);
        clip.setFramePosition(0);
        clip.start();
    }
}
