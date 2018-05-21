package com.jollyremedy.notreddit.models.auth;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("scope")
    private String scope;

    @SerializedName("expires_in")
    private Integer expiresIn;

    private Object account;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
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

    public Object getAccount() {
        return account;
    }

    public void setAccount(Object account) {
        this.account = account;
    }
}
