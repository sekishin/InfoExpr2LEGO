package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.TouchSensor;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	
	// 走行状態フラグ
	static boolean isGarage = false;
	
	// 走行状態定数
	private static final int WAIT = 0;
	private static final int STOP = 1;
	private static final int LINE = 2;
	private static final int SURROUND = 3;
	private static final int SHORT = 4;
	private static final int GARAGE_IN = 5;
	private static final int GARAGE_OUT = 6;
	private static final int RECOVERY = 7;

	public static int runState = WAIT;

	
	@Override
	public void run() {
		enterPressWait();
		areaA();
		areaB();
		areaC();
		//test();
		motorStop();
	}
	
/*	private void test() {
		while ( Button.ESCAPE.isUp() ) {
			motorSetSpeed(50, 50);
			motorForward();
		}
	}
*/
	//===============================================
	//	エリア分け
	//===============================================
	/**
	 * スタートから目標周回完了まで
	 */
	private void areaA() {
		
		//-- スタートから一定距離は目標検知はせずに高速走行
		int lineTraceTacoCount = 2900;
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= lineTraceTacoCount ) {
			quickLineTrace();
		}
		
		//-- 指定距離だけ目標検知を行い、検知できれば目標周回に移行、できなければ無視して黒線追跡
		int searchTachoCount = 200;
		SoundThread.setSearchModeON();
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= searchTachoCount ) {
			slowLineTrace();
			if ( SensorThread.isFind(0.05F, 0.4F) ) {
				int slowTachoCount = 300;
				targetSurrounding();
				leftMotor.resetTachoCount();
				while ( Button.ESCAPE.isUp() 
						&& leftMotor.getTachoCount() <= slowTachoCount) 
					slowLineTrace();
				break;
			}
		}
		SoundThread.setSearchModeOFF();
	}
	
	/**
	 * 目標周回完了から短絡走行完了まで
	 */
	private void areaB() {
		
		//-- 一定距離は目標検知をせずに高速走行
		int quickTachoCount = 5000;
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= quickTachoCount ) {
			quickLineTrace();
		}
		
		//-- 指定距離だけ目標検知を行い、検知できれば短絡走行。指定距離内で検知できなくても強引に短絡走行
		int searchTachoCount = 500;
		SoundThread.setSearchModeON();
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= searchTachoCount 
				&& ! SensorThread.isFind(0.05F, 0.4F) ) {
			slowLineTrace();
		}
		shortCircuit();
	}
	
	/**
	 * 短絡走行完了からゴールまで
	 */
	private void areaC() {

		//-- 黒線に確実に復帰するよう一定距離は低速走行
		int slowTachoCount = 500;
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= slowTachoCount) 
			slowLineTrace();

		//-- 車庫近くまで高速走行
		int quickTachoCount = 3000;
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= quickTachoCount) 
			quickLineTrace();
		
		//-- 車庫を検知するまで低速走行
		while ( Button.ESCAPE.isUp() && ! isGarage ) {
			slowLineTrace();
		}
		
		//-- 車庫入出
		garageIO();
		
		//-- 接触センサに反応があるまで高速走行
		while ( Button.ESCAPE.isUp() && ! touch.isPressed()) {
			quickLineTrace();
		}

	}
	
	//===============================================
	//	黒線追跡
	//===============================================
	/**
	 * 高速黒線追跡
	 */
	private static void quickLineTrace() {
		final int highSpeed = 700;
		final int lowSpeed = 450;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
	/**
	 * 低速黒線追跡
	 */
	private static final void slowLineTrace() {
		final int highSpeed = 400;
		final int lowSpeed = 150;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
	/**
	 * センサスレッドで判定した結果からモータの速さを調整し黒線追跡
	 * @param highSpeed モータの回転速度
	 * @param lowSpeed モータの回転速度
	 */
	private static void lineTrace(int highSpeed, int lowSpeed) {
		runState = LINE;
		switch (SensorThread.dirDecision()) {
		case FRONT:
			motorSetSpeed(highSpeed, highSpeed);
			break;
		case LEFT:
			motorSetSpeed(lowSpeed, highSpeed);
			break;
		case RIGHT:
			motorSetSpeed(highSpeed, lowSpeed);
			break;
		case BOTH:
			isGarage = ! isGarage;
			break;
		default:
			break;
		}
		motorForward();
	}
		
	//===============================================
	//	目標周回
	//===============================================
	/**
	 * 目標周回
	 */
	private void targetSurrounding() {
		//-- 目標周回
		surrounding();
		//-- 黒線復帰
		recoveryLineTrace();
	}
	
	/**
	 * 周回
	 */
	private void surrounding() {
		runState = SURROUND;
		int leftSpeed = 540;
		int rightSpeed = 750;
		int runTachoCount = 3200;
		
		//-- モータの回転数を指定し、一定距離だけ曲進
		motorSetSpeed(leftSpeed, rightSpeed);
		leftMotor.resetTachoCount();
		while (leftMotor.getTachoCount() <= runTachoCount 
				&& Button.ESCAPE.isUp()) {
			motorForward();
		}
	}
	
	/**
	 * 黒線復帰
	 */
	private void recoveryLineTrace() {
		runState = RECOVERY;
		final int speed = 500;
		int recoveryTachoCount = 500;
		
		//-- 左の色彩センサが黒を検知するか、一定距離進むまで直進
		motorSetSpeed(speed, speed);
		leftMotor.resetTachoCount();
		while ( SensorThread.dirDecision() != Direction.LEFT 
				&& Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= recoveryTachoCount) {
			motorForward();
		}
	}

	//===============================================
	//	短絡走行
	//===============================================
	/**
	 * 	短絡走行
	 */
	private void shortCircuit() {
		runState = SHORT;
		int leftSpeed = 400;
		int rightSpeed = 600;
		int runTachoCount = 500;
		
		//-- モータの回転数を指定し、一定距離だけ曲進
		motorSetSpeed(leftSpeed, rightSpeed);
		leftMotor.resetTachoCount();
		while (leftMotor.getTachoCount() <= runTachoCount 
				&& Button.ESCAPE.isUp()) {
			motorForward();
		}
		
		//-- 黒線復帰
		recoveryLineTrace();
	}
	
	//===============================================
	//	車庫入出
	//===============================================
	/**
	 * 車庫入出
	 */
	private void garageIO() {
		int backTachoCount;
		garageIN();
		backTachoCount = leftMotor.getTachoCount();
		garageOUT(backTachoCount);
	}
	
	/**
	 * 車庫入れ(向き調整から青線検知まで)
	 */
	private static void garageIN() {
		runState = GARAGE_IN;
		
		//-- 灰4を検知するまで向き調整しながら前進
		SoundThread.setHarmonyModeON();
		while ( Button.ESCAPE.isUp() 
				&& SensorThread.getLeftColor() != SensorColor.GRAY4 
				&& SensorThread.getRightColor() != SensorColor.GRAY4 ) {
			adjustmentRun(false);
		}

		//-- 一定距離以上進み、灰3を検知するまで向き調整しながら前進
		int tachoCount = 150;
		SoundThread.setHarmonyModeOFF();
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& SensorThread.getLeftColor() != SensorColor.GRAY3 
				&& SensorThread.getRightColor() != SensorColor.GRAY3 
				|| leftMotor.getTachoCount() <= tachoCount) {
			adjustmentRun(false);
		}
		
		//-- 左に一定距離回転(⇒一定距離+左が灰3を検知するまでに変更?)
		int garageSpeed = 200;
		int turnTachoCount = 170;
		motorSetSpeed(garageSpeed, garageSpeed);
		rightMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& rightMotor.getTachoCount() <= turnTachoCount ) {
			turnLeft();
		}
		
		//-- 青をどちらかの色彩センサが検知するまで直進(⇒灰4の範囲で進むように変更?)
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() ) {
			if ( SensorThread.getLeftColor() == SensorColor.BLUE 
					|| SensorThread.getRightColor() == SensorColor.BLUE ) 
				break;
			motorForward();
		}
	}
	
	/**
	 * 車庫だし(後進から黒線復帰まで)
	 * @param backTachoCount 後進する距離
	 */
	private static void garageOUT(int backTachoCount) {
		runState = GARAGE_OUT;

		//-- 進んだ距離だけ更新
		int garageSpeed = 200;
		rightMotor.resetTachoCount();
		motorSetSpeed(garageSpeed, garageSpeed);
		while ( Button.ESCAPE.isUp() 
				&& -rightMotor.getTachoCount() <= backTachoCount ) {
			motorBackward();
		}
		
		//-- 右に一定距離だけ回転(⇒一定距離+左が灰3を検知するまでに変更?)
		int turnTachoCount = 170;
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= turnTachoCount ) {
			turnRight();
		}
		
		//-- 左右の色彩センサの少なくとも一方が城を検知するまで向き調整しながら前進
		boolean outLeft = false;
		boolean outRight = false;
		SoundThread.setHarmonyModeON();
		while ( Button.ESCAPE.isUp() ) {
			if ( SensorThread.getLeftColor() == SensorColor.WHITE ) {
				outLeft = true;
			}
			if ( SensorThread.getRightColor() == SensorColor.WHITE ) {
				outRight = true;
			}
			if ( outLeft || outRight ) break;
			adjustmentRun(true);
		}
		
		//-- 指定距離または、白を検知していない色彩センサが白を検知するまで左右反転黒線追跡
		int reverseTachoCount = 200;
		SoundThread.setHarmonyModeOFF();
		leftMotor.resetTachoCount();
		if ( outLeft && ! outRight) {
			while ( SensorThread.getRightColor() != SensorColor.WHITE 
					&& leftMotor.getTachoCount() <= reverseTachoCount ) 
				reverseLineTrace();
		} else if ( ! outLeft && outRight ) {
			while ( SensorThread.getLeftColor() != SensorColor.WHITE 
					&& leftMotor.getTachoCount() <= reverseTachoCount ) 
				reverseLineTrace();
		}
	}
	
	/**
	 * 向き調整しながら前進する。車庫入れか車庫出しかで判定を変える
	 * @param parkFinished 駐車が完了しているか
	 */
	private static void adjustmentRun(boolean parkFinished) {
		int highSpeed = 300;
		int lowSpeed = 150;
		switch (SensorThread.posDicision(parkFinished)) {
		case FRONT:
			motorSetSpeed(highSpeed, highSpeed);
			break;
		case LEFT:
			motorSetSpeed(lowSpeed, highSpeed);
			break;
		case RIGHT:
			motorSetSpeed(highSpeed, lowSpeed);
			break;
		default:
			break;
		}
		motorForward();
	}

	/**
	 * 左右の判定を逆にした黒線追跡
	 */
	private static void reverseLineTrace() {
		int highSpeed = 300;
		int lowSpeed = 200;
		runState = LINE;
		switch (SensorThread.dirDecision()) {
		case FRONT:
			motorSetSpeed(highSpeed, highSpeed);
			break;
		case LEFT:
			motorSetSpeed(lowSpeed, highSpeed);
			break;
		case RIGHT:
			motorSetSpeed(highSpeed, lowSpeed);
			break;
		default:
			break;
		}
		motorForward();
	}

	//===============================================
	//	基本走行制御
	//===============================================
	/**
	 * モータの回転速度を設定
	 * @param leftMotorSpeed 左側モータの回転速度
	 * @param rightMotorSpeed 右側モータの回転速度
	 */
	private static void motorSetSpeed(int leftMotorSpeed, 
			int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	/**
	 * 前進する
	 */
	private static void motorForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	/**
	 * 後進する
	 */
	private static void motorBackward() {
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**
	 * 左に回転する
	 */
	private static void turnLeft() {
		leftMotor.backward();
		rightMotor.forward();
	}
	
	/**
	 * 右に回転する
	 */
	private static void turnRight() {
		leftMotor.forward();
		rightMotor.backward();
	}
	
	/**
	 * モータを停止
	 */
	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
		runState = STOP;
	}

	/**
	 * 接触センサに反応があるまで待機
	 */
	private static void touchPressWait(){
		while(! touch.isPressed()){}
		while(touch.isPressed()){}
	}
	
	/**
	 * エンターボタンが押されるまで待機
	 */
	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}
}
