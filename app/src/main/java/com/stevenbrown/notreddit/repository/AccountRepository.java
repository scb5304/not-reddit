package com.stevenbrown.notreddit.repository;

import com.stevenbrown.notreddit.api.OAuthRedditApi;
import com.stevenbrown.notreddit.models.auth.RedditAccount;

import javax.inject.Inject;

import io.reactivex.Single;

public class AccountRepository {

    private OAuthRedditApi mRedditApi;

    @Inject
    public AccountRepository(OAuthRedditApi oAuthRedditApi) {
        mRedditApi = oAuthRedditApi;
    }

    public Single<RedditAccount> getCurrentRedditAccount() {
        return mRedditApi.getMe();
    }
}
