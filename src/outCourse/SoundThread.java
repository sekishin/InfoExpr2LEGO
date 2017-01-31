package outCourse;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class SoundThread implements Runnable {

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() && RunThread.isRun ) {
			if (SensorThread.isFind(0.1F, 0.4F) ) {
				Button.LEDPattern(1);
				Sound.beep();
			}
			Delay.msDelay(50);
			Button.LEDPattern(0);
		}
	}
}
