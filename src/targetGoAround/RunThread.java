package targetGoAround;

import jp.ac.kagawa_u.infoexpr.Sensor.TouchSensor;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	static int leftLowSpeed = 300;	// 左折時の左モーターの速度
	static int rightLowSpeed = 400;	// 右折時の右モーターの速度
	static int highSpeed = 400;	// 直進時のモーターの速度
	static int rightHighSpeed = 850;	// 左折時の右モーターの速度
	static int leftHighSpeed = 750;	// 右折時の左モーターの速度
	static int turnTachoCount = 720;	// 回転時の回転角度
	static long lastRedDetectionTime = 0;
	static long redDetectionInterval = 10000;
	
	static int straightTachoCount;
	static final int TURN_TACHO_COUNT = 80;
	static float distance;
	static float diff = 0.05F;


	@Override
	public void run() {
		while ( ! SensorThread.isFind() && ! Button.ESCAPE.isDown() ) {
			lineTrace();
		}
		straightTachoCount = calcTaxhoCount(SensorThread.getDistance());
		//distance = SensorThread.getDistance();
		goAround();
		recovery();
		while ( ! Button.ESCAPE.isDown() ) {
			lineTrace();
		}
		motorStop();
	}

	private static void lineTrace() {
		switch (SensorThread.getDistination()) {
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
	
	private void recovery() {
		motorSetSpeed(highSpeed, highSpeed);
		while ( SensorThread.getDistination() != Distination.LEFT &&  ! Button.ESCAPE.isDown() ) {
			motorForward();
		}
	}
	private static int calcTaxhoCount(float d) {
		return (int) (d / 0.18 * 360);
	}
	
	private static void goAround() {
		for (int i = 0; i < 6; i++ ) {
			leftMotor.resetTachoCount();
			motorSetSpeed(highSpeed, highSpeed);
			if ( i == 0 ) {
				while ( leftMotor.getTachoCount() <= straightTachoCount-100 && ! Button.ESCAPE.isDown() ) {
					motorForward();
				}
			} else {
				while ( leftMotor.getTachoCount() <= straightTachoCount && ! Button.ESCAPE.isDown() ) {
					motorForward();
				}
			}
			
			if ( Button.ESCAPE.isDown() ) break;
			rightMotor.resetTachoCount();
			while ( rightMotor.getTachoCount() <= TURN_TACHO_COUNT && ! Button.ESCAPE.isDown() ) {
				motorTurn();
			}
			if ( Button.ESCAPE.isDown() ) break;
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
		leftMotor.backward();
		rightMotor.forward();
	}

	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}

}
