package com.example.hiltdemo

import android.util.Log
import javax.inject.Inject

class ElectricEngine @Inject constructor() : Engine {
    override fun start() {
        Log.d(TAG, "start: 电力引擎正在启动")
    }

    override fun shutdown() {
        Log.d(TAG, "shutdown: 电力引擎正在关闭")
    }

    companion object {
        private const val TAG = "ElectricEngine"
    }
}