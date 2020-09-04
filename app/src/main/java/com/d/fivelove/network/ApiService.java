package com.d.fivelove.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by Nguyen Kim Khanh on 9/3/2020.
 */
public interface ApiService {
    @POST("send")
    Call<String> sendRemoteMessage(
            @HeaderMap HashMap<String, String> header,
            @Body String remoteBody
    );
}
