package com.renegens.firefy

import android.app.Application
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.renegens.firefy.di.FirebaseModule
import com.renegens.firefy.di.RetrofitModule
import com.renegens.firefy.service.ServiceModule
import com.renegens.firefy.ui.MainModule
import dagger.Component
import timber.log.Timber

class MyApplication: Application() {

    private lateinit var component: Component

    override fun onCreate() {
        super.onCreate()
        Timber.plant(TimberFirebase())
        RxPaparazzo.register(this)

        component = DaggerApplicationComponent
                .builder()
                .retrofitModule(RetrofitModule())
                .firebaseModule(FirebaseModule())
                .mainModule(MainModule())
                .serviceModule(ServiceModule())
                .build()
    }

    open fun getComponent() = component

}
