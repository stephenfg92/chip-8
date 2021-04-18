package chip;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {

	public static void playSound(String file) {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(file));
			clip.open(audioInput);
			clip.start();
			System.out.println("Beep");
		} catch (Exception e) {
			System.err.println("Falha ao reproduzir arquivo: " + e.getMessage());
		}
	}

}