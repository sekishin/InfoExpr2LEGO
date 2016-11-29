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

}
