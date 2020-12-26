package com.example.mvvmdemo.utils

import android.content.Context
import com.example.mvvmdemo.common.BaseApplication
import com.example.mvvmdemo.common.BaseConstant

object AppPrefsUtils {
    private val sp =
        BaseApplication.context.getSharedPreferences(BaseConstant.TABLE_PREFS, Context.MODE_PRIVATE)
    private val ed = sp.edit();

    /*
      Boolean数据
   */
    fun putBoolean(key: String, value: Boolean) {
        ed.putBoolean(key, value)
        ed.commit()
    }

    /*
        默认 false
     */
    fun getBoolean(key: String): Boolean {
        return sp.getBoolean(key, true)
    }

    /*
        String数据
     */
    fun putString(key: String, value: String) {
        ed.putString(key, value)
        ed.commit()
    }

    /*
        默认 ""
     */
    fun getString(key: String): String {
        return sp.getString(key, "") as String
    }

    /*
        Int数据
     */
    fun putInt(key: String, value: Int) {
        ed.putInt(key, value)
        ed.commit()
    }

    /*
        默认 0
     */
    fun getInt(key: String): Int {
        return sp.getInt(key, 0)
    }

    /*
        Long数据
     */
    fun putLong(key: String, value: Long) {
        ed.putLong(key, value)
        ed.commit()
    }

    /*
        默认 0
     */
    fun getLong(key: String): Long {
        return sp.getLong(key, 0)
    }

    /*
        Set数据
     */
    fun putStringSet(key: String, set: Set<String>) {
        // 从本地取出,再加入
        val localSet = getStringSet(key).toMutableSet()
        localSet.addAll(set)
        ed.putStringSet(key, localSet)
        ed.commit()
    }

    /*
        默认空set
     */
    fun getStringSet(key: String): Set<String> {
        val set = setOf<String>()
        return sp.getStringSet(key, set) as Set<String>
    }

    /*
       删除key数据
    */
    fun remove(key: String) {
        ed.remove(key)
        ed.commit()
    }
}