package lineTrace;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.port.SensorPort;

public class SensorThread implements Runnable {

	static ColorSensor rightColor = new ColorSensor(SensorPort.S2);
	static ColorSensor leftColor = new ColorSensor(SensorPort.S3);
	static float middleValue = 0.03F;
	static float middleGreen = 0.15F;
	static float middleRed = 0.17F;
	static float diffRed = 0.10F;
	static float diffGreen = 0.07F;


	@Override
	public void run() {
		while ( ! RunThread.touch.isPressed() ) {
			/*
			LCD.clear();
			LCD.drawString("RR:" + rightColor.getRed(), 0, 0);
			LCD.drawString("RG:" + rightColor.getGreen(), 0, 1);
			LCD.drawString("RB:" + rightColor.getBlue(), 0, 2);
			LCD.drawString("LR:" + leftColor.getRed(), 0, 3);
			LCD.drawString("LG:" + leftColor.getGreen(), 0, 4);
			LCD.drawString("LB:" + leftColor.getBlue(), 0, 5);
			LCD.refresh();
			*/
		}
	}

	public static Distination getSensor() {

		if ( isBlack(leftColor) ) {
			if (isBlack(rightColor) ) return Distination.ELSE;
			return Distination.LEFT;
		}
		else {
			if ( isBlack(rightColor) ) return Distination.RIGHT;
			return Distination.STRAIGHT;
		}
/*
		if( leftColor.getLight() < middleValue && rightColor.getLight() >= middleValue){
			return Distination.LEFT;
		}
		// 白＆黒
		else if( leftColor.getLight() >= middleValue && rightColor.getLight() < middleValue){
			return Distination.RIGHT;
		}
		// 白＆白
		else if( leftColor.getLight() >= middleValue && rightColor.getLight() >= middleValue){
			return Distination.STRAIGHT;
		}
		else {
			return Distination.ELSE;
		}
*/
	}

	public static boolean isGreen() {
		float red = leftColor.getRed();
		float green = leftColor.getGreen();
		float blue = leftColor.getBlue();
		return ( green >= middleGreen && green > red+diffGreen && green > blue+diffGreen) ? true : false;
	}

	public static boolean isRed() {
		float red = leftColor.getRed();
		float green = leftColor.getGreen();
		float blue = leftColor.getBlue();
		return (red >= middleRed && red > green+diffRed && red > blue+diffRed) ? true : false;
	}

	private static boolean isBlack(ColorSensor cs) {
		float red = cs.getRed();
		float green = cs.getGreen();
		float blue = cs.getBlue();

		float average = (red + green + blue) / 3;
		return (average > middleValue) ? false : true;
	}
}
