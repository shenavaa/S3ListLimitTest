package S3LimitTest;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListerThread extends Thread {
	
	public static AtomicInteger counter = new AtomicInteger(0);
	public static AtomicInteger exceptionCounter = new AtomicInteger(0);
	
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

		s3Client =  new AmazonS3Client(new ProfileCredentialsProvider(), clientConfig);
	}
	
	public ListerThread(String bucketName,Integer interval) {
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.interval = interval;
	}
	
	public void run() {
				while ( (this.isInterrupted() == false) && (this.isAlive()) ) {
					try {
						final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(1);
						ListObjectsV2Result result;
						result = s3Client.listObjectsV2(req);
						/*
			            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
			                System.out.println(" - " + objectSummary.getKey() + "  " +
			                          "(size = " + objectSummary.getSize() + 
			                          ")");
			            }
			            */
						this.counter.incrementAndGet();
						this.sleep(this.interval);
						// No Exception. Going faster
						this.interval = (long)(this.interval * 0.5) + 1 + random.nextInt(20);
						//System.out.println("Going faster:" + this.interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					} catch (Exception e) {
						e.printStackTrace();
						
						this.interval = (long)(this.interval * 1.6) + 1;
						if (this.interval > 500) {
							this.interval = 200;
						}
						exceptionCounter.incrementAndGet();
					}
				}
	}
}
