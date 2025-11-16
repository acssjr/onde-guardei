package com.example.ondeguardei.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ondeguardei.data.AppDatabase
import com.example.ondeguardei.data.Category
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    val allItems: Flow<List<Item>>
    val allCategories: Flow<List<Category>>

    // Estado de busca
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ItemRepository(
            itemDao = database.itemDao(),
            categoryDao = database.categoryDao()
        )
        allItems = repository.allItems
        allCategories = repository.allCategories
    }

    // === ITEM OPERATIONS ===

    fun getItemById(id: Int): Flow<Item?> = repository.getItemById(id)

    fun insert(item: Item, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val id = repository.insert(item)
                onComplete(id)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun update(item: Item) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun delete(item: Item) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // === CATEGORY OPERATIONS ===

    suspend fun getCategoryById(id: Long): Category? {
        return repository.getCategoryById(id)
    }

    suspend fun getItemCountByCategory(categoryId: Long): Int {
        return repository.getItemCountByCategory(categoryId)
    }

    // === SEARCH ===

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}
