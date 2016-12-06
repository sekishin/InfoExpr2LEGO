package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class LightSensor extends EV3ColorSensor implements Sensor {

	private SensorMode mode = this.getMode(1);

	/**
	 * LightSensorのコンストラクタ
	 * @param Port SensorPort
	 */
	public LightSensor(Port port) {
		super(port);
	}

	/**
	 * センサー値を取得
	 * @return float[] データが格納されたfloat型配列
	 */
	public float[] getValue() {
		float[] result = new float[mode.sampleSize()];
		mode.fetchSample(result, 0);
		return result;
	}

	/**
	* 反射光の値を取得
	* @return float 反射光の値が格納されたfloat型
	*/
	public float getLight() {
		return this.getValue()[0];
	}

}
