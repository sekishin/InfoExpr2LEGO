package targetGoAround;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;


public class SoundThread implements Runnable {

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			if (SensorThread.isFind() ) {
				Button.LEDPattern(1);
				Sound.beep();
			}
			Delay.msDelay(50);
			Button.LEDPattern(0);
		}
	}

}
