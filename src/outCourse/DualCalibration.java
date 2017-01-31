package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class DualCalibration {
	
	private static int calibNumber;
	private static float leftCalibData[][], rightCalibData[][];
	private static ColorSensor leftColorSensor, rightColorSensor;
	
	private static String[] colorName = {"white", "black", "gray1", "gray2", "gray3", "gray4", "blue"};
		
	public static void executeCalibration(int n, ColorSensor left, ColorSensor right) {
		int i;
		
		calibNumber = n;
		leftCalibData = new float[calibNumber][3];
		rightCalibData = new float[calibNumber][3];
		leftColorSensor = left;
		rightColorSensor = right;

		LCD.clear();
		LCD.drawString("Please Push Enter", 0, 0);
		for ( i = 0; i < calibNumber; i++ ) {
			LCD.drawString(colorName[i], 0, i + 1);
			enterPressWait();
			leftCalibData[i] = getAction(leftColorSensor);
			rightCalibData[i] = getAction(rightColorSensor);
		}
		LCD.drawString("Complete!!!!", 0, i + 1);
		Delay.msDelay(1000);
		enterPressWait();
		LCD.clear();
	}
	
	private static float[] getAction(ColorSensor s) {
		float f[];
		f = s.getHSV();
		f = cleanDecimal(f);
		return f;

	}
	
	private static float[] cleanDecimal(float f[]){
		for(int i = 0; i < f.length; i++) {
			f[i] = (float)Math.floor((double)f[i] * 1000) / 1000;
		}
		return f;
	}
	
	public static float[] getLeftCalibData(int n) {
		if (n < calibNumber) {
			return leftCalibData[n];
		} else {
			return null;
		}
	}
	
	public static float[] getRightCalibData(int n) {
		if (n < calibNumber) {
			return rightCalibData[n];
		} else {
			return null;
		}
	}



	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}


}
