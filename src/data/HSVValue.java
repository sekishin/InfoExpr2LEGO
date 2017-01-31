package data;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;

public class HSVValue {

	private static ColorSensor left = new ColorSensor(SensorPort.S3);

	public static void main(String[] args) {
		LCD.clear();
		for (int i = 0; i < 8; i++) {
			enterPressWait();
			showHSV(cleanDecimal(left.getHSV()), 0, i);
		}
		enterPressWait();
	}

	public static void showHSV(float[] v, int x, int y) {
		LCD.drawString(v[0] + " " + v[1] + " " + v[2], x, y);
	}

	private static float[] cleanDecimal(float f[]){
		for(int i = 0; i < f.length; i++) {
			f[i] = (float)Math.floor((double)f[i] * 1000) / 1000;
		}
		return f;
	}

	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}
}

