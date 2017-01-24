package garageIO;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public class SensorThread implements Runnable {
	static ColorSensor rightColor = new ColorSensor(SensorPort.S2);
	static ColorSensor leftColor = new ColorSensor(SensorPort.S3);
	static final int WHITE = 0;
	static final int BLACK = 1;
	static final int GRAY1 = 2;
	static final int GRAY2 = 3;
	static final int GRAY3 = 4;
	static final int GRAY4 = 5;
	static final int BLUE = 6;
	static final int ELSE = 7;
	
	static float whiteWidth = 0.04F;   // 白判定の認識範囲
	static float blackWidth = 0.02F;  // 黒安定の認識範囲
	static float gray1Width = 0.02F;
	static float gray2Width = 0.02F;
	static float gray3Width = 0.02F;
	static float gray4Width = 0.02F;
	static float blueWidth = 30F;
	
	static final int H = 0;
	static final int S = 1;
	static final int V = 2;
	
	static SenserColor rightState = SenserColor.WHITE;
	static SenserColor leftState = SenserColor.WHITE;

	RGBCalibration rightCalibration;
	RGBCalibration leftCalibration;
	
	private static float leftValue[] = new float[3];
	private static float rightValue[] = new float[3];

	static final String[] color = {"white", "black", "gray1", "gray2", "gray3", "gray4", "blue", "else"};

	
	public void calibration() {
		rightCalibration = new RGBCalibration(7, rightColor);
		leftCalibration = new RGBCalibration(7, leftColor);
		LCD.drawString("==Right Calibration==", 0, 0);
		enterPressWait();
		rightCalibration.executeCalibration();
		LCD.clear();
		LCD.drawString("==Leftt Calibration==", 0, 0);
		enterPressWait();
		leftCalibration.executeCalibration();
		enterPressWait();
		LCD.clear();

	}

	@Override
	public void run() {
		
		calibration();
		
		while ( Button.ESCAPE.isUp() ) {
			leftValue = getSensorValue(leftColor);
			rightValue = getSensorValue(rightColor);
			rightState = positionDicision(rightValue, rightCalibration);
			leftState = positionDicision(leftValue, leftCalibration);
			Delay.msDelay(100);
			RunThread.wait = false;
			SoundThread.wait = false;
			LCD.clear();
			LCD.drawString("R : " + color[toInt(rightState)], 0, 0);
			LCD.drawString("L : " + color[toInt(leftState)], 0, 1);
			LCD.refresh();
		}

	}
	
	public static SenserColor getRightState() {
		return rightState;
	}
	
	public static SenserColor getLeftState() {
		return leftState;
	}

	private static int toInt(SenserColor c) {
		if ( c == SenserColor.WHITE ) return 0;
		if ( c == SenserColor.BLACK ) return 1;
		if ( c == SenserColor.GRAY1 ) return 2;
		if ( c == SenserColor.GRAY2 ) return 3;
		if ( c == SenserColor.GRAY3 ) return 4;
		if ( c == SenserColor.GRAY4 ) return 5;
		if ( c == SenserColor.BLUE ) return 6;
		return 7;
	}

	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}
	
	private static SenserColor positionDicision(float[] sensorValue, RGBCalibration calibration) {
		float whiteData[] = calibration.getCalibData(WHITE);
		float blackData[] = calibration.getCalibData(BLACK);
		float gray1Data[] = calibration.getCalibData(GRAY1);
		float gray2Data[] = calibration.getCalibData(GRAY2);
		float gray3Data[] = calibration.getCalibData(GRAY3);
		float gray4Data[] = calibration.getCalibData(GRAY4);
		float blueData[] = calibration.getCalibData(BLUE);

		if ( isBlue(sensorValue, blueData) ) return SenserColor.BLUE;
		if ( isWhite(sensorValue, whiteData) ) return SenserColor.WHITE;
		if ( isBlack(sensorValue, blackData) ) return SenserColor.BLACK;
		//if ( isGray1(sensorValue, gray1Data) ) return SenserColor.GRAY1;
		//if ( isGray2(sensorValue, gray2Data) ) return SenserColor.GRAY2;
		if ( isGray3(sensorValue, gray3Data) ) return SenserColor.GRAY3;
		if ( isGray4(sensorValue, gray4Data) ) return SenserColor.GRAY4;
		
		return SenserColor.ELSE;
	}
	
	private static boolean isWhite(float value[], float data[]) {
		return compereValue(value, data, whiteWidth, V);
	}
	
	private static boolean isBlack(float value[], float data[]) {
		return compereValue(value, data, blackWidth, V);
	}

	private static boolean isGray1(float value[], float data[]) {
		return compereValue(value, data, gray1Width, V);
	}

	private static boolean isGray2(float value[], float data[]) {
		return compereValue(value, data, gray2Width, V);
	}

	private static boolean isGray3(float value[], float data[]) {
		return compereValue(value, data, gray3Width, V);
	}

	private static boolean isGray4(float value[], float data[]) {
		return compereValue(value, data, gray4Width, V);
	}

	private static boolean isBlue(float value[], float data[]) {
		return compereValue(value, data, blueWidth, H);
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
	public static float getRightBrightness() {
		return rightValue[V];
	}
	
	public static float getLeftBrightness() {
		return leftValue[V];
	}

}
