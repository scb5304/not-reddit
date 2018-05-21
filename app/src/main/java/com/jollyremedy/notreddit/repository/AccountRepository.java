package com.jollyremedy.notreddit.repository;

import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.models.auth.RedditAccount;

import javax.inject.Inject;

import io.reactivex.Single;

public class AccountRepository {

    private OAuthRedditApi mRedditApi;

    @Inject
    public AccountRepository(OAuthRedditApi oAuthRedditApi) {
        mRedditApi = oAuthRedditApi;
    }

    public Single<Object> getCurrentRedditAccount() {
        return mRedditApi.getMe();
    }
}
