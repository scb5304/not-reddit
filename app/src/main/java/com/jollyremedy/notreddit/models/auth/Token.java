package com.jollyremedy.notreddit.models.auth;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("scope")
    private String scope;

    @SerializedName("expires_in")
    private Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getScope() {
        return scope;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }
}
