package com.njp.wallhaven.ui.tag

import com.njp.wallhaven.repositories.bean.TagImageInfo

interface TagContract {

    interface View {
        fun onGetTagImageInfo(imageInfo: TagImageInfo)
        fun onGetTagImageInfoFail(msg: String)
    }

    interface Presenter {
        fun getTagImageInfo(tagId: Int)
    }

}