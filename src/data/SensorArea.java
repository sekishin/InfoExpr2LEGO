package data;

import jp.ac.kagawa_u.infoexpr.Sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class SensorArea {

	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
	static float state;

	public static void main(String[] args) {
		while ( Button.ESCAPE.isUp() ) {
			LCD.clear();
			state = sonic.getDistance();
			LCD.drawString(String.valueOf(state*100), 0, 0);
			LCD.refresh();
			Delay.msDelay(100);
			LCD.refresh();
		}
		LCD.clear();
	}
}
