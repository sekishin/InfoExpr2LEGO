package targetGoAround;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import jp.ac.kagawa_u.infoexpr.Sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;

public class SensorThread implements Runnable {

	static ColorSensor rightColor = new ColorSensor(SensorPort.S2);
	static ColorSensor leftColor = new ColorSensor(SensorPort.S3);
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
	static float state = sonic.getDistance();
	static float newstate;

	static float middleValue = 0.03F;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			LCD.clear();
			state = sonic.getDistance();
			LCD.drawString(String.valueOf(state), 0, 0);
			LCD.refresh();
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
		return state < 0.6F; 
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
