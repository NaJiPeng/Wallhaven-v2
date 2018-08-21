package com.njp.wallhaven.utils

import com.njp.wallhaven.repositories.bean.DetailImageInfo
import com.njp.wallhaven.repositories.bean.SimpleImageInfo

/**
 * 图片列表页与图片详情页之间共享数据类
 */
class CommonDataHolder {
    companion object {
        private var simpleData: List<SimpleImageInfo>? = null
        private var detailData: Array<DetailImageInfo?>? = null

        fun setSimpleData(list: List<SimpleImageInfo>) {
            simpleData = list
            detailData = Array(list.size) { null }
        }

        fun getSimpleData() = simpleData!!

        fun getDetailData(position: Int) = detailData?.get(position - 1)

        fun setDetailData(detailImageInfo: DetailImageInfo, position: Int) {
            detailData?.set(position - 1, detailImageInfo)
        }

        fun removeData() {
            simpleData = null
            detailData = null
        }
    }
}