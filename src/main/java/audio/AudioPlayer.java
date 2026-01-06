/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Singleton;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AudioPlayer {
    private static final Logger log = LoggerFactory.getLogger(AudioPlayer.class);
    private Line prevLine;

    public void play(File file, float gain) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));){
            this.play(stream, gain);
        }
    }

    public void play(Class<?> c, String path, float gain) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        try (BufferedInputStream stream = new BufferedInputStream(c.getResourceAsStream(path));){
            this.play(stream, gain);
        }
    }

    public void play(InputStream stream, float gain) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(stream);){
            DataLine line = this.getSelfClosingLine(audio);
            if (gain != 0.0f) {
                this.trySetGain(line, gain);
            }
            line.start();
        }
    }

    private DataLine getSelfClosingLine(AudioInputStream stream) throws IOException, LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        try {
            clip.open(stream);
        }
        catch (IOException e) {
            clip.close();
            throw e;
        }
        clip.addLineListener(event -> {
            if (event.getType() != LineEvent.Type.STOP) {
                return;
            }
            AudioPlayer audioPlayer = this;
            synchronized (audioPlayer) {
                if (this.prevLine != null) {
                    this.prevLine.close();
                }
                this.prevLine = clip;
            }
        });
        return clip;
    }

    private void trySetGain(DataLine line, float gain) {
        try {
            FloatControl control = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(gain);
        }
        catch (Exception e) {
            log.warn("Failed to set gain: {}", (Object)e.getMessage());
        }
    }
}

