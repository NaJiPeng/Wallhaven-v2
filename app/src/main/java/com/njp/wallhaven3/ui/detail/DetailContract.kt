package com.njp.wallhaven3.ui.detail

import com.njp.wallhaven3.repositories.bean.DetailImageInfo
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

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