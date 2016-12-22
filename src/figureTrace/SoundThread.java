package figureTrace;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class SoundThread  implements Runnable {

	private static final int BEEP_TIME = 15;
	private static boolean isBeep = false;
	
	@Override
	public void run() {
		while(TimeThread.getTime() <= BEEP_TIME || ! isBeep);
		Button.LEDPattern(1);
		Sound.beep();
		Delay.msDelay(100);
		Button.LEDPattern(0); // 消灯
	}
	
	public static void setBeep() {
		isBeep = true;
	}
}
