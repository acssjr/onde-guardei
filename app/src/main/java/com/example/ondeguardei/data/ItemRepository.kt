package com.example.ondeguardei.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository central para acesso a dados
 * Abstrai a fonte de dados (Room) das ViewModels
 */
class ItemRepository(
    private val itemDao: ItemDao,
    private val categoryDao: CategoryDao
) {
    // Items
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

    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun getItemCountByCategory(categoryId: Long): Int {
        return categoryDao.getItemCountByCategory(categoryId)
    }
}
