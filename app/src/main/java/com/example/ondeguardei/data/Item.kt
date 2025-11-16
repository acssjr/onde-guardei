package com.example.ondeguardei.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val description: String = "",
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
