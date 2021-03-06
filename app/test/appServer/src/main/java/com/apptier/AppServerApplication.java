package com.apptier;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
@ServletComponentScan
public class AppServerApplication {

	
	static AmazonS3 s3;
	static String bucketName="video-detection-results";
	
	
	@RequestMapping("/RunScript")
	String runScript() {
		
		String ans = "error oocured at app instance";
		try{
			 
			System.out.println("RunScript called"); 
			ProcessBuilder pb = new ProcessBuilder("python","/home/ubuntu/darknet/generate_result.py");
			Process p = pb.start();
			System.out.println("Process started"); 
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			ans = in.readLine();
			
			
			String msgs [] = ans.split("@",2);
			
			s3.putObject(bucketName,msgs[0], msgs[1]);
			
			System.out.println(in.readLine());
			}catch(Exception e){
				ans = "Exception occured at app tier";
				System.out.println("Exception occured at app tier");
				
				System.out.println(e);}
			
		
		
		
		return ans;
	}
	
	
	
	@RequestMapping("/testS3")
	String testS3() {
		
		String ans = "error oocured at app instance";
		try{
			 
			ans = "DummyVideo@DummyLabel";
			String msgs [] = ans.split("@");
			System.out.println(msgs[0]);
			System.out.println(msgs[1]);
			s3.putObject(bucketName,msgs[0], msgs[1]);
			
			
			}catch(Exception e){
				ans = "Exception occured at app tier";
				System.out.println("Exception occured at app tier");
				
				System.out.println(e);}
			
		
		
		
		return ans;
	}
	
	
	@RequestMapping("/test")
	String runtest() {
		return "test successful final";
	}
	
	public static void main(String[] args) {
		System.out.println("#################VINIT######################");
		
		AWSCredentials credentials = new BasicAWSCredentials(
				  "AKIAJN7UZDZO7RFC7FFA", 
				  "bHJ2NMJZjDA3+IyTUsbOE68xDkxJnVcypk9GVPOV"
				);
		
		s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_WEST_1)
				  .build();
		
		
		SpringApplication.run(AppServerApplication.class, args);
	}

}
