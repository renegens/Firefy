package com.justupped.android.util

import rx.Subscriber

class SimpleActionSubscriber<T>(val action: (T) -> Unit) : Subscriber<T>() {

    override fun onError(e: Throwable) {
        throw e
    }

    override fun onNext(t: T) {
        action(t)
    }

    override fun onCompleted() {
    }

}
