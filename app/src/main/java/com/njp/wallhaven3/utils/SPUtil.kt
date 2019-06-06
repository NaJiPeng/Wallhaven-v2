package com.njp.wallhaven3.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences工具类
 */
class SPUtil private constructor() {

    companion object {

        private lateinit var context: Application

        private var instance: SPUtil? = null

        fun init(context: Application) {
            this.context = context
        }

        fun getInstance(): SPUtil {
            if (instance == null) {
                instance = SPUtil()
            }
            return instance!!
        }
    }

    private val sp: SharedPreferences

    init {
        sp = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)
    }

    fun putInt(key: String, values: Int) {
        sp.edit().putInt(key, values).apply()
    }

    fun putLong(key: String, values: Long) {
        sp.edit().putLong(key, values).apply()
    }

    fun putFloat(key: String, values: Float) {
        sp.edit().putFloat(key, values).apply()
    }

    fun putString(key: String, values: String) {
        sp.edit().putString(key, values).apply()
    }

    fun putBoolean(key: String, values: Boolean) {
        sp.edit().putBoolean(key, values).apply()
    }

    fun getInt(key: String, defValues: Int): Int {
        return sp.getInt(key, defValues)
    }

    fun getLong(key: String, defValues: Long): Long {
        return sp.getLong(key, defValues)
    }

    fun getFloat(key: String, defValues: Float): Float {
        return sp.getFloat(key, defValues)
    }

    fun getBoolean(key: String, defValues: Boolean): Boolean {
        return sp.getBoolean(key, defValues)
    }

    fun getString(key: String, defValues: String): String {
        return sp.getString(key, defValues)!!
    }

}