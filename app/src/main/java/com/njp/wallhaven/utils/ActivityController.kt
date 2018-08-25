package com.njp.wallhaven.utils

import android.app.Activity
import com.njp.wallhaven.ui.detail.DetailActivity
import com.njp.wallhaven.ui.tag.TagActivity

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

    private val list = ArrayList<Activity>()
    var preDetailSign = false

    fun add(activity: Activity) {
        list.add(activity)
    }

    fun remove(activity: Activity) {
        list.remove(activity)
        if (list.filterIsInstance<DetailActivity>().isEmpty() && !preDetailSign) {
            CommonDataHolder.removeData()
        }
    }

    fun clearDetail() {
        list.filterIsInstance<DetailActivity>().forEach { it.finish() }
    }

    fun clearTag() {
        list.filterIsInstance<TagActivity>().let {
            if (it.size >= 2) {
                it.last().finish()
            }
        }
    }

}