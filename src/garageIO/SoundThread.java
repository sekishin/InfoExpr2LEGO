package garageIO;

import lejos.hardware.Button;
import lejos.hardware.Sound;

public class SoundThread implements Runnable {
	
	static boolean wait = true;
	
	@Override
	public void run() {
		while ( wait );
		while ( Button.ESCAPE.isUp()) {
			if ( SensorThread.getRightState() == SensorThread.getLeftState()) {
				if ( SensorThread.getRightState() == SenserColor.BLACK) {
					Sound.playTone(523, 200);
				} else if ( SensorThread.getRightState() == SenserColor.GRAY1) {
					Sound.playTone(587, 200);
				} else if ( SensorThread.getRightState() == SenserColor.GRAY2) {
					Sound.playTone(659, 200);
				} else if ( SensorThread.getRightState() == SenserColor.GRAY3 ) {
					Sound.playTone(698, 200);
				} else if ( SensorThread.getRightState() == SenserColor.GRAY4 ) {
					Sound.playTone(783, 200);
				}
			}
		}
	}

}
