package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import jp.ac.kagawa_u.infoexpr.Sensor.UltrasonicSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class SensorThread implements Runnable {

	// 色彩センサ
	static ColorSensor rightColor;
	static ColorSensor leftColor;
	
	// 反響センサ
	static UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
	
	// 検知距離範囲
	static final float MAX_DISTANCE = 0.45F;
	static final float MIN_DISTANCE = 0.05F;

	// 検知距離
	private static float distance;
	
	// 色
	private static String[] colorName = {"white", "black", "gray1", "gray2", "gray3", "gray4", "blue", "else"};
	private static final int COLORS = 7;
	private static final int WHITE = 0;
	private static final int BLACK = 1;
	private static final int GRAY1 = 2;
	private static final int GRAY2 = 3;
	private static final int GRAY3 = 4;
	private static final int GRAY4 = 5;
	private static final int BLUE = 6;

	// HSV情報	
	private float leftValue[] = new float[3];
	private float rightValue[] = new float[3];
	
	// 色情報
	private static SensorColor leftState;
	private static SensorColor rightState;
	private static int leftColorNum;
	private static int rightColorNum;

	// HSV定数
	private static final int H = 0;
	private static final int S = 1;
	private static final int V = 2;

	// キャリブレーション誤差範囲
	private static float whiteWidth = 0.05F;
	private static float blackWidth = 0.02F;
	private static float gray1Width = 0.005F;
	private static float gray2Width = 0.0115F;
	private static float gray3Width = 0.0145F;
	private static float gray4Width = 0.0445F;
	private static float blueWidth = 30F;


	// 検知範囲
	private static float leftCalibData[][] = new float[COLORS][3];
	private static float rightCalibData[][] = new float[COLORS][3];
	
	// 計測回数
	static final int COUNT = 5;
	
	public SensorThread(ColorSensor left, ColorSensor right) {
		int i;
		leftColor = left;
		rightColor = right;
		for ( i = 0; i < COLORS-1; i++ ) {
			//leftCalibData[i] = DualCalibration.getLeftCalibDataMax(i, V);
			//rightCalibData[i] = DualCalibration.getRightCalibDataMax(i, V);
			leftCalibData[i] = DualCalibration.getLeftCalibDataAve(i);
			rightCalibData[i] = DualCalibration.getRightCalibDataAve(i);
		}
		//leftCalibData[i] = DualCalibration.getLeftCalibDataMax(i, H);
		//rightCalibData[i] = DualCalibration.getRightCalibDataMax(i, H);
		leftCalibData[i] = DualCalibration.getLeftCalibDataAve(i);
		rightCalibData[i] = DualCalibration.getRightCalibDataAve(i);
	}
	
	// 走行状態
	private static final String[] STATE_NAME = {
			"PRESS ENTER",
			"STOP", 
			"LINE TRACE", 
			"TARGET SURROUND", 
			"SHORT CIRCUIT", 
			"GARAGE IN",
			"GARAGE OUT",
			"RECOVERY TRACE"
			};
	
	@Override
	public void run() {		
		while ( Button.ESCAPE.isUp() ) {
			distance = sonic.getDistance();
			leftValue = getSensorValue(leftColor);
			rightValue = getSensorValue(rightColor);
			leftState = colorDecision(leftValue, leftCalibData);
			rightState = colorDecision(rightValue, rightCalibData);
			leftColorNum = toInt(leftState);
			rightColorNum = toInt(rightState);
			LCD.clear();
			LCD.drawString(STATE_NAME[RunThread.runState], 0, 0);
			LCD.drawString("L : " + colorName[leftColorNum], 0, 1);
			LCD.drawString("R : " + colorName[rightColorNum], 0, 2);
			LCD.drawString("D : " + distance, 0, 3);
			LCD.refresh();
			Delay.msDelay(50);
		}
	}
	
	public static boolean isFind(float min, float max) {
		return distance <= max && distance >= min;
	}
	
	public static Direction dirDecision() {
		if ( leftState == SensorColor.BLACK ) {
			if ( rightState == SensorColor.BLACK ) return Direction.BOTH;
			else return Direction.LEFT;
		} else {
			if ( rightState == SensorColor.BLACK ) return Direction.RIGHT;
			else return Direction.FRONT;
		}
		
	}
	
	public static Direction posDicision(boolean parkFinished) {
		if ( rightColorNum == leftColorNum ) return Direction.FRONT;
		if ( parkFinished ) {
			if ( rightColorNum > leftColorNum ) return Direction.LEFT;
			else return Direction.RIGHT;
		} else {
			if ( rightColorNum > leftColorNum ) return Direction.RIGHT;
			else return Direction.LEFT;
		}
	}
	
	public static SensorColor getLeftColor() {
		return leftState;
	}

	public static SensorColor getRightColor() {
		return rightState;
	}

	private static int toInt(SensorColor c) {
		if ( c == SensorColor.WHITE ) return 0;
		if ( c == SensorColor.BLACK ) return 1;
		if ( c == SensorColor.GRAY1 ) return 2;
		if ( c == SensorColor.GRAY2 ) return 3;
		if ( c == SensorColor.GRAY3 ) return 4;
		if ( c == SensorColor.GRAY4 ) return 5;
		if ( c == SensorColor.BLUE ) return 6;
		return 7;
	}
/*	private static SensorColor colorDecision(float[] value, float[][] data) {
		if ( compereValue(value, data[BLUE], blueWidth, H) ) return SensorColor.BLUE;
		if ( compereVValue(value[V], Float.MIN_VALUE, data[BLACK][V]) ) return SensorColor.BLACK;
		if ( compereVValue(value[V], data[BLACK][V], data[GRAY1][V]) ) return SensorColor.GRAY1;
		if ( compereVValue(value[V], data[GRAY1][V], data[GRAY2][V]) ) return SensorColor.GRAY2;
		if ( compereVValue(value[V], data[GRAY2][V], data[GRAY3][V]) ) return SensorColor.GRAY3;
		if ( compereVValue(value[V], data[GRAY3][V], data[GRAY4][V]) ) return SensorColor.GRAY4;
		if ( compereVValue(value[V], data[GRAY4][V], Float.MAX_VALUE) ) return SensorColor.WHITE;
		return SensorColor.ELSE;
	}
*/	
	private static SensorColor colorDecision(float[] value, float[][] data) {
		if ( compereValue(value, data[BLUE], blueWidth, H) ) return SensorColor.BLUE;
		if ( compereValue(value, data[BLACK], blackWidth, V) ) return SensorColor.BLACK;
		if ( compereValue(value, data[GRAY1], gray1Width, V) ) return SensorColor.GRAY1;
		if ( compereValue(value, data[GRAY2], gray2Width, V) ) return SensorColor.GRAY2;
		if ( compereValue(value, data[GRAY3], gray3Width, V) ) return SensorColor.GRAY3;
		if ( compereValue(value, data[GRAY4], gray4Width, V) ) return SensorColor.GRAY4;
		if ( compereValue(value, data[WHITE], whiteWidth, V) ) return SensorColor.WHITE;
		return SensorColor.ELSE;
	}
	
	private static boolean compereValue(float value[], float data[], float width, int i) {
		return Math.abs(value[i]-data[i]) <= width;
	}
	
	private static boolean compereVValue(float value, float min, float max) {
		return value <= max && value > min;
	}

	private static float[] getSensorValue(ColorSensor s) {
		float f[] = {0F, 0F, 0F};
		float tmp[];
		for ( int i = 0; i < COUNT; i++ ) {
			tmp = s.getHSV();
			for ( int j = 0; j < 3; j++ ) {
				f[j] += tmp[j];
			}
		}
		for ( int j = 0; j < 3; j++ ) {
			f[j] /= COUNT;
		}
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
