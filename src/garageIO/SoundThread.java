package garageIO;

import lejos.hardware.Button;

public class SoundThread implements Runnable {
	
	static boolean wait = true;
	
	@Override
	public void run() {
		while ( wait );
		while ( Button.ESCAPE.isUp()) {
			
		}
	}

}
