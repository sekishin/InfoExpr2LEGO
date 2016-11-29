package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;

public class UltrasonicSensor extends EV3UltrasonicSensor implements Sensor {

	private SensorMode mode = this.getMode(0);

	/**
	 * UltrasonicSensorのコンストラクタ
	 * @param Port SensorPort
	 */
	public UltrasonicSensor(Port port) {
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
	* 距離の値を取得
	* @return float 距離の値が格納されたfloat型
	*/
	public float getDistance() {
		return this.getValue()[0];
	}

}
