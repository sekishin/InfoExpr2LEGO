package figureTrace;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class SoundThread  implements Runnable {

	private static final int BEEP_TACHO_COUNT = RunThread.L2_TACHO_COUNT / 2;
	private static boolean isBeep = false;
	
	@Override
	public void run() {
		while(! isBeep || RunThread.getTachoCount() < BEEP_TACHO_COUNT );
		Button.LEDPattern(1);
		Sound.beep();
		Delay.msDelay(100);
		Button.LEDPattern(0); // 消灯
	}
	
	public static void setBeep() {
		isBeep = true;
	}
}
