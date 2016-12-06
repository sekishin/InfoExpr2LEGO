package lineTrace;


public class DoubleLineTrace {




	public static void main(String[] args) {
		Thread run = new Thread(new RunThread());
		Thread sensor = new Thread(new SensorThread());
		Thread sound = new Thread(new SoundThread());

		run.start();
		sensor.start();
		sound.start();
		try {
			run.join();
			sensor.join();
			sound.join();
		} catch (InterruptedException e) {}

	}


}


