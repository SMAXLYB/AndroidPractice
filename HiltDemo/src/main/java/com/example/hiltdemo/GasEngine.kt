package com.example.hiltdemo

import android.util.Log
import javax.inject.Inject

class GasEngine @Inject constructor() : Engine {

    override fun start() {
        Log.d(TAG, "start: 燃气引擎启动")
    }

    override fun shutdown() {
        Log.d(TAG, "shutdown: 燃气引擎关闭")
    }

    companion object {
        private const val TAG = "GasEngine"
    }

}