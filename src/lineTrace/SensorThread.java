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
		while (! RunThread.touch.isPressed() );
	}

	/*
	 * decide go distination
	 * @return Distination
	 */
	public static Distination getSensor() {
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

	/*
	 * judge green
	 * @return boolean
	 */
	public static boolean isGreen() {
		float red = leftColor.getRed();
		float green = leftColor.getGreen();
		float blue = leftColor.getBlue();
		return ( green >= middleGreen && green > red + diffGreen && green > blue + diffGreen ) ? true : false;
	}

	/*
	 * judge red
	 * @return boolean
	 */
	public static boolean isRed() {
		float red = leftColor.getRed();
		float green = leftColor.getGreen();
		float blue = leftColor.getBlue();
		return ( red >= middleRed && red > green + diffRed && red > blue + diffRed ) ? true : false;
	}

	/*
	 * judge black
	 * @return boolean
	 */
	private static boolean isBlack(ColorSensor cs) {
		float red = cs.getRed();
		float green = cs.getGreen();
		float blue = cs.getBlue();

		float average = (red + green + blue) / 3;
		return ( average > middleValue ) ? false : true;
	}
}
