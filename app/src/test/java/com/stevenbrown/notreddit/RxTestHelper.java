package com.stevenbrown.notreddit;

import org.mockito.stubbing.Answer;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class RxTestHelper {

    /**
     * With this default Repository Answer, RxJava2 observables and the like will immediately emit
     * an error by default. They would otherwise throw NullPointerExceptions when the unit under test
     * attempts to subscribe to them. This allows us to verify the methods are invoked without having to
     * actually mock out every single method.
     */
    public static Answer defaultRepositoryAnswer() {
        return invocation -> {
            Class returnType = invocation.getMethod().getReturnType();
            if (returnType == Flowable.class) {
                return Flowable.error(new Throwable("Flowable not stubbed."));
            } else if (returnType == Single.class) {
                return Single.error(new Throwable("Single not stubbed."));
            } else if (returnType == Maybe.class) {
                return Maybe.error(new Throwable("Maybe not stubbed."));
            } else if (returnType == Observable.class) {
                return Observable.error(new Throwable("Observable not stubbed."));
            } else if (returnType == Completable.class) {
                return Completable.error(new Throwable("Completable not stubbed."));
            }
            return Observable.error(new Throwable("Repository answer of type " + returnType + " was not stubbed."));
        };
    }
}
