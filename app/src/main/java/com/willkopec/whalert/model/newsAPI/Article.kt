package com.willkopec.whalert.model.newsAPI

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)

data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
) : Serializable
