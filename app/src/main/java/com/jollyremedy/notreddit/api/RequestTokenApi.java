package com.jollyremedy.notreddit.api;

import com.jollyremedy.notreddit.models.auth.Token;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RequestTokenApi {

    @FormUrlEncoded
    @POST("api/v1/access_token")
    Call<Token> getTokenAppOnlyFlow(@Field("grant_type") String grantType,
                                    @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("api/v1/access_token")
    Call<Token> getRefreshToken(@Field("grant_type") String grantType,
                                @Field("device_id") String deviceId,
                                @Field("refresh_token") String refreshToken);

    @FormUrlEncoded
    @POST("api/v1/access_token")
    Single<Token> getTokenCodeFlow(@Field("grant_type") String grantType,
                                   @Field("code") String authCode,
                                   @Field("redirect_uri") String redirectUri);
}
