package com.njp.wallhaven.utils

import com.njp.wallhaven.repositories.bean.SimpleImageInfo

/**
 * 图片列表页与图片详情页之间共享数据类
 */
class CommonDataHolder {
    companion object {
        private var data: List<SimpleImageInfo>? = null

        fun setData(list: List<SimpleImageInfo>) {
            data = list
        }

        fun getData() = data!!
    }
}