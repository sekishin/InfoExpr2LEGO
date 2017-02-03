package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class DualCalibration {
	
	private static int calibNumber;
	private static float leftCalibData[][][], rightCalibData[][][];
	private static ColorSensor leftColorSensor, rightColorSensor;
	
	// 計測回数
	private static final int COUNT = 10000;
	
	// 色名
	private static String[] colorName = {
			"white",
			"black",
			"gray1",
			"gray2",
			"gray3",
			"gray4",
			"blue"
			};
	
	/**
	 * キャリブレーションの実行
	 * @param n 取得する色の総数
	 * @param left 左の色彩センサ
	 * @param right 右の色彩センサ
	 */
	public static void executeCalibration(int n, ColorSensor left, ColorSensor right) {
		int i;
		calibNumber = n;
		leftCalibData = new float[calibNumber][COUNT][3];
		rightCalibData = new float[calibNumber][COUNT][3];
		leftColorSensor = left;
		rightColorSensor = right;
		LCD.clear();
		for ( i = 0; i < calibNumber; i++ ) {
			LCD.drawString(colorName[i], 0, i);
			enterPressWait();
			for ( int j = 0; j < COUNT; j++ ) {
				leftCalibData[i][j] = getAction(leftColorSensor);
				rightCalibData[i][j] = getAction(rightColorSensor);

			}
			LCD.drawString("OK", 10, i);
		}
		LCD.drawString("Complete!!!!", 0, i);
		Delay.msDelay(1000);
		LCD.clear();
	}
	
	/**
	 * 色彩センサで値を取得し、小数部を加工
	 * @param ColorSensor 値の取得に使用するセンサ
	 * @return float[] 簡略化されたセンサの値
	 **/
	private static float[] getAction(ColorSensor s) {
		float f[];
		f = s.getHSV();
		f = cleanDecimal(f);
		return f;

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
	 * 左の色彩センサで取得したデータの最大値を返却
	 * @param n 色番号
	 * @param hsv HSVのどの値で比較するか
	 * @return 最大の計測データ
	 */
	public static float[] getLeftCalibDataMax(int n, int hsv) {
		int max = 0;
		if (n < calibNumber) {
			for ( int i = 1; i < COUNT; i++) {
				if ( leftCalibData[n][max][hsv] > leftCalibData[n][i][hsv] ) {
					max = i;
				}
			}
			return leftCalibData[n][max];
		} else {
			return null;
		}
	}
	
	/**
	 * 右の色彩センサで取得したデータの最大値を返却
	 * @param n 色番号
	 * @param hsv HSVのどの値で比較するか
	 * @return 最大の計測データ
	 */
	public static float[] getRightCalibDataMax(int n, int hsv) {
		int max = 0;
		if (n < calibNumber) {
			for ( int i = 1; i < COUNT; i++) {
				if ( rightCalibData[n][max][hsv] > rightCalibData[n][i][hsv] ) {
					max = i;
				}
			}
			return rightCalibData[n][max];
		} else {
			return null;
		}
	}
	
	/**
	 * 左の色彩センサで取得したデータの平均値を返却
	 * @param n 色番号
	 * @param hsv HSVのどの値で比較するか
	 * @return 最大の計測データ
	 */
	public static float[] getLeftCalibDataAve(int n) {
		float f[] = {0F, 0F, 0F};
		if ( n >= calibNumber ) return null;
		for ( int i = 0; i < COUNT; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				f[j] += leftCalibData[n][i][j];
			}
		}
		for ( int j = 0; j < 3; j++ ) {
			f[j] /= COUNT;
		}
		return f;
	}

	/**
	 * 右の色彩センサで取得したデータの平均値を返却
	 * @param n 色番号
	 * @param hsv HSVのどの値で比較するか
	 * @return 最大の計測データ
	 */
	public static float[] getRightCalibDataAve(int n) {
		float f[] = {0F, 0F, 0F};
		if ( n >= calibNumber ) return null;
		for ( int i = 0; i < COUNT; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				f[j] += rightCalibData[n][i][j];
			}
		}
		for ( int j = 0; j < 3; j++ ) {
			f[j] /= COUNT;
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
