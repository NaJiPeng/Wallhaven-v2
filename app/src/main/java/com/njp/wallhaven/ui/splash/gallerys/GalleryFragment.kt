package com.njp.wallhaven.ui.splash.gallerys

import android.support.v4.app.Fragment
import com.njp.wallhaven.repositories.bean.SimpleImageInfo

abstract class GalleryFragment :Fragment(){

    abstract fun setImages(images: List<SimpleImageInfo>)

}