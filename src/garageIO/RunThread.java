package garageIO;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class RunThread implements Runnable {

	static RegulatedMotor rightMotor = Motor.B;;
	static RegulatedMotor leftMotor  = Motor.C;
	static int highSpeed = 300;
	static int lowSpeed = 100;
	static SenserColor rightState;
	static SenserColor leftState;
	static boolean wait = true;

	static int garageSpeed = 300;
	static int time = 0;

	static boolean parkNow = false;
	static boolean parkStart = false;
	static boolean parkFinished = false;

	static final String[] color = {"white", "black", "none",  "gray1", "gray2", "blue"};

	@Override
	public void run() {
		while ( wait );
		lineTrace();
		garageIO();
		lineTrace();
	}

	private static void garageIO() {
		adjustmentRun();
		leftTurn();
		forwardToBlue();
		backToRoot();
		rightTurn();
		parkFinished = true;
		adjustmentRun();
		recoveryTrace();
	}

	private static void adjustmentRun() {
		boolean isSame = false;
		while ( true && Button.ESCAPE.isUp()) {
			LCD.clear();
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
			LCD.drawString("R : " + color[toInt(rightState)], 0, 2);
			LCD.drawString("L : " + color[toInt(leftState)], 0, 3);
			LCD.drawString("GarageIO", 0, 0);
			LCD.refresh();
			if ( rightState == SenserColor.GRAY2 && leftState == SenserColor.GRAY2 && ! parkNow ) {
				parkNow = true;
			}
			if ( parkNow && ! parkStart && rightState == SenserColor.GRAY1 && leftState == SenserColor.GRAY1) {
				parkStart = true;
				break;
			}
			if ( parkFinished && (rightState == SenserColor.WHITE || leftState == SenserColor.WHITE) ) {
				break;
			}
			if ( isSame && rightState != leftState ) {
				if ( ! parkFinished ) {
					if ( toInt(rightState) > toInt(leftState) ) motorSetSpeed(lowSpeed, 0);
					else motorSetSpeed(0, lowSpeed);
				} else {
					if ( toInt(rightState) > toInt(leftState) ) motorSetSpeed(0, lowSpeed);
					else motorSetSpeed(lowSpeed, 0);
				}
			} else motorSetSpeed(lowSpeed, lowSpeed);
			motorForward();
		}
	}
	private static void recoveryTrace() {
		boolean flag = true;
		Distination last = decideDistination(rightState, leftState);
		while ( flag && Button.ESCAPE.isUp() ) {
			LCD.clear();
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
			LCD.drawString("R : " + color[toInt(rightState)], 0, 2);
			LCD.drawString("L : " + color[toInt(leftState)], 0, 3);
			LCD.drawString("RecoveryTrace", 0, 0);
			if ( decideDistination(rightState, leftState) != last ) break;
			switch ( decideDistination(rightState, leftState)) {
			case STRAIGHT:
				flag = false;
			case LEFT:
				LCD.drawString("RIGHT", 0, 1);
				motorSetSpeed(highSpeed, lowSpeed);
				motorForward();
				break;
			case RIGHT:
				LCD.drawString("LEFT", 0, 1);
				motorSetSpeed(lowSpeed, highSpeed);
				motorForward();
				break;
			default:
				LCD.drawString("ELSE", 0, 1);
				break;
			}
			LCD.refresh();

		}

	}
	
	private static void lineTrace() {
		boolean flag = true;
		while ( flag && Button.ESCAPE.isUp() ) {
			LCD.clear();
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
			LCD.drawString("R : " + color[toInt(rightState)], 0, 2);
			LCD.drawString("L : " + color[toInt(leftState)], 0, 3);
			LCD.drawString("LineTrace", 0, 0);
			switch ( decideDistination(rightState, leftState)) {
			case STRAIGHT:
				LCD.drawString("STRAIGHT", 0, 1);
				motorSetSpeed(highSpeed, highSpeed);
				motorForward();
				break;
			case LEFT:
				LCD.drawString("LEFT", 0, 1);
				motorSetSpeed(lowSpeed, highSpeed);
				motorForward();
				break;
			case RIGHT:
				LCD.drawString("RIGHT", 0, 1);
				motorSetSpeed(highSpeed, lowSpeed);
				motorForward();
				break;
			case END:
				motorStop();
				flag = false;
				break;
			default:
				LCD.drawString("ELSE", 0, 1);
				break;
			}
			LCD.refresh();

		}
	}

	private static void leftTurn() {
		int time = 550;
		motorSetSpeed(garageSpeed , garageSpeed );
		leftMotor.backward();
		rightMotor.forward();
		Delay.msDelay(time);
		motorStop();
	}

	private static void forwardToBlue() {
		int rightMotorSpeed = garageSpeed;
		int leftMotorSpeed = garageSpeed;
		rightMotor.resetTachoCount();

		while( true && Button.ESCAPE.isUp()) {
			LCD.clear();
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
			LCD.drawString("R : " + color[toInt(rightState)], 0, 2);
			LCD.drawString("L : " + color[toInt(leftState)], 0, 3);
			LCD.drawString("GarageIO", 0, 0);
			LCD.refresh();

			motorSetSpeed(leftMotorSpeed, rightMotorSpeed);
			if ( rightState == SenserColor.BLUE ) {
				rightMotorSpeed = 0;
				if ( leftMotorSpeed != 0 ) leftMotorSpeed = lowSpeed; 
			}

			if ( leftState == SenserColor.BLUE ) {
				leftMotorSpeed = 0;
				if ( rightMotorSpeed != 0 ) rightMotorSpeed = lowSpeed;
			}
			if ( rightMotorSpeed == 0 && leftMotorSpeed == 0 ) {
				break;
			}
			motorForward();
		}
		motorStop();
	}

	private static void backToRoot() {
		motorSetSpeed(garageSpeed, garageSpeed);
		while( 0 < rightMotor.getTachoCount()) {
			motorBackward();
		}
		motorStop();
	}
	
	private static void rightTurn() {
		int time = 550;
		motorSetSpeed(garageSpeed , garageSpeed );
		rightMotor.backward();
		leftMotor.forward();
		Delay.msDelay(time);
		motorStop();
	}
	
	private static int toInt(SenserColor c) {
		if ( c == SenserColor.WHITE ) return 0;
		if ( c == SenserColor.BLACK ) return 1;
		if ( c == SenserColor.GRAY1 ) return 3;
		if ( c == SenserColor.GRAY2 ) return 4;
		if ( c == SenserColor.BLUE ) return 5;
		return 2;
	}

	private static Distination decideDistination(SenserColor right, SenserColor left) {
		if ( right == SenserColor.BLACK ) {
			if ( left == SenserColor.BLACK ) {
				return Distination.END;
			} else if ( left == SenserColor.WHITE ) {
				return Distination.RIGHT;
			}
		} else if ( right == SenserColor.WHITE ) {
			if ( left == SenserColor.BLACK ) {
				return Distination.LEFT;
			} else if ( left == SenserColor.WHITE ) {
				return Distination.STRAIGHT;
			}
		}
		return Distination.ELSE;

	}

	private static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed) {
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}

	private static void motorForward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	private static void motorBackward() {
		leftMotor.backward();
		rightMotor.backward();
	}

	private static void motorStop() {
		leftMotor.stop(true);
		rightMotor.stop();
	}


}
