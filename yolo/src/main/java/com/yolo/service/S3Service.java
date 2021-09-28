package com.yolo.service;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

//	private final AmazonS3Client amazonS3Client;

	private AmazonS3 s3Client;

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@PostConstruct
	public void setS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

		s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(this.region).build();
	}

	public String upload(MultipartFile file, String dirName) throws IOException {
		String imageUrl = upload(file, dirName, file.getOriginalFilename());

		return imageUrl;
	}

	public String upload(MultipartFile file, String dirName, String fileName) throws IOException {
		fileName = fileName.replaceAll("[^\\uAC00-\\uD7A30-9a-zA-Z.]", "");
		System.out.println("특수문자, 공백 제거된 파일 이름: " + fileName);
		
		int dateTime = (int) (new Date().getTime() / 1000);
		String filePath = dirName + "/" + dateTime + fileName; // 현재 날짜, 시간을 기준으로 구별값 첨부 -> 중복 방지

		s3Client.putObject(new PutObjectRequest(bucket, filePath, file.getInputStream(), null)
				.withCannedAcl(CannedAccessControlList.PublicRead));

		return s3Client.getUrl(bucket, filePath).toString();
	}

	public boolean delete(String imageUrl) {
		String filePath = imageUrl.substring(52);
		System.out.println("파일 이름: " + filePath);

		boolean isExistObject = s3Client.doesObjectExist(bucket, filePath);

		if (isExistObject) {
			s3Client.deleteObject(bucket, filePath);

			return true;
		}

		return false;
	}

}
