package com.example.ondeguardei.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operações de Categoria
 */
@Dao
interface CategoryDao {
    /**
     * Retorna todas as categorias como Flow (observável)
     */
    @Query("SELECT * FROM categories ORDER BY nome ASC")
    fun getAllCategories(): Flow<List<Category>>

    /**
     * Busca uma categoria por ID
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?

    /**
     * Insere uma categoria (ignora se já existe)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category): Long

    /**
     * Insere múltiplas categorias (usado para seed inicial)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<Category>)

    /**
     * Conta quantos itens existem em uma categoria
     */
    @Query("SELECT COUNT(*) FROM items WHERE categoriaId = :categoryId")
    suspend fun getItemCountByCategory(categoryId: Long): Int
}
