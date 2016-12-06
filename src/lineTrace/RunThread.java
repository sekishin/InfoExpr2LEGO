package lineTrace;

import jp.ac.kagawa_u.infoexpr.Sensor.TouchSensor;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable {

	static TouchSensor touch = new TouchSensor(SensorPort.S1);
	static RegulatedMotor rightMotor  = Motor.B;
	static RegulatedMotor leftMotor  = Motor.C;
	static int speedUp = 20;
	static int speedDown = 20;
	static final int DEFAULT_SPEED = 800;
	static int leftLowSpeed = 300;
	static int rightLowSpeed = 400;
	static int highSpeed = 600;
	static int veryHighSpeed = 1000;
	static int rightHighSpeed = 850;
	static int leftHighSpeed = 750;
	static int turnTachoCount = 730;

	@Override
	public void run() {

		// Go Straight
		leftMotor.resetTachoCount();
		while ( ! SensorThread.isRed() ) {
			//motorSetSpeed(veryHighSpeed, veryHighSpeed);
			//motorForward();
			lineTrace();
			if ( touch.isPressed() ) break;
		}
		Button.LEDPattern(2); // 赤色に点灯


		// turn
		leftMotor.resetTachoCount();
		while ( leftMotor.getTachoCount() <= turnTachoCount ) {
			motorSetSpeed(highSpeed, highSpeed);
			motorTurn();
			LCD.clear();
			LCD.drawString(Integer.toString(leftMotor.getTachoCount()), 0, 0);
			LCD.refresh();
		}
/*
		while ( ! SensorThread.isRed() ) {
			motorSetSpeed(highSpeed, highSpeed);
			motorTurn();
			if ( touch.isPressed() ) break;
		}
*/
		leftMotor.stop(true);
		rightMotor.stop();

		// Start Line Trace
		while( ! touch.isPressed() ){
			lineTrace();
		}
		leftMotor.stop(true);
		rightMotor.stop();
	}

	private static void lineTrace() {
		switch (SensorThread.getSensor()) {
		case STRAIGHT:
			motorSetSpeed(highSpeed, highSpeed);
			motorForward();
			break;
		case LEFT:
			motorSetSpeed(leftLowSpeed, rightHighSpeed);
			motorForward();
			break;
		case RIGHT:
			motorSetSpeed(leftHighSpeed, rightLowSpeed);
			motorForward();
			break;
		default:
			break;
		}
	}

	private static void motorSetSpeed(int leftMotorSpeed, int rightMotorSpeed){
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
		//leftMotor.setSpeed(0);
		//rightMotor.setSpeed(0);
	}

	private static void motorForward(){
		leftMotor.forward();
		rightMotor.forward();
	}

	private static void motorTurn() {
		leftMotor.forward();
		rightMotor.backward();
	}

}
