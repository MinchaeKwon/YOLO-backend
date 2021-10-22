package com.yolo.firebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
	
//	private static String getAccessToken() throws IOException {
//        ClassPathResource resource = new ClassPathResource("firebase/jackpot-1611239774705-firebase-adminsdk-xlp80-fa2c872b91.json");
//        GoogleCredential googleCredential = GoogleCredential
//                .fromStream(new FileInputStream(resource.getFile()))
//                .createScoped(Arrays.asList(SCOPES));
//        googleCredential.refreshToken();
//        return googleCredential.getAccessToken();
//    }
	
	public String sendCommentToToken(String registrationToken) throws FirebaseMessagingException {
		Message message = Message.builder()
				.putData("title", "게시글 댓글 알림")
				.putData("content", "회원님의 게시글에 댓글이 작성되었습니다.")
				.setToken(registrationToken).build();

		String response = FirebaseMessaging.getInstance().send(message);
		System.out.println("Successfully sent message: " + response);

		return response;
	}
	
	public String sendCommentToToken2(String registrationToken) {
		 // 1. create message body
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("title", "게시글 댓글 알림");
        jsonValue.put("content", "회원님의 게시글에 댓글이 작성되었습니다.");

        JSONObject jsonData = new JSONObject();
        jsonData.put("token", registrationToken);
        jsonData.put("data", jsonValue);

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", jsonData);
        
        Response response = null;

        // 2. create token & send push
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + "AAAAsiEk5eQ:APA91bESl8-TwBWCSm7Zz6AkVaZwwIfSrRkfBoDeWdt9FkbFhWESKcqP9f-3cuvLVRQ5aUmd5zLW6Wvf6lmM5pSWgVXLXanPVifOJ-I9LiVsAW2nHEveZLpDxsDP8uzY4wP3_w-E1Uy-")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .url("https://fcm.googleapis.com/v1/projects/yolo-10f40/messages:send")
                    .post(RequestBody.create(jsonMessage.toString(), MediaType.parse("application/json")))
                    .build();
            response = okHttpClient.newCall(request).execute();

            System.out.println("### response str : " + response.toString());
            System.out.println("### response result : " + response.isSuccessful());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Successfully sent message: " + response.toString());
        
		return response.toString();
	}
	
}
