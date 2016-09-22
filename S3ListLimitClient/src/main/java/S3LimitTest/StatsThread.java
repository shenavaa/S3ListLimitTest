package S3LimitTest;

public class StatsThread extends Thread {
	
	long stime = System.currentTimeMillis();
	
	public StatsThread() {
		
	}
	
	public void run() {
		while ((this.isInterrupted() == false) && (this.isAlive())) {
			try {
				this.sleep(1000L);
			} catch (InterruptedException e) {
				return;
			}
			int count = ListerThread.counter.getAndSet(0);
			System.out.println((int)((System.currentTimeMillis()-stime)/1000) + " lists per second: " + count + " Exceptions: " + ListerThread.exceptionCounter.getAndSet(0));
		}
	}
}
