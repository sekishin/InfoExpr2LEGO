package figureTrace;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TimeThread implements Runnable {
    private static int  time = 0;

    @Override
    public void run() {
    	while(time <= 50) {
    		Delay.msDelay(100);
    		time++;
    		LCD.clear();
            LCD.drawString(Integer.toString(time), 0, 5);
    	}
    }

    public static int getTime() {
        return time;
    }
        
    public static void resetTime() {
    	time = 0;
    }
}
