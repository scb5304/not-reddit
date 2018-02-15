package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.auth.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RequestTokenApi {
    @FormUrlEncoded
    @POST("api/v1/access_token")
    Call<Token> getToken(@Field("grant_type") String grantType,
                         @Field("device_id") String deviceId);
}
