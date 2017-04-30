package com.renegens.firefy.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides


@Module
class FirebaseModule {

    @Provides fun provideDb() = FirebaseDatabase.getInstance().getReference("events")

    @Provides fun provideStorage() = FirebaseStorage.getInstance().getReference("images")

    @Provides fun provideAuth() = FirebaseAuth.getInstance()
}
