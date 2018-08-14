package com.njp.wallhaven.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {

    private lateinit var toast: Toast

    fun init(context: Context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }

    fun show(content: String) {
        toast.setText(content)
        toast.show()
    }

}
