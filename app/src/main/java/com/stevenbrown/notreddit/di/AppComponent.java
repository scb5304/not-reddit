package com.stevenbrown.notreddit.di;

import android.app.Application;

import com.stevenbrown.notreddit.NotRedditApplication;
import com.stevenbrown.notreddit.auth.accounting.Accountant;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        AppModule.class,
        MainActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(NotRedditApplication app);
    void inject(Accountant accountant);
}
