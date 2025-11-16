package com.example.ondeguardei.data

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    val allItems: Flow<List<Item>> = itemDao.getAllItems()

    fun getItemById(id: Int): Flow<Item?> = itemDao.getItemById(id)

    suspend fun insert(item: Item): Long {
        return itemDao.insert(item)
    }

    suspend fun update(item: Item) {
        itemDao.update(item)
    }

    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

    suspend fun deleteById(id: Int) {
        itemDao.deleteById(id)
    }
}
