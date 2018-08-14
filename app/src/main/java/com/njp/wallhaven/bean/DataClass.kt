package com.njp.wallhaven.bean

import java.util.*

/**
 * 简单图片信息
 */
data class SimpleImageInfo(
        var url: String,
        var href: String
)

/**
 * 首页图片信息
 */
data class SplashImages(
        var date: String,
        var images: List<SimpleImageInfo>
)