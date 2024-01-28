package com.example.mealplanner

import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds

class YandexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            Log.d("Yandex mobile ads", "SDK initialized");

        }
    }
}