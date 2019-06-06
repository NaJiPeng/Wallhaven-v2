package com.njp.wallhaven.ui.detail

import com.njp.wallhaven.repositories.bean.DetailImageInfo
import com.njp.wallhaven.repositories.bean.SimpleImageInfo

interface DetailContract {

    interface View {
        fun onGetDetailImageSuccess(detailImageInfo: DetailImageInfo)
        fun onGetDetailImageFail(msg: String)
    }

    interface Presenter {
        fun getDetailImage(id: String)
        fun starImage(image: SimpleImageInfo)
        fun unStarImage(image: SimpleImageInfo)
        fun isStared(image: SimpleImageInfo): Boolean
    }

}