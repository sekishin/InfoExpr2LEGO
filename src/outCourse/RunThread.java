package outCourse;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	
	static final int highSpeed = 600;
	static final int lowSpeed = 250;
	
	@Override
	public void run() {
		while ( Button.ESCAPE.isUp() ) {
			lineTrace();
		}
		motorStop();
	}
	
	private static void lineTrace() {
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
