package com.renegens.firefy

import android.util.Log
import com.google.firebase.crash.FirebaseCrash
import timber.log.Timber

class TimberFirebase : Timber.Tree() {

    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        FirebaseCrash.log(message)
        if (t != null) {
            FirebaseCrash.report(t)
        }
    }

}
