package com.it10x.foodappgstav7_09

import android.app.Application
import android.util.Log
import com.it10x.foodappgstav7_09.firebase.ClientIdStore

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val clientId = ClientIdStore.get(this)

        if (clientId.isNullOrBlank()) {
            Log.w("MyApp", "ClientId not found (will initialize later)")
        } else {
            Log.d("MyApp", "ClientId found: $clientId (Firebase init deferred)")
        }
    }
}