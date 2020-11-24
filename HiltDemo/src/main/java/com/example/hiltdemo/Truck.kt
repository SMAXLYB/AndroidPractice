package com.example.hiltdemo

import android.util.Log
import javax.inject.Inject

class Truck @Inject constructor(val driver: Driver) {

    @Inject
    @BindElectricEngine
    lateinit var engine: Engine

    fun deliver() {
        engine.start()
        Log.d(Companion.TAG, "deliver: Truck is delivering cargo Driven by $driver")
        engine.shutdown()
    }

    companion object {
        private const val TAG = "Truck"
    }
}