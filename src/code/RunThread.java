package code;

import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable{

	private static RegulatedMotor leftMotor  = Motor.C;
	private static RegulatedMotor rightMotor  = Motor.B;

	@Override
	public void run() {
		goStraight(18);
		turn(23);
		goStraight(53);
		turn(55);
		bigCircle(700, 520, 95);
		smallCircle(700, 480, 134);
		leftMotor.stop();
		rightMotor.stop();
	}

	public void goStraight(int time) {
		leftMotor.setSpeed(600);
		rightMotor.setSpeed(600);
		while (TimeThread.getTime() <= time) {
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	public void turn(int time) {
		leftMotor.setSpeed(300);
		rightMotor.setSpeed(300);
		while (TimeThread.getTime() <= time) {
			leftMotor.backward();
			rightMotor.forward();
		}
	}

	public void  bigCircle(int rSpeed, int lSpeed, int time) {
		leftMotor.setSpeed(lSpeed);
		rightMotor.setSpeed(rSpeed);
		while (TimeThread.getTime() <= time) {
			rightMotor.forward();
			leftMotor.forward();
		}
		leftMotor.setSpeed(300);
		rightMotor.setSpeed(300);
		while (TimeThread.getTime() <= time+11) {
			rightMotor.backward();
			leftMotor.forward();
		}
		rightMotor.stop(true);
		leftMotor.stop();
	}

	public void smallCircle(int rSpeed, int lSpeed, int time) {
		leftMotor.setSpeed(lSpeed);
		rightMotor.setSpeed(rSpeed);
		while (TimeThread.getTime() <= time) {
			rightMotor.forward();
			leftMotor.forward();
		}
		rightMotor.stop(true);
		leftMotor.stop();
	}

}
