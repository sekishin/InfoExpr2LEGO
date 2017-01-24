package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class SensorThread implements Runnable {

	// 色彩センサ
	static ColorSensor rightColor;
	static ColorSensor leftColor;

	// 色
	private static String[] colorName = {"white", "black", "else"};
	private static final int COLORS = 2;
	private static final int WHITE = 0;
	private static final int BLACK = 1;

	// HSV情報	
	private float leftValue[] = new float[3];
	private float rightValue[] = new float[3];
	
	// 色情報
	private static SensorColor leftState;
	private static SensorColor rightState;

	// HSV
	private static final int H = 0;
	private static final int S = 1;
	private static final int V = 2;

	// キャリブレーション誤差範囲
	private static float whiteWidth = 0.04F;
	private static float blackWidth = 0.02F;

	// キャリブレーション値
	private static float leftCalibData[][] = new float[COLORS][3];
	private static float rightCalibData[][] = new float[COLORS][3];
	
	
	public SensorThread(ColorSensor left, ColorSensor right) {
		leftColor = left;
		rightColor = right;
	}
	
	@Override
	public void run() {
		for ( int i = 0; i < COLORS; i++ ) {
			leftCalibData[i] = DualCalibration.getLeftCalibData(i);
			rightCalibData[i] = DualCalibration.getRightCalibData(i);
		}
		
		while ( Button.ESCAPE.isUp() ) {
			leftValue = getSensorValue(leftColor);
			rightValue = getSensorValue(rightColor);
			leftState = colorDecision(leftValue, leftCalibData);
			rightState = colorDecision(rightValue, rightCalibData);
			LCD.drawString("L : " + colorName[toInt(leftState)], 0, 0);
			LCD.drawString("R : " + colorName[toInt(rightState)], 0, 1);
			Delay.msDelay(100);
		}
	}
	
	public static Direction dirDecision() {
		if ( leftState == SensorColor.BLACK ) {
			if ( rightState == SensorColor.BLACK ) return Direction.ELSE;
			else return Direction.LEFT;
		} else {
			if ( rightState == SensorColor.BLACK ) return Direction.RIGHT;
			else return Direction.FRONT;
		}
	}
	
	private static int toInt(SensorColor c) {
		if ( c == SensorColor.WHITE ) return 0;
		if ( c == SensorColor.BLACK ) return 1;
		return 2;
	}
	private static SensorColor colorDecision(float[] value, float[][] data) {
		if ( isBlack(value, data[BLACK]) ) return SensorColor.BLACK;
		if ( isWhite(value, data[WHITE]) ) return SensorColor.WHITE;
		return SensorColor.ELSE;
	}
	
	private static boolean isBlack(float[] value, float[] data) {
		return compereValue(value, data, blackWidth, V);
	}

	private static boolean isWhite(float value[], float data[]) {
		return compereValue(value, data, whiteWidth, V);
	}

	private static boolean compereValue(float value[], float data[], float width, int i) {
		return Math.abs(value[i]-data[i]) <= width;
	}

	private static float[] getSensorValue(ColorSensor s) {
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

}
