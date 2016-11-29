package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class ColorSensor extends EV3ColorSensor implements Sensor {

	private SensorMode lightMode = this.getMode(1);
	private SensorMode colorMode = this.getMode(2);

/**
 * ColorSensorのコンストラクタ
 * @param Port SensorPort
 */
	public ColorSensor(Port port) {
		super(port);
	}

	/**
	 * センサー値を取得
	 * @return float[] RGBが格納されたfloat型配列
	 */
	public float[] getValue() {
		float[] result = new float[colorMode.sampleSize()];
		colorMode.fetchSample(result, 0);
		return result;
	}

	/**
	 * RGBを取得
	 * @return float RGBが格納されたfloat型配列
	 */
	public float[] getRGB() {
		return this.getValue();
	}

	/**
	* RGBの赤成分を取得
	* @return float RGBの赤成分が格納されたfloat型
	*/
	public float getRed() {
		return this.getValue()[0];
	}

	/**
	 * RGBの緑成分を取得
	 * @return float RGBの緑成分が格納されたfloat型
	 */
	public float getGreen() {
		return this.getValue()[1];
	}

	/**
	 * RGBの青成分を取得
	 * @return float RGBの青成分が格納されたfloat型
	 */
	public float getBlue() {
		return this.getValue()[2];
	}

	/**
	* 反射光の値を取得
	* @return float 反射光の値が格納されたfloat型
	*/
	public float getLight() {
		float[] result = new float[lightMode.sampleSize()];
		lightMode.fetchSample(result, 0);
		return result[0];
	}
}
