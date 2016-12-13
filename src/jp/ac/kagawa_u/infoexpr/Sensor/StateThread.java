package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class StateThread implements Runnable{

    private static RegulatedMotor rightMotor = Motor.B;
    private static RegulatedMotor leftMotor = Motor.C;
    
    @Override
    public void run(){
        while(TimeThread.getTime() <= 10000){
            switch (RunThread.getState()){
            case FORWARD:
                rightMotor.forward();
                leftMotor.forward();
                break;
                
            case LEFT:
                rightMotor.stop();
                leftMotor.forward();
                break;
                
            case RIGHT:
                rightMotor.forward();
                leftMotor.stop();
                break;
                
            case STOP:
                rightMotor.stop();
                leftMotor.stop();
                break;
            }
        }
    }
}
