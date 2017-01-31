package outCourse;

import jp.ac.kagawa_u.infoexpr.Sensor.ColorSensor;
import lejos.hardware.port.SensorPort;

public class OutCourse {
	
	static ColorSensor right = new ColorSensor(SensorPort.S2);
	static ColorSensor left = new ColorSensor(SensorPort.S3);

	public static void main(String[] args) {
		
		DualCalibration.executeCalibration(7, left, right);
		
		Thread run = new Thread(new RunThread());
		Thread sensor = new Thread(new SensorThread(left, right));
		Thread sound = new Thread(new SoundThread());
		
		run.start();
		sensor.start();
		sound.start();
		try {
			run.join();
			sensor.join();
			sound.join();
		} catch ( InterruptedException e ) {}
		
	}

}
