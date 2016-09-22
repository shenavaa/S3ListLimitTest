package S3LimitTest;

import java.util.LinkedList;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

public class Main {
	
	private LinkedList<Thread> threadPool = new LinkedList<Thread>();
	private int THREAD_COUNT = 10;
	public Main(String bucket, int tCount) {
		this.THREAD_COUNT=tCount;
		for (int i=0; i< THREAD_COUNT; i++) {
			Thread t  = new ListerThread(bucket,1000);
			t.start();
			threadPool.add(t);
		}
		
		StatsThread sThread = new StatsThread();
		sThread.start();
		try {
			for (Thread t: threadPool) {
				t.join();
			}
			sThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			for (Thread t: threadPool) {
				t.interrupt();
				return;
			}
		}
	}
	
	public static void main(String argv[]) {
		java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
		Main m = new Main("shenavaa-test", 10);
	}
}
