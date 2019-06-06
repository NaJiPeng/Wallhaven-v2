package com.njp.wallhaven3.utils

import android.app.Application
import com.njp.wallhaven3.R

class ColorUtil private constructor() {

    companion object {
        private var instance: ColorUtil? = null
        private lateinit var context: Application
        fun init(application: Application) {
            this.context = application
        }

        fun getInstance(): ColorUtil {
            if (instance == null) {
                instance = ColorUtil()
            }
            return instance!!
        }
    }

    val list = listOf(
            "Red" to context.resources.getColor(R.color.red),
            "Pink" to context.resources.getColor(R.color.pink),
            "Purple" to context.resources.getColor(R.color.purple),
            "Deep Purple" to context.resources.getColor(R.color.deepPurple),
            "Indigo" to context.resources.getColor(R.color.indigo),
            "Blue" to context.resources.getColor(R.color.blue),
            "Light Blue" to context.resources.getColor(R.color.lightBlue),
            "Cyan" to context.resources.getColor(R.color.cyan),
            "Teal" to context.resources.getColor(R.color.teal),
            "Green" to context.resources.getColor(R.color.green),
            "Light Green" to context.resources.getColor(R.color.lightGreen),
            "Lime" to context.resources.getColor(R.color.lime),
            "Yellow" to context.resources.getColor(R.color.yellow),
            "Amber" to context.resources.getColor(R.color.amber),
            "Orange" to context.resources.getColor(R.color.orange),
            "Deep Orange" to context.resources.getColor(R.color.deepOrange),
            "Brown" to context.resources.getColor(R.color.brown),
            "Grey" to context.resources.getColor(R.color.grey),
            "Blue Grey" to context.resources.getColor(R.color.blueGrey),
            "Black" to context.resources.getColor(R.color.black)
    )

    fun getCurrentColor() = list.asSequence()
            .filter {
                it.first == SPUtil.getInstance().getString("color", "Blue")
            }.last()

}