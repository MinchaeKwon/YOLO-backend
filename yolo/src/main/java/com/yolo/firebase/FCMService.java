package com.yolo.firebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FCMService {

//	private static String getAccessToken() throws IOException {
//        ClassPathResource resource = new ClassPathResource("firebase/firebase_service_key");
//        GoogleCredential googleCredential = GoogleCredential
//                .fromStream(new FileInputStream(resource.getFile()))
//                .createScoped(Arrays.asList(SCOPES));
//        googleCredential.refreshToken();
//        return googleCredential.getAccessToken();
//    }
	
	public String sendCommentToToken(String registrationToken) throws FirebaseMessagingException {
		Message message = Message.builder()
				.putData("title", "게시글 댓글 알림")
				.putData("content", "회원님의 게시글에 댓글이 작성되었습니다")
				.setToken(registrationToken).build();

		String response = FirebaseMessaging.getInstance().send(message);
		System.out.println("Successfully sent message: " + response);

		return response;
	}
}
