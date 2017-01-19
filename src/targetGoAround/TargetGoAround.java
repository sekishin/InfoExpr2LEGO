package targetGoAround;

public class TargetGoAround {

	public static void main(String[] args) {
		Thread run = new Thread(new RunThread());
		Thread sensor = new Thread(new SensorThread());
		Thread sound = new Thread(new SoundThread());

		sensor.start();
		run.start();
		sound.start();
		try {
			run.join();
			sensor.join();
			sound.join();
		} catch (InterruptedException e) {}
	}

}
