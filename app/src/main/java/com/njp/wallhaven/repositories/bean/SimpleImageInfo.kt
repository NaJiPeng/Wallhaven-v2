package com.njp.wallhaven.repositories.bean

import com.njp.wallhaven.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * 简单图片信息
 */
@Table(database = AppDatabase::class)
data class SimpleImageInfo(
        @PrimaryKey(autoincrement = true) var id: Int = 0,
        @Column var imageId: Int = 0,
        @Column var url: String = "",
        @Column var isSPlash: Boolean = false,
        @Column var isHistory: Boolean = false,
        @Column var isStared: Boolean = false,
        @Column var data: String = ""
) : BaseModel()