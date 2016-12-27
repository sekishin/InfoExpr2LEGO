package figureTrace;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable{

	private static RegulatedMotor leftMotor  = Motor.C;
	private static RegulatedMotor rightMotor  = Motor.B;
	/*	スピード定数	*/
	private static final int SPEED_FOR_STRAIGHT = 700;
	private static final int SPEED_FOR_TURN = 500;
	private static final int RIGHT_SPEED_FOR_BIG_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_BIG_CIRCLE = 520;
	private static final int RIGHT_SPEED_FOR_SMALL_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_SMALL_CIRCLE = 470;
	/*	移動距離定数	*/
	//private static final int L1_FINISH_TIME = 19;
	private static final int L1_TACHO_COUNT = 1550;
	private static final int V1_TACHO_COUNT = 170;
	//private static final int L2_FINISH_TIME = 31;
	public static final int L2_TACHO_COUNT = 1950;
	private static final int V2_TACHO_COUNT = 85;
	//private static final int C1_FINISH_TIME = 42;
	private static final int C1_TACHO_COUNT = 2150;
	private static final int V3_TACHO_COUNT = 320;
	//private static final int C2_FINISH_TIME = 30;
	private static final int C2_TACHO_COUNT = 1350;
	

	@Override
	public void run() {
		goStraightUnit();
		goCircleUnit();
	}

	public void goStraightUnit() {
		goStraight(L1_TACHO_COUNT);
		turn(V1_TACHO_COUNT);
		SoundThread.setBeep();
		goStraight(L2_TACHO_COUNT);
		turn(V2_TACHO_COUNT);
	}

	public void goCircleUnit() {
		goCircle(RIGHT_SPEED_FOR_BIG_CIRCLE, LEFT_SPEED_FOR_BIG_CIRCLE, C1_TACHO_COUNT);
		turn(V3_TACHO_COUNT);
		goCircle(RIGHT_SPEED_FOR_SMALL_CIRCLE, LEFT_SPEED_FOR_SMALL_CIRCLE, C2_TACHO_COUNT);
	}

	/*public void goStraight(int time) {
		setMoterSpeed(SPEED_FOR_STRAIGHT, SPEED_FOR_STRAIGHT);
		TimeThread.resetTime();
		while (TimeThread.getTime() <= time) {
			leftMotor.forward();
			rightMotor.forward();
		}
		stopMoter();
	}*/
	
	public void goStraight(int tc) {
		setMoterSpeed(SPEED_FOR_STRAIGHT, SPEED_FOR_STRAIGHT);
		resetTachoCount();
		while ( getTachoCount() <= tc) {
			moterForward();
		}
		stopMoter();
	}

	public void turn(int tc) {
		setMoterSpeed(SPEED_FOR_TURN, SPEED_FOR_TURN);
		resetTachoCount();
		while (getTachoCount() <= tc) {
			leftMotor.backward();
			rightMotor.forward();
		}
		stopMoter();
	}

	/*public void  goCircle(int rSpeed, int lSpeed, int time) {
		setMoterSpeed(rSpeed, lSpeed);
		TimeThread.resetTime();
		while (TimeThread.getTime() <= time) {
			leftMotor.forward();
			rightMotor.forward();
		}
		stopMoter();
	}*/
	
	public void goCircle(int rSpeed, int lSpeed, int tc) {
		setMoterSpeed(rSpeed, lSpeed);
		resetTachoCount();
		while ( getTachoCount() <= tc ) {
			moterForward();
		}
		stopMoter();
	}

	public void setMoterSpeed(int rSpeed, int lSpeed) {
		leftMotor.setSpeed(lSpeed);
		rightMotor.setSpeed(rSpeed);
	}

	public void stopMoter() {
		moterBackward();
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
	public void resetTachoCount() {
		rightMotor.resetTachoCount();
		leftMotor.resetTachoCount();
	}
	
	public static int getTachoCount() {
		LCD.clear();
		LCD.drawString(Integer.toString(Math.abs(leftMotor.getTachoCount())), 0, 5);
		return Math.abs(leftMotor.getTachoCount());
	}
	
	public void moterForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void moterBackward() {
		leftMotor.backward();
		rightMotor.backward();
	}

}
