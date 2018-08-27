package com.njp.wallhaven.repositories.bean

import com.njp.wallhaven.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = AppDatabase::class)
data class History(
        @PrimaryKey var string: String = "",
        @Column var time: Long = 0
)