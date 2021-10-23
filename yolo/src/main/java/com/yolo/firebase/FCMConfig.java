package com.yolo.firebase;

import java.io.FileInputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FCMConfig {

    @Value("classpath:firebase/yolo-10f40-firebase-adminsdk-phvq4-f383bb0ecb.json")
    private Resource resource;

    @PostConstruct
    public void initFirebase() {
        try {
            // Service Account를 이용하여 Fireabse Admin SDK 초기화
        	
        	FirebaseApp firebaseApp = null;
        	List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        	 
        	if (firebaseApps != null && !firebaseApps.isEmpty()){
        	    for (FirebaseApp app : firebaseApps){
        	        if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
        	            firebaseApp = app;
        	        }
        	    }
        	             
        	} else {
        		FileInputStream serviceAccount = (FileInputStream) resource.getInputStream();
        		
        	    FirebaseOptions options = new FirebaseOptions.Builder()
        	        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//        	        .setDatabaseUrl("https://{사용자마다 다름}.firebaseio.com")
        	        .build();
        	    
        	    firebaseApp = FirebaseApp.initializeApp(options);              
        	}
        	
//            FileInputStream serviceAccount = new FileInputStream(resource.getFile());
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
////                    .setDatabaseUrl("https://{사용자마다 다름}.firebaseio.com")
//                    .build();
//            
//            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
