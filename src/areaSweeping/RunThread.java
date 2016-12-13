package areaSweeping;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	static int leftLowSpeed = 300;
	static int rightLowSpeed = 400;
	static int highSpeed = 600;
	static int veryHighSpeed = 1000;
	static int rightHighSpeed = 850;
	static int leftHighSpeed = 750;
	static int turnTachoCount = 730;
	static int backTime = 500;
	static int turnTime = 300;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			switch (SensorThread.getSensor()) {
			case STRAIGHT:
				motorSetSpeed(highSpeed, highSpeed);
				motorBackward(backTime);
				motorTurn(turnTime, Distination.RIGHT);
				//goStraight();
				break;
			case LEFT:
				motorSetSpeed(leftLowSpeed, rightHighSpeed);
				motorBackward(backTime);
				motorTurn(turnTime, Distination.LEFT);
				//goStraight();
				break;
			case RIGHT:
				motorSetSpeed(leftHighSpeed, rightLowSpeed);
				motorBackward(backTime);
				motorTurn(turnTime, Distination.RIGHT);
				//goStraight();
				break;
			default:
				goStraight();
				break;
			}
		}
		motorStop();
	}

	private static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
		// leftMotor.setSpeed(0);
		// rightMotor.setSpeed(0);
	}

	private static void goStraight() {
		motorSetSpeed(highSpeed, highSpeed);
		motorForward(1);
	}

	private static void motorForward(int time) {
		int t = 0;
		while (t < time) {
			leftMotor.forward();
			rightMotor.forward();
			t++;
		}
	}

	private static void motorBackward(int time) {
		int t = 0;
		while ( t < time) {
			leftMotor.backward();
			rightMotor.backward();
			t++;
		}
	}

	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}

	private static void motorTurn(int time, Distination d) {
		int t = 0;
		motorSetSpeed(highSpeed, highSpeed);
		while ( t < time ) {
			if ( d == Distination.LEFT) {
				leftMotor.forward();
				rightMotor.backward();
			} else {
				leftMotor.backward();
				rightMotor.forward();
			}
			t++;
		}
	}

}
