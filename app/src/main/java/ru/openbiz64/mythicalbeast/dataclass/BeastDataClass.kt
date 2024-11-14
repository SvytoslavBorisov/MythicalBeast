package ru.openbiz64.mythicalbeast.dataclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "beast_list")
data class BeastDataClass(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "picUrl")
    val picUrl: String,

    @ColumnInfo(name = "htmlUrl")
    val htmlUrl: String,

    @ColumnInfo(name = "mythology")
    val mythology: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean

): Serializable