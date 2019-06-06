package com.njp.wallhaven3.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo
import com.njp.wallhaven3.ui.detail.DetailFragment

class DetailImagesAdapter(fm: FragmentManager, private val images: List<SimpleImageInfo>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int) = DetailFragment.create(images[p0], count, p0)

    override fun getCount() = images.size



}