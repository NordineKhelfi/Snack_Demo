package com.khelfi.snackdemo.Common;

import com.khelfi.snackdemo.Model.User;
import com.khelfi.snackdemo.RemoteWebServer.APIService;
import com.khelfi.snackdemo.RemoteWebServer.RetrofitClient;

/**
 * Created by norma on 23/12/2017.
 */

public class Common {

    public static User currentUser;

    //FCM
    public static String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
