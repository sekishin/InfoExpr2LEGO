package data;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;

public class RGBValue {
	
	private static ColorSensor left = new ColorSensor(SensorPort.S3);

	public static void main(String[] args) {
		LCD.clear();
		for (int i = 0; i < 5; i++) {
			enterPressWait();
			showRGB(cleanDecimal(left.getRGB()), 0, i);
		}
		enterPressWait();
	}
	
	public static void showRGB(float[] v, int x, int y) {
		LCD.drawString(v[0] + " " + v[1] + " " + v[2], x, y);
	}


	/**
	 * float型の小数第4位以下を除去
	 * @param float[] float型の配列
	 * @return float[] float型の配列
	 **/
	private static float[] cleanDecimal(float f[]){
		for(int i = 0; i < f.length; i++) {
			f[i] = (float)Math.floor((double)f[i] * 1000) / 1000;
		}
		return f;
	}

	/**
	 * 真ん中ボタンが押されるまで停止
	 **/
	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}
}
