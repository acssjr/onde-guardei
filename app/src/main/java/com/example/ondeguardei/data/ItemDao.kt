package com.example.ondeguardei.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int): Flow<Item?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: Int)
}
