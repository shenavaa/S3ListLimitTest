package S3LimitTest;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3Client;

public class ListerThread extends Thread {
	
	public static AtomicInteger counter = new AtomicInteger(0);
	
	private Random random;
	private String bucketName;
	private long interval;
	private static AmazonS3Client s3Client;

	static {
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setRequestTimeout(20 * 1000);
		clientConfig.setConnectionMaxIdleMillis(1000L);
		clientConfig.setMaxConnections(1000);
		clientConfig.setMaxErrorRetry(0);
		clientConfig.setRetryPolicy(new RetryPolicy(null, null, 0, true));
		clientConfig.setConnectionTTL(1000000L);
	    clientConfig.setUseThrottleRetries(false);

		s3Client =  = new AmazonS3Client(new ProfileCredentialsProvider(), clientConfig);
	}
	
	public ListerThread(String bucketName,Integer interval) {
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.interval = interval;
	}
	
	public void run() {
				while ( (this.isInterrupted() == false) && (this.isAlive()) ) {
					try {
						this.counter.incrementAndGet();
						s3Client.listObjectsV2(bucketName);
						this.sleep(this.interval);
						// No Exception. Going faster
						this.interval = (long)(this.interval * 0.5) + 1 + random.nextInt(20);
						//System.out.println("Going faster:" + this.interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					} catch (Exception e) {
						System.out.println("Got exception. slowing down:" + e.getMessage());
						this.interval = (long)(this.interval * 1.6) + 1;
					}
				}
	}
}
