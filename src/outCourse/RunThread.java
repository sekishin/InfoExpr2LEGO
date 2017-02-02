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
	static boolean isGarage = false;
	static boolean isWait = true;
	
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
		//areaA();
		//areaB();
		areaC();
		//test();
		motorStop();
	}
	
	private void test() {
		while ( Button.ESCAPE.isUp() ) {
			motorSetSpeed(50, 50);
			motorForward();
		}
	}
	//===============================================
	//	エリア分け
	//===============================================
	private void areaA() {
		while ( Button.ESCAPE.isUp() ) {
			quickLineTrace();
			if ( SensorThread.isFind(0.05F, 0.4F) ) {
				targetSurrounding();
				break;
			}
		}
	}
	
	private void areaB() {
		int quickTachoCount = 4300;
		//int quickTachoCount = 0;

		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() ) {
			if ( leftMotor.getTachoCount() <= quickTachoCount) quickLineTrace();
			else  {
				slowLineTrace();
				if ( SensorThread.isFind(0.1F, 0.4F) ) {
					shortCircuit();
					break;
				}
			}
		}
	}
	
	private void areaC() {
		int quickTachoCount = 3000;
		int slowTachoCount = 500;

		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && leftMotor.getTachoCount() <= slowTachoCount) slowLineTrace();

		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && leftMotor.getTachoCount() <= quickTachoCount) quickLineTrace();

		while ( Button.ESCAPE.isUp() && ! isGarage ) {
			slowLineTrace();
		}
		garageIO();
		while ( Button.ESCAPE.isUp() && ! touch.isPressed()) {
			quickLineTrace();
		}

	}
	
	//===============================================
	//	黒線追跡
	//===============================================
	private static void quickLineTrace() {
		final int highSpeed = 600;
		final int lowSpeed = 400;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
	private static final void slowLineTrace() {
		final int highSpeed = 300;
		final int lowSpeed = 100;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
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
	private void targetSurrounding() {
		surrounding();
		recoveryLineTrace();
	}
	
	private void surrounding() {
		runState = SURROUND;
		int leftSpeed = 430;
		int rightSpeed = 600;
		int runTachoCount = 3200;
		
		motorSetSpeed(leftSpeed, rightSpeed);
		leftMotor.resetTachoCount();
		while (leftMotor.getTachoCount() <= runTachoCount && Button.ESCAPE.isUp()) {
			motorForward();
		}
		motorStop();

	}
	
	private void recoveryLineTrace() {
		runState = RECOVERY;
		final int speed = 500;
		int recoveryTachoCount = 500;
		motorSetSpeed(speed, speed);
		leftMotor.resetTachoCount();
		while ( SensorThread.dirDecision() != Direction.LEFT && Button.ESCAPE.isUp() 
				&& leftMotor.getTachoCount() <= recoveryTachoCount) {
			motorForward();
		}
	}

	//===============================================
	//	短絡走行
	//===============================================
	private void shortCircuit() {
		runState = SHORT;
		int leftSpeed = 400;
		int rightSpeed = 600;
		int runTachoCount = 500;
		
		motorSetSpeed(leftSpeed, rightSpeed);
		leftMotor.resetTachoCount();
		while (leftMotor.getTachoCount() <= runTachoCount && Button.ESCAPE.isUp()) {
			motorForward();
		}
		motorStop();
		
		recoveryLineTrace();
	}
	
	//===============================================
	//	車庫入出
	//===============================================
	private void garageIO() {
		runState = GARAGE_IN;
		int reverseTachoCount = 200;
		int turnTachoCount = 170;
		int backTachoCount;
		boolean outLeft = false;
		boolean outRight = false;
		SoundThread.setHarmonyModeON();
		while ( Button.ESCAPE.isUp() && SensorThread.getLeftColor() != SensorColor.GRAY4 && SensorThread.getRightColor() != SensorColor.GRAY4 ) {
			adjustmentRun(false);
		}
		motorStop();
		SoundThread.setHarmonyModeOFF();
		while ( Button.ESCAPE.isUp() && SensorThread.getLeftColor() != SensorColor.GRAY3 && SensorThread.getRightColor() != SensorColor.GRAY3 ) {
			adjustmentRun(false);
		}
		motorStop();
		// 左回転
		motorSetSpeed(200, 200);
		rightMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && rightMotor.getTachoCount() <= turnTachoCount ) {
			turnLeft();
		}
		
		// 直進
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() ) {
			if ( SensorThread.getLeftColor() == SensorColor.BLUE || SensorThread.getRightColor() == SensorColor.BLUE ) break;
			motorForward();
		}
		backTachoCount = leftMotor.getTachoCount();
		
		// 後進
		rightMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && -rightMotor.getTachoCount() <= backTachoCount ) {
			motorBackward();
		}
		
		// 右回転
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && leftMotor.getTachoCount() <= turnTachoCount ) {
			turnRight();
		}
		
		runState = GARAGE_OUT;
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
		SoundThread.setHarmonyModeOFF();
		leftMotor.resetTachoCount();
		if ( outLeft && ! outRight) {
			while ( SensorThread.getRightColor() != SensorColor.WHITE && leftMotor.getTachoCount() <= reverseTachoCount ) reverseLineTrace();
		} else if ( ! outLeft && outRight ) {
			while ( SensorThread.getLeftColor() != SensorColor.WHITE  && leftMotor.getTachoCount() <= reverseTachoCount ) reverseLineTrace();
		}
	}
	
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
	private static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	private static void motorForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	private static void motorBackward() {
		leftMotor.backward();
		rightMotor.backward();
	}
	
	private static void turnLeft() {
		leftMotor.backward();
		rightMotor.forward();
	}
	
	private static void turnRight() {
		leftMotor.forward();
		rightMotor.backward();
	}
	
	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
		runState = STOP;
	}

	private static void touchPressWait(){
		while(! touch.isPressed()){}
		while(touch.isPressed()){}
	}
	
	private static void enterPressWait(){
		while(Button.ENTER.isUp()){}
		while(Button.ENTER.isDown()){}
	}



}
