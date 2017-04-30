package com.renegens.firefy.di

import com.renegens.firefy.Component
import com.renegens.firefy.service.ServiceModule
import com.renegens.firefy.ui.MainModule
import javax.inject.Singleton

@Singleton
@dagger.Component(modules = arrayOf(
        RetrofitModule::class,
        MainModule::class,
        FirebaseModule::class,
        ServiceModule::class
))
interface ApplicationComponent : Component {}
