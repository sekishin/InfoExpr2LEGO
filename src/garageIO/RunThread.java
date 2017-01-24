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
		/*while ( Button.ESCAPE.isUp() ) {
			motorSetSpeed(lowSpeed, lowSpeed);
			motorForward();
		}*/

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
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
//			LCD.clear();
//			LCD.drawString("GarageIO", 0, 0);
//			LCD.refresh();
			if ( rightState == SenserColor.GRAY4 && leftState == SenserColor.GRAY4 && ! parkNow ) {
				parkNow = true;
			}
			if ( parkNow && ! parkStart && rightState == SenserColor.GRAY3 && leftState == SenserColor.GRAY3) {
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
			motorSetSpeed(lowSpeed, lowSpeed);
			motorForward();
		}
	}
	private static void recoveryTrace() {
		boolean flag = true;
		Distination last = decideDistination(rightState, leftState);
		while ( flag && Button.ESCAPE.isUp() ) {
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
//			LCD.clear();
//			LCD.drawString("RecoveryTrace", 0, 0);
			if ( decideDistination(rightState, leftState) != last ) break;
			switch ( decideDistination(rightState, leftState)) {
			case STRAIGHT:
				flag = false;
			case LEFT:
				motorSetSpeed(highSpeed, lowSpeed);
				motorForward();
				break;
			case RIGHT:
				motorSetSpeed(lowSpeed, highSpeed);
				motorForward();
				break;
			default:
				break;
			}
//			LCD.refresh();
		}

	}
	
	private static void lineTrace() {
		boolean flag = true;
		while ( flag && Button.ESCAPE.isUp() ) {
			rightState = SensorThread.getRightState();
			leftState = SensorThread.getLeftState();
//			LCD.clear();
//			LCD.drawString("LineTrace", 0, 0);
			switch ( decideDistination(rightState, leftState)) {
			case STRAIGHT:
				motorSetSpeed(highSpeed, highSpeed);
				motorForward();
				break;
			case LEFT:
				motorSetSpeed(lowSpeed, highSpeed);
				motorForward();
				break;
			case RIGHT:
				motorSetSpeed(highSpeed, lowSpeed);
				motorForward();
				break;
			case END:
				motorStop();
				flag = false;
				break;
			default:
				break;
			}
//			LCD.refresh();
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
//			LCD.drawString("GarageIO", 0, 0);
//			LCD.refresh();

			motorSetSpeed(leftMotorSpeed, rightMotorSpeed);
			if ( rightState == SenserColor.BLUE ) {
				rightMotorSpeed = 0;
				break;
				//if ( leftMotorSpeed != 0 ) leftMotorSpeed = lowSpeed; 
				
			}

			if ( leftState == SenserColor.BLUE ) {
				leftMotorSpeed = 0;
				break;
				//if ( rightMotorSpeed != 0 ) rightMotorSpeed = lowSpeed;
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
		if ( c == SenserColor.GRAY3 ) return 2;
		if ( c == SenserColor.GRAY4 ) return 3;
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
