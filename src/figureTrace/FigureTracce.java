package figureTrace;

public class FigureTracce {


	public static void main(String[] args) {
		Thread time = new Thread(new TimeThread());
	    Thread run   = new Thread(new RunThread());
	    Thread sound = new Thread(new SoundThread());

        time.start();
		run.start();
        sound.start();
        try {
            run.join();
            time.join();
            sound.join();
        } catch (InterruptedException e) {
        }

	}
}
