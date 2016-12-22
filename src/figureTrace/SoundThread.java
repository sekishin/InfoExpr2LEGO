package figureTrace;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class SoundThread  implements Runnable {

	private static final int BEEP_TIME = 40;
	@Override
	public void run() {
		while(TimeThread.getTime() <= BEEP_TIME);
		Sound.beep();
		Button.LEDPattern(1);
		Delay.msDelay(100);
		Button.LEDPattern(0); // 消灯
	}
}
