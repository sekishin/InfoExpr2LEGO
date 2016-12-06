package figureTrace;

import lejos.hardware.Sound;

public class SoundThread  implements Runnable {

    @Override
    public void run() {
    	while(TimeThread.getTime() <= 38);
    	Sound.beep();
    }
}