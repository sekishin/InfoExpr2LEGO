package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

public class RunThread implements Runnable{

    private static RegulatedMotor rightMotor = Motor.B;
    private static RegulatedMotor leftMotor = Motor.C;
    private static StatType state = StatType.FORWARD;
    @Override
    public void run(){
        rightMotor.setSpeed(740);
        leftMotor.setSpeed(740);
        
        
        
        while(TimeThread.getTime() <= 10000){
            if(Button.ENTER.isDown()){
                state = StatType.FORWARD;
            }
            else if(Button.RIGHT.isDown()){
                state = StatType.RIGHT;
            }
            else if(Button.LEFT.isDown()){
                state = StatType.LEFT;
            }
            else if(Button.ESCAPE.isDown()){
                state = StatType.STOP;
            }
        }
        

    }
    public static StatType getState(){
        return state;
    }
}
