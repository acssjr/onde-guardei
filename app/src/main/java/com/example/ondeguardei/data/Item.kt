package com.example.ondeguardei.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Item - representa um objeto guardado pelo usuário
 *
 * @param id Identificador único
 * @param name Nome do item
 * @param location Onde o item está guardado
 * @param description Descrição adicional (opcional)
 * @param imagePath Caminho da foto do item
 * @param categoriaId ID da categoria (FK para Category)
 * @param createdAt Timestamp de quando foi adicionado
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val description: String = "",
    val imagePath: String? = null,
    val categoriaId: Long = 7, // Default: Categoria "Outros"
    val createdAt: Long = System.currentTimeMillis()
)
