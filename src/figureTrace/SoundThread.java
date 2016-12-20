package figureTrace;

import lejos.hardware.Sound;

public class SoundThread  implements Runnable {

	private static final int BEEP_TIME = 38;
	@Override
	public void run() {
		while(TimeThread.getTime() <= BEEP_TIME);
		Sound.beep();
	}
}
