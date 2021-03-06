package com.njp.wallhaven3.ui.tag

import com.njp.wallhaven3.repositories.bean.Tag
import com.njp.wallhaven3.repositories.bean.TagImageInfo

interface TagContract {

    interface View {
        fun onGetTagImageInfo(imageInfo: TagImageInfo)
        fun onGetTagImageInfoFail(msg: String)
    }

    interface Presenter {
        fun getTagImageInfo(tagId: String)
        fun starTag(tag: Tag)
        fun unStarTag(tag: Tag)
        fun isTagStared(tag: Tag): Boolean
    }

}