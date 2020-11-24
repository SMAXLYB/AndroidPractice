package com.example.hiltdemo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


// 对于activity和application,也可以不用注解,hilt提供了内置注入
@Singleton
class Driver @Inject constructor(@ApplicationContext val context: Context) {

}