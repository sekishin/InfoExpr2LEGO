package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class AmbientSensor extends EV3ColorSensor implements Sensor {

	private SensorMode mode = this.getMode(3);

	/**
	 * AmbientSensorのコンストラクタ
     * @param Port SensorPort
     */
	public AmbientSensor(Port port) {
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
     * 自然光を取得
     * @return float 自然光が格納されたfloat型
     */
	public float getLight() {
		return this.getValue()[0];
	}
}
