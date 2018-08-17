package com.njp.wallhaven.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.njp.wallhaven.ui.main.MainFragment

class TabAdapter(fm: FragmentManager, val list: List<Pair<String, String>>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int) = MainFragment.create(list[p0].first)

    override fun getCount() = list.size

    override fun getPageTitle(position: Int) = list[position].second

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

}