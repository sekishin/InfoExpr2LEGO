package targetGoAround;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import jp.ac.kagawa_u.infoexpr.Sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class SensorThread implements Runnable {

	static ColorSensor rightColor = new ColorSensor(SensorPort.S2);
	static ColorSensor leftColor = new ColorSensor(SensorPort.S3);
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
	private static int Count = 10;
	static float newstate, state;
	static boolean isStart = false;

	static float middleValue = 0.03F;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			newstate = 0;
			for ( int i = 0; i < Count; i++ ) {
				newstate += sonic.getDistance();
			}
			newstate /= Count;
			//LCD.clear();
			LCD.drawString(String.valueOf(state), 0, 3);
			LCD.refresh();
			state = newstate;
			isStart = true;
			Delay.msDelay(100);
		}
		LCD.clear();
		LCD.refresh();
	}

	public static Distination getDistination() {
		if ( isBlack(leftColor) ) {
			if ( isBlack(rightColor) )
				return Distination.ELSE;
			return Distination.LEFT;
		} else {
			if ( isBlack(rightColor) )
				return Distination.RIGHT;
			return Distination.STRAIGHT;
		}
	}
	
	public static boolean isFind() {
		return isStart && state < 0.4F && state > 0.05F; 
	}
	
	public static float getDistance() {
		return state;
	}
	

	private static boolean isBlack(ColorSensor cs) {
		float red = cs.getRed();
		float green = cs.getGreen();
		float blue = cs.getBlue();

		float average = (red + green + blue) / 3;
		return ( average > middleValue ) ? false : true;
	}
}
