package S3LimitTest;

import java.util.LinkedList;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

public class Main {
	
	private LinkedList<Thread> threadPool = new LinkedList<Thread>();
	
	public Main(String bucket, int tCount) {
		for (int i=0; i< tCount; i++) {
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
		if (argv.length < 2 ) {
			System.out.println("Wrong arguments!");
			return;
		}
		String bName=argv[0];
		int tcount = Integer.parseInt(argv[1]);
		
		java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
		Main m = new Main(bName, tcount);
	}
}
