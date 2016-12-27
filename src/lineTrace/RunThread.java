package lineTrace;

import jp.ac.kagawa_u.infoexpr.Sensor.TouchSensor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	static int leftLowSpeed = 300;	// 左折時の左モーターの速度
	static int rightLowSpeed = 400;	// 右折時の右モーターの速度
	static int highSpeed = 600;	// 直進時のモーターの速度
	static int rightHighSpeed = 850;	// 左折時の右モーターの速度
	static int leftHighSpeed = 750;	// 右折時の左モーターの速度
	static int turnTachoCount = 720;	// 回転時の回転角度
	static long lastRedDetectionTime = 0;
	static long redDetectionInterval = 10000;


	@Override
	public void run() {
		while (! touch.isPressed() ) {
			lineTrace();
			if ( SensorThread.isRed() ) turn();
		}
		motorStop();
	}

	private static void lineTrace() {
		switch (SensorThread.getSensor()) {
		case STRAIGHT:
			motorSetSpeed(highSpeed, highSpeed);
			motorForward();
			break;
		case LEFT:
			motorSetSpeed(leftLowSpeed, rightHighSpeed);
			motorForward();
			break;
		case RIGHT:
			motorSetSpeed(leftHighSpeed, rightLowSpeed);
			motorForward();
			break;
		default:
			break;
		}
	}

	private static void turn() {
		if ( System.currentTimeMillis() > lastRedDetectionTime + redDetectionInterval ) {
			leftMotor.resetTachoCount();
			motorSetSpeed(highSpeed, highSpeed);
			while (leftMotor.getTachoCount() <= turnTachoCount) {
				motorTurn();
			}
			motorStop();
			lastRedDetectionTime = System.currentTimeMillis();
		}
	}

	private static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	private static void motorForward() {
		leftMotor.forward();
		rightMotor.forward();
	}

	private static void motorTurn() {
		leftMotor.forward();
		rightMotor.backward();
	}

	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}

}
