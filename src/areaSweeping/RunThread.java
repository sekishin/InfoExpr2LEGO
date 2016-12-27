package areaSweeping;

import java.util.Random;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;
	static RegulatedMotor leftMotor = Motor.C;
	private static int lowSpeed = 400;
	private static int highSpeed = 700;
	private static int veryHighSpeed = 600;
	private static int veryLowSpeed = 100;
	private static Random rand = new Random();
	private static final int BACK_TACHO_COUNT = 200;
	private static final int TURN_TACHO_COUNT = 240;
	private static final int SWING_WIDYH = 150;
	private static boolean goleft = true;

	@Override
	public void run() {
		resetTachoCount();
		while ( ! Button.ESCAPE.isDown() ) {
			switch (SensorThread.getSensor()) {
			case STRAIGHT:
				motorSetSpeed(highSpeed, highSpeed);
				moterBackward(BACK_TACHO_COUNT);
				if ( rand.nextInt(100) % 2 == 0 ) rightTurn(randomTachoCount(TURN_TACHO_COUNT));
				else leftTurn(randomTachoCount(TURN_TACHO_COUNT));
				break;
			case LEFT:
				motorSetSpeed(lowSpeed, highSpeed);
				moterBackward(BACK_TACHO_COUNT);
				rightTurn(randomTachoCount(TURN_TACHO_COUNT));
				break;
			case RIGHT:
				motorSetSpeed(highSpeed, lowSpeed);
				moterBackward(BACK_TACHO_COUNT);
				leftTurn(randomTachoCount(TURN_TACHO_COUNT));
				break;
			default:
				motorSetSpeed(highSpeed, highSpeed);
				//moterForward();
				headShake();
				break;
			}
		}
		motorStop();
	}
	
	
	public static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	public static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}

	public int randomTachoCount(int w) {
		return rand.nextInt(w+1);
	}

	public void resetTachoCount() {
		rightMotor.resetTachoCount();
		leftMotor.resetTachoCount();
	}
	
	public int getTachoCount() {
		return Math.max(Math.abs(leftMotor.getTachoCount()), Math.abs(rightMotor.getTachoCount()));
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
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	
	public void moterBackward(int tc) {
		resetTachoCount();
		while ( getTachoCount() <= tc ) {
			leftMotor.backward();
			rightMotor.backward();
		}
	}
	
	public void moterForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void headShake() {
		if ( goleft ) motorSetSpeed(veryLowSpeed, veryHighSpeed);
		else motorSetSpeed(veryHighSpeed, veryLowSpeed);
		moterForward();
		if ( getTachoCount() >= SWING_WIDYH ) {
			goleft = ! goleft;
			resetTachoCount();
		}
	}

}
