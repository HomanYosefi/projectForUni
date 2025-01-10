package com.example.homanproj

import android.app.Application
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Add any dependencies if needed
}

@HiltAndroidApp
class ProducerConsumerApp : Application()