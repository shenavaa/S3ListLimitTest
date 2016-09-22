package S3LimitTest;

public class StatsThread extends Thread {
	public void run() {
		while ((this.isInterrupted() == false) && (this.isAlive())) {
			try {
				this.sleep(1000L);
			} catch (InterruptedException e) {
				return;
			}
			int count = ListerThread.counter.getAndSet(0);
			System.out.println(System.currentTimeMillis() + " lists per second: " + count + " Exceptions: " + ListerThread.exceptionCounter.get());
		}
	}
}
