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
	private static boolean isSearch = false;
	
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
			} else if (SensorThread.isFind(0.1F, 0.4F) && isSearch ) {
				Button.LEDPattern(1);
				Sound.beep();
				setSearchModeOFF();
			}
			Delay.msDelay(50);
			Button.LEDPattern(0);
		}
	}
	
	/**
	 * 諧調発音モードに移行
	 */
	public static void setHarmonyModeON() {
		isHarmony = true;
	}

	/**
	 * 諧調発音モードから脱出
	 */
	public static void setHarmonyModeOFF() {
		isHarmony = false;
		beepedA = false;
		beepedB = false;
		beepedC = false;
		beepedD = false;
	}
	
	/**
	 * 目標検知モードに移行
	 */
	public static void setSearchModeON() {
		isSearch = true;
	}
	
	/**
	 * 目標検知モードから脱出
	 */
	public static void setSearchModeOFF() {
		isSearch = false;
	}

	private void beepA() {
		if ( beepedA ) return;
		Button.LEDPattern(1);
		Sound.playTone(440, 200);
		beepedA = true;
		
	}
	private void beepB() {
		if ( beepedB ) return;
		Button.LEDPattern(2);
		Sound.playTone(880, 200);
		beepedB = true;
	}

	private void beepC() {
		if ( beepedC ) return;
		Button.LEDPattern(3);
		Sound.playTone(1760, 200);
		beepedC = true;
	}
	
	private void beepD() {
		if ( beepedD ) return;
		Button.LEDPattern(4);
		Sound.playTone(3520, 200);
		beepedD = true;
	}
	
	
}


