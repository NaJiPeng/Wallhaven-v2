package com.njp.wallhaven3.repositories.bean

import com.njp.wallhaven3.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel


@Table(database = AppDatabase::class)
data class Tag(
        @PrimaryKey
        var id: String = "",
        @Column
        var name: String = ""
) : BaseModel()



