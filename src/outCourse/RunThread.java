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
	static boolean isRun = true;
	static boolean isGarage = false;
	
	@Override
	public void run() {
		//areaA();
		//areaB();
		areaC();
		motorStop();
	}
	
	//===============================================
	//	エリア分け
	//===============================================
	private void areaA() {
		while ( Button.ESCAPE.isUp() && isRun) {
			quickLineTrace();
			//slowLineTrace();
			if ( SensorThread.isFind(0.05F, 0.4F) ) {
				targetSurrounding();
				break;
			}
		}
	}
	
	private void areaB() {
		int quickTachoCount = 4000;
		
		leftMotor.resetTachoCount();
		while ( Button.ESCAPE.isUp() && isRun) {
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
		/*while ( Button.ESCAPE.isUp() && ! isGarage ) {
			quickLineTrace();
		}*/
		garageIO();
		/*while ( Button.ESCAPE.isUp() && ! touch.isPressed()) {
			quickLineTrace();
		}*/
	}
	
	//===============================================
	//	黒線追跡
	//===============================================
	private static void quickLineTrace() {
		final int highSpeed = 600;
		final int lowSpeed = 420;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
	private static final void slowLineTrace() {
		final int highSpeed = 300;
		final int lowSpeed = 140;
		
		lineTrace(highSpeed, lowSpeed);
	}
	
	private static void lineTrace(int highSpeed, int lowSpeed) {
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
		final int speed = 500;
		motorSetSpeed(speed, speed);
		while ( SensorThread.dirDecision() != Direction.LEFT && Button.ESCAPE.isUp()) {
			motorForward();
		}
	}

	//===============================================
	//	短絡走行
	//===============================================
	private void shortCircuit() {
		int leftSpeed = 400;
		int rightSpeed = 600;
		int runTachoCount = 800;
		
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
		while ( Button.ESCAPE.isUp() ) {
			adjustmentRun(false);
		}
		/*while ( SensorThread.getColor() == SensorColor.GRAY4 ) {
			adjustmentRun(false);
			motorStop();
		}
		while ( SensorThread.getColor() == SensorColor.BLACK ) {
			adjustmentRun(true);
		}*/
	}
	
	private static void adjustmentRun(boolean parkFinished) {
		int highSpeed = 0;
		int lowSpeed = 0;
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
	
	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}


}
