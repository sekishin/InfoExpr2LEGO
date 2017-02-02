package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class DualCalibration {
	
	private static int calibNumber;
	private static float leftCalibData[][][], rightCalibData[][][];
	private static ColorSensor leftColorSensor, rightColorSensor;
	
	// 計測回数
	private static final int COUNT = 10000;
	
	private static String[] colorName = {"white", "black", "gray1", "gray2", "gray3", "gray4", "blue"};
		
	public static void executeCalibration(int n, ColorSensor left, ColorSensor right) {
		int i;
		
		calibNumber = n;
		leftCalibData = new float[calibNumber][COUNT][3];
		rightCalibData = new float[calibNumber][COUNT][3];
		leftColorSensor = left;
		rightColorSensor = right;

		LCD.clear();
		for ( i = 0; i < calibNumber; i++ ) {
			LCD.drawString(colorName[i], 0, i);
			enterPressWait();
			for ( int j = 0; j < COUNT; j++ ) {
				leftCalibData[i][j] = getAction(leftColorSensor);
				rightCalibData[i][j] = getAction(rightColorSensor);

			}
			LCD.drawString("OK", 10, i);

		}
		LCD.drawString("Complete!!!!", 0, i);
		Delay.msDelay(1000);
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
	
	public static float[] getLeftCalibDataMax(int n, int hsv) {
		int max = 0;
		if (n < calibNumber) {
			for ( int i = 1; i < COUNT; i++) {
				if ( leftCalibData[n][max][hsv] > leftCalibData[n][i][hsv] ) {
					max = i;
				}
			}
			return leftCalibData[n][max];
		} else {
			return null;
		}
	}
	
	public static float[] getRightCalibDataMax(int n, int hsv) {
		int max = 0;
		if (n < calibNumber) {
			for ( int i = 1; i < COUNT; i++) {
				if ( rightCalibData[n][max][hsv] > rightCalibData[n][i][hsv] ) {
					max = i;
				}
			}
			return rightCalibData[n][max];
		} else {
			return null;
		}
	}
	
	public static float[] getLeftCalibDataAve(int n) {
		float f[] = {0F, 0F, 0F};
		if ( n >= calibNumber ) return null;
		for ( int i = 0; i < COUNT; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				f[j] += leftCalibData[n][i][j];
			}
		}
		for ( int j = 0; j < 3; j++ ) {
			f[j] /= COUNT;
		}
		return f;
	}

	public static float[] getRightCalibDataAve(int n) {
		float f[] = {0F, 0F, 0F};
		if ( n >= calibNumber ) return null;
		for ( int i = 0; i < COUNT; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				f[j] += rightCalibData[n][i][j];
			}
		}
		for ( int j = 0; j < 3; j++ ) {
			f[j] /= COUNT;
		}
		return f;
	}


	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}


}
