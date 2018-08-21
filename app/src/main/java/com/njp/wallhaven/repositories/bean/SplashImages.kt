package com.njp.wallhaven.repositories.bean

import com.njp.wallhaven.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = AppDatabase::class)
class SplashImages : BaseModel() {

    @PrimaryKey(autoincrement = true)
    var id: Int = 1

    @Column
    var date: String = ""

    var images: List<SimpleImageInfo>? = null

    @OneToMany(methods = [OneToMany.Method.ALL], variableName = "images")
    fun createImages(): List<SimpleImageInfo> {
        if (images == null) {
            images = SQLite.select()
                    .from(SimpleImageInfo::class)
                    .where(SimpleImageInfo_Table.isSPlash.eq(true))
                    .queryList()
        }
        return images!!
    }

    override fun save(): Boolean {
        images?.forEach {
            it.isSPlash = true
            it.save()
        }
        return super.save()
    }

    override fun delete(): Boolean {
        images?.forEach { it.delete() }
        return super.delete()
    }


}