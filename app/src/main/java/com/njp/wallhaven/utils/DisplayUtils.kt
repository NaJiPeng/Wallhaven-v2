package com.njp.wallhaven.utils

import android.content.Context

fun px2dp(context: Context, px: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

fun dp2px(context: Context, dp: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun px2sp(context: Context, px: Int): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (px / fontScale + 0.5f).toInt()
}

fun sp2px(context: Context, sp: Int): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (sp * fontScale + 0.5f).toInt()
}