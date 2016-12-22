package figureTrace;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable{

	private static RegulatedMotor leftMotor  = Motor.C;
	private static RegulatedMotor rightMotor  = Motor.B;
	private static final int SPEED_FOR_STRAIGHT = 600;
	private static final int SPEED_FOR_TURN = 350;
	private static final int RIGHT_SPEED_FOR_BIG_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_BIG_CIRCLE = 515;
	private static final int RIGHT_SPEED_FOR_SMALL_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_SMALL_CIRCLE = 480;
	private static final int L1_FINISH_TIME = 19;
	private static final int V1_FINISH_TIME = L1_FINISH_TIME + 5;
	private static final int V1_TACHO_COUNT = 160;
	private static final int L2_FINISH_TIME = V1_FINISH_TIME + 32;
	private static final int V2_FINISH_TIME = L2_FINISH_TIME + 2;
	private static final int V2_TACHO_COUNT = 80;
	private static final int C1_FINISH_TIME = V2_FINISH_TIME + 40;
	private static final int V3_FINISH_TIME = C1_FINISH_TIME + 11;
	private static final int V3_TACHO_COUNT = 720;
	private static final int C2_FINISH_TIME = V3_FINISH_TIME + 33;

	@Override
	public void run() {
		while ( ! Button.ESCAPE.isDown() ) {
			//goStraightUnit();
			goCircleUnit();
		}
	}

	public void goStraightUnit() {
		goStraight(L1_FINISH_TIME);
		turn(V1_FINISH_TIME, V1_TACHO_COUNT);
		goStraight(L2_FINISH_TIME);
		turn(V2_FINISH_TIME, V2_TACHO_COUNT);
	}

	public void goCircleUnit() {
		//TimeThread.setTime(V2_FINISH_TIME);
		//while ( TimeThread.getTime() <= V2_FINISH_TIME );
		//goCircle(RIGHT_SPEED_FOR_BIG_CIRCLE, LEFT_SPEED_FOR_BIG_CIRCLE, C1_FINISH_TIME);
		turn(V3_FINISH_TIME, V3_TACHO_COUNT);
		//goCircle(RIGHT_SPEED_FOR_SMALL_CIRCLE, LEFT_SPEED_FOR_SMALL_CIRCLE, C2_FINISH_TIME);
	}

	public void goStraight(int time) {
		setMoterSpeed(SPEED_FOR_STRAIGHT, SPEED_FOR_STRAIGHT);
		while (TimeThread.getTime() <= time) {
			leftMotor.forward();
			rightMotor.forward();
		}
		stopMoter();
	}

	public void turn(int time, int tc) {
		setMoterSpeed(SPEED_FOR_TURN, SPEED_FOR_TURN);
		rightMotor.resetTachoCount();
		while (rightMotor.getTachoCount() <= tc) {
			leftMotor.backward();
			rightMotor.forward();
		}
		stopMoter();
	}

	public void  goCircle(int rSpeed, int lSpeed, int time) {
		setMoterSpeed(rSpeed, lSpeed);
		while (TimeThread.getTime() <= time) {
			rightMotor.forward();
			leftMotor.forward();
		}
		stopMoter();
	}

	public void setMoterSpeed(int rSpeed, int lSpeed) {
		leftMotor.setSpeed(lSpeed);
		rightMotor.setSpeed(rSpeed);
	}

	public void stopMoter() {
		rightMotor.stop(true);
		leftMotor.stop();
	}

}
