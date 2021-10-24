package com.yolo.firebase;

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
	
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    
    private static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource("firebase/yolo-10f40-firebase-adminsdk-phvq4-f383bb0ecb.json");
        
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(resource.getInputStream())
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshToken();
        
        return googleCredential.getAccessToken();
    }
	
	public String sendCommentToToken(String registrationToken) throws FirebaseMessagingException {
		Message message = Message.builder()
				.putData("title", "게시글 댓글 알림")
				.putData("content", "회원님의 게시글에 댓글이 작성되었습니다.")
				.setToken(registrationToken).build();

		String response = FirebaseMessaging.getInstance().send(message);
		System.out.println("Successfully sent message: " + response);

		return response;
	}
	
	// 댓글 알림
	public String sendCommentPush(String registrationToken) {
		 // 1. create message body
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("title", "게시글 댓글 알림");
        jsonValue.put("content", "회원님의 게시글에 댓글이 작성되었습니다.");
        jsonValue.put("action", "toCommunity");

        JSONObject jsonData = new JSONObject();
        jsonData.put("token", registrationToken);
        jsonData.put("data", jsonValue);

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", jsonData);
        
        Response response = null;

        // 2. create token & send push
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            
            System.out.println("### message : " + jsonMessage.toString());
            
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + getAccessToken())
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
        
		return response.toString();
	}
	
	// 좋아요 알림
	public String sendLikedPush(String registrationToken, String nickname) {
		 // 1. create message body
       JSONObject jsonValue = new JSONObject();
       jsonValue.put("title", "좋아요 알림");
       jsonValue.put("content", nickname + "님이 회원님의 게시글을 좋아합니다.");
       jsonValue.put("action", "toCommunity");

       JSONObject jsonData = new JSONObject();
       jsonData.put("token", registrationToken);
       jsonData.put("data", jsonValue);

       JSONObject jsonMessage = new JSONObject();
       jsonMessage.put("message", jsonData);
       
       Response response = null;

       // 2. create token & send push
       try {
           OkHttpClient okHttpClient = new OkHttpClient();
           
           System.out.println("### message : " + jsonMessage.toString());
           
           Request request = new Request.Builder()
                   .addHeader("Authorization", "Bearer " + getAccessToken())
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
       
		return response.toString();
	}
	
}
