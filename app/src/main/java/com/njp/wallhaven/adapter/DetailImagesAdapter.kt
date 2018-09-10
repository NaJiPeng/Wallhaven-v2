package com.njp.wallhaven.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.ui.detail.DetailFragment

class DetailImagesAdapter(fm: FragmentManager, private val images: List<SimpleImageInfo>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int) = DetailFragment.create(images[p0], count, p0)

    override fun getCount() = images.size



}