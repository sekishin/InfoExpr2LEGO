package lineTrace;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;


public class SoundThread implements Runnable {

	@Override
	public void run() {
		while ( ! RunThread.touch.isPressed()) {
			if (SensorThread.isGreen()) { Button.LEDPattern(1); Sound.beep(); }
			Delay.msDelay(50);
			Button.LEDPattern(0); // 消灯
		}

	}

}
