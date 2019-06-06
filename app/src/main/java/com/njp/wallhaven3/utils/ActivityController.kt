package com.njp.wallhaven3.utils

import android.app.Activity
import com.njp.wallhaven3.ui.detail.DetailActivity

/**
 * 管理Activity的工具
 */
class ActivityController private constructor() {

    companion object {
        private var instance: ActivityController? = null
        fun getInstance(): ActivityController {
            if (instance == null) {
                instance = ActivityController()
            }
            return instance!!
        }
    }

    private val activities = ArrayList<Activity>()
    private var preDetailSign = false

    fun add(activity: Activity) {
        if (activities.size >= 5) {
            activities.first().finish()
        }
        activities.add(activity)
        if (activity is DetailActivity) {
            preDetailSign = false
        }
    }

    fun remove(activity: Activity) {
        if (activity is DetailActivity && !preDetailSign) {
            CommonDataHolder.removeData()
            activity.postMessage()
        }
        activities.remove(activity)
    }

    fun clearDetail() {
        activities.filterIsInstance<DetailActivity>().forEach { it.finish() }
        preDetailSign = true
    }

}