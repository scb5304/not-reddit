package com.jollyremedy.notreddit.di;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jollyremedy.notreddit.api.OAuthRedditApi;
import com.jollyremedy.notreddit.api.OAuthTokenInterceptor;
import com.jollyremedy.notreddit.api.RequestTokenApi;
import com.jollyremedy.notreddit.api.RequestTokenInterceptor;
import com.jollyremedy.notreddit.api.adapter.LocalDateTimeAdapter;
import com.jollyremedy.notreddit.di.viewmodel.ViewModelModule;
import com.jollyremedy.notreddit.repository.TokenRepository;

import org.threeten.bp.LocalDateTime;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton
    @Provides
    AccountManager provideAccountManager(Application application) {
        return AccountManager.get(application);
    }

    @Singleton
    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    Resources provideResources(Application application) {
        return application.getResources();
    }

    @Singleton
    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    /*
     * Dependencies for requesting a token to https://www.reddit.com
     */
    @Singleton
    @Provides
    @Named("requestToken")
    OkHttpClient provideRequestTokenOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .addInterceptor(new RequestTokenInterceptor())
                .build();
    }

    @Singleton
    @Provides
    @Named("requestToken")
    Retrofit provideRequestTokenRetrofit(@Named("requestToken") OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://www.reddit.com")
                .build();
    }

    @Singleton
    @Provides
    RequestTokenApi provideRequestTokenApi(@Named("requestToken") Retrofit retrofit) {
        return retrofit.create(RequestTokenApi.class);
    }


    /*
     * Dependencies for requesting resources from https://oauth.reddit.com
     */
    @Singleton
    @Provides
    @Named("oauth")
    OkHttpClient provideOAuthOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor,
                                          SharedPreferences sharedPreferences,
                                          TokenRepository tokenRepository) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OAuthTokenInterceptor(sharedPreferences, tokenRepository))
                .build();
    }

    @Singleton
    @Provides
    @Named("oauth")
    Retrofit provideOAuthRetrofit(@Named("oauth") OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://oauth.reddit.com")
                .build();
    }

    @Singleton
    @Provides
    OAuthRedditApi provideOAuthApi(@Named("oauth") Retrofit retrofit) {
        return retrofit.create(OAuthRedditApi.class);
    }
}
