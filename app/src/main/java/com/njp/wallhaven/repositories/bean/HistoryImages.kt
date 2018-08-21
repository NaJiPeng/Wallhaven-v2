package com.njp.wallhaven.repositories.bean

import com.njp.wallhaven.repositories.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = AppDatabase::class)
class HistoryImages : BaseModel() {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column
    var date: String = ""

    var images: MutableList<SimpleImageInfo>? = null

    @OneToMany(methods = [OneToMany.Method.ALL], variableName = "images")
    fun createImages(): MutableList<SimpleImageInfo> {
        if (images == null) {
            images = SQLite.select()
                            .from(SimpleImageInfo::class.java)
                            .where(SimpleImageInfo_Table.isHistory.eq(true) and SimpleImageInfo_Table.date.eq(date))
                            .queryList()

        }
        return images!!
    }

    override fun save(): Boolean {
        images?.forEach {
            it.isHistory = true
            it.date = date
            it.save()
        }
        return super.save()
    }

    override fun delete(): Boolean {
        images?.forEach {
            it.delete()
        }
        return super.delete()
    }


}