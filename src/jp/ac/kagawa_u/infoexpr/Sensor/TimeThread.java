package jp.ac.kagawa_u.infoexpr.Sensor;

import lejos.utility.Delay;

public class TimeThread implements Runnable{
    
    private static int time = 0;
    
    @Override
    public void run(){
        while(time <= 10000){
            Delay.msDelay(100);
            time += 100;
        }
    }
    
    public static int getTime(){
        return time;
    }
}
