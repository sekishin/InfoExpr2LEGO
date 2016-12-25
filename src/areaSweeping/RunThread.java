package areaSweeping;

import java.util.Random;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	private static int lowSpeed = 300;
	private static int highSpeed = 600;
	private static int veryHighSpeed = 800;
	private static Random rand = new Random();
	private static final int MAX_TURN_TACHO_COUNT = 600;
	private static final int MAX_BACK_TACHO_COUNT = 50;
	private static final int SWING_WIDYH = 100;
	private static boolean goleft = true;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			switch (SensorThread.getSensor()) {
			case STRAIGHT:
				motorSetSpeed(highSpeed, highSpeed);
				moterBackward(randomTachoCount(MAX_BACK_TACHO_COUNT));
				rightTurn(randomTachoCount(MAX_TURN_TACHO_COUNT));
				break;
			case LEFT:
				motorSetSpeed(lowSpeed, highSpeed);
				moterBackward(randomTachoCount(MAX_BACK_TACHO_COUNT));
				break;
			case RIGHT:
				motorSetSpeed(highSpeed, lowSpeed);
				moterBackward(randomTachoCount(MAX_BACK_TACHO_COUNT));
				break;
			default:
				motorSetSpeed(veryHighSpeed, veryHighSpeed);
				moterForward();
				break;
			}
		}
		motorStop();
	}
	
	public int randomTachoCount(int max) {
		return rand.nextInt(max) + 1;
	}

	public static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	public static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}

	public void resetTachoCount() {
		rightMotor.resetTachoCount();
		leftMotor.resetTachoCount();
	}
	
	public int getTachoCount() {
		return Math.abs(leftMotor.getTachoCount());
	}
	
	public void rightTurn(int tc) {
		resetTachoCount();
		motorSetSpeed(highSpeed, highSpeed);
		while ( getTachoCount() <= tc ) {
			leftMotor.forward();
			rightMotor.backward();
		}
	}
	
	public void leftTurn(int tc) {
		resetTachoCount();
		motorSetSpeed(highSpeed, highSpeed);
		while ( getTachoCount() <= tc ) {
			leftMotor.forward();
			rightMotor.backward();
		}
	}
	
	public void moterBackward(int tc) {
		resetTachoCount();
		while ( getTachoCount() <= tc ) {
			leftMotor.forward();
			rightMotor.backward();
		}
	}
	
	public void moterForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void headShake() {
		if ( goleft ) motorSetSpeed(lowSpeed, highSpeed);
		else motorSetSpeed(highSpeed, lowSpeed);
		moterForward();
		if ( getTachoCount() >= SWING_WIDYH ) goleft = ! goleft;
	}

}
