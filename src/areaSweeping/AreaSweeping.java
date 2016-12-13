package areaSweeping;

public class AreaSweeping {

	public static void main(String[] args) {
		Thread run = new Thread(new RunThread());
		Thread sensor = new Thread(new SensorThread());

		run.start();
		sensor.start();
		try {
			run.join();
			sensor.join();
		} catch (InterruptedException e) {}
	}

}
