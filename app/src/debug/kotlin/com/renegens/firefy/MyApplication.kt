package com.renegens.firefy

import android.app.Application
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.renegens.firefy.di.DaggerApplicationComponent
import com.renegens.firefy.di.FirebaseModule
import com.renegens.firefy.di.RetrofitModule
import com.renegens.firefy.service.ServiceModule
import com.renegens.firefy.ui.MainModule

import timber.log.Timber

class MyApplication : Application() {

    private lateinit var component: Component

    override fun onCreate() {
        super.onCreate()
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                return super.createStackElementTag(element) + ":" + element.lineNumber
            }
        })

        component = DaggerApplicationComponent
                .builder()
                .retrofitModule(RetrofitModule())
                .firebaseModule(FirebaseModule())
                .mainModule(MainModule())
                .serviceModule(ServiceModule())
                .build()

        RxPaparazzo.register(this)

    }

    open fun getComponent() = component

}

