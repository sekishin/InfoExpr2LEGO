package figureTrace;

import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable{

	private static RegulatedMotor leftMotor  = Motor.C;
	private static RegulatedMotor rightMotor  = Motor.B;
	private static final int SPEED_FOR_STRAIGHT = 600;
	private static final int SPEED_FOR_TURN = 300;
	private static final int RIGHT_SPEED_FOR_BIG_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_BIG_CIRCLE = 520;
	private static final int RIGHT_SPEED_FOR_SMALL_CIRCLE = 700;
	private static final int LEFT_SPEED_FOR_SMALL_CIRCLE = 480;
	private static final int L1_FINISH_TIME = 18;
	private static final int V1_FINISH_TIME = 23;
	private static final int L2_FINISH_TIME = 53;
	private static final int V2_FINISH_TIME = 55;
	private static final int C1_FINISH_TIME = 95;
	private static final int V3_FINISH_TIME = 106;
	private static final int C2_FINISH_TIME = 134;

	@Override
	public void run() {
		goStraightUnit();
		goCircleUnit();
	}

	public void goStraightUnit() {
		goStraight(L1_FINISH_TIME);
		turn(V1_FINISH_TIME);
		goStraight(L2_FINISH_TIME);
		turn(V2_FINISH_TIME);
	}

	public void goCircleUnit() {
		while ( TimeThread.getTime <= V2_FINISH_TIME );
		goCircle(RIGHT_SPEED_FOR_BIG_CIRCLE, LEFT_SPEED_FOR_BIG_CIRCLE, C1_FINISH_TIME);
		turn(V3_FINISH_TIME);
		goCircle(RIGHT_SPEED_FOR_SMALL_CIRCLE, LEFT_SPEED_FOR_SMALL_CIRCLE, C2_FINISH_TIME);
	}

	public void goStraight(int time) {
		setMoterSpeed(SPEED_FOR_STRAIGHT, SPEED_FOR_STRAIGHT);
		while (TimeThread.getTime() <= time) {
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	public void turn(int time) {
		setMoterSpeed(SPEED_FOR_TURN, SPEED_FOR_TURN);
		while (TimeThread.getTime() <= time) {
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
