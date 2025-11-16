package com.example.ondeguardei.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ondeguardei.data.AppDatabase
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.data.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository
    val allItems: Flow<List<Item>>

    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(itemDao)
        allItems = repository.allItems
    }

    fun getItemById(id: Int): Flow<Item?> = repository.getItemById(id)

    fun insert(item: Item, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insert(item)
            onComplete(id)
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
}
