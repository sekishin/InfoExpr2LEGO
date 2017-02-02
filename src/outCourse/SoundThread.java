package outCourse;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class SoundThread implements Runnable {

	private static boolean isHarmony = false;
	private static boolean beepedA = false;
	private static boolean beepedB = false;
	private static boolean beepedC = false;
	private static boolean beepedD = false;
	
	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			if ( isHarmony ) {
				if ( SensorThread.getLeftColor() == SensorColor.GRAY1) {
					beepA();
				} else if ( SensorThread.getLeftColor() == SensorColor.GRAY2) {
					beepB();
				} else if ( SensorThread.getLeftColor() == SensorColor.GRAY3 ) {
					beepC();
				} else if ( SensorThread.getLeftColor() == SensorColor.GRAY4 ) {
					beepD();
				}
			} else if (SensorThread.isFind(0.1F, 0.4F) ) {
				Button.LEDPattern(1);
				//Sound.beep();
			}
			Delay.msDelay(50);
			Button.LEDPattern(0);
		}
	}
	
	public static void setHarmonyModeON() {
		isHarmony = true;
	}

	public static void setHarmonyModeOFF() {
		isHarmony = false;
		beepedA = false;
		beepedB = false;
		beepedC = false;
		beepedD = false;
	}

	private void beepA() {
		if ( beepedA ) return;
		Sound.playTone(440, 200);
		beepedA = true;
		
	}
	private void beepB() {
		if ( beepedB ) return;
		Sound.playTone(880, 200);
		beepedB = true;
	}

	private void beepC() {
		if ( beepedC ) return;
		Sound.playTone(1760, 200);
		beepedC = true;
	}
	
	private void beepD() {
		if ( beepedD ) return;
		Sound.playTone(3520, 200);
		beepedD = true;
	}
	
	
}


