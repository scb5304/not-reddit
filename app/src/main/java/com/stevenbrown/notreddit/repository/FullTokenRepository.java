package com.stevenbrown.notreddit.repository;

import android.content.SharedPreferences;

import com.stevenbrown.notreddit.Constants;
import com.stevenbrown.notreddit.models.auth.RedditAccount;
import com.stevenbrown.notreddit.models.auth.Token;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FullTokenRepository {

    private SharedPreferences mSharedPreferences;
    private TokenRepository mTokenRepository;
    private AccountRepository mAccountRepository;

    @Inject
    public FullTokenRepository(SharedPreferences sharedPreferences,
                               TokenRepository tokenRepository,
                               AccountRepository accountRepository) {
        mSharedPreferences = sharedPreferences;
        mTokenRepository = tokenRepository;
        mAccountRepository = accountRepository;
    }

    /**
     * Orchestrates a call to retrieve a token, gets the account associated that token, then returns
     * the token with that account inside of it.
     */
    public Single<Token> getFullToken(String authToken) {
        Single<Token> tokenSingle = mTokenRepository.getUserToken(authToken);
        return tokenSingle.flatMap(token -> getCurrentRedditAccount(token).flatMap(redditAccount -> {
            token.setAccount(redditAccount);
            return Single.just(token);
        })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private Single<RedditAccount> getCurrentRedditAccount(Token token) {
        mSharedPreferences.edit().putString(Constants.SharedPreferenceKeys.TEMP_USER_TOKEN, token.getAccessToken()).apply();
        return mAccountRepository.getCurrentRedditAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
