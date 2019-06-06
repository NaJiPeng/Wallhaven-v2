package com.njp.wallhaven3.repositories.bean

import com.njp.wallhaven3.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * 简单图片信息
 */
@Table(database = AppDatabase::class)
data class SimpleImageInfo(
        @PrimaryKey var id: String = "",
        @Column var url: String = "",
        @Column var isSPlash: Boolean = false,
        @Column var isHistory: Boolean = false,
        @Column var isStared: Boolean = false,
        @Column var time: Long = 0
) : BaseModel() {
    fun selfCheck() {
        if (!isSPlash && !isStared && !isHistory) delete()
    }
}