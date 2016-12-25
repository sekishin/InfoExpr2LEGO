package areaSweeping;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;

public class SensorThread implements Runnable {

	static ColorSensor rightColor = new ColorSensor(SensorPort.S2);
	static ColorSensor leftColor = new ColorSensor(SensorPort.S3);
	static float middleValue = 0.03F;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
		}
	}

	/*
	 * decide go distination
	 * @return Distination
	 */
	public static Distination getSensor() {
		if (isBlack(leftColor)) {
			if (isBlack(rightColor)) return Distination.STRAIGHT;
			return Distination.LEFT;
		} else {
			if (isBlack(rightColor)) return Distination.RIGHT;
			return Distination.ELSE;
		}
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
		return (average > middleValue) ? false : true;
	}
}
