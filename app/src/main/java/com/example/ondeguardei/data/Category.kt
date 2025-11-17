package com.example.ondeguardei.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ondeguardei.ui.theme.*

/**
 * Entidade de Categoria para organizar os itens
 *
 * @param id Identificador único da categoria
 * @param nome Nome da categoria (ex: "Cozinha", "Quarto")
 * @param icone Nome do ícone Material (ex: "Restaurant", "Bed")
 * @param cor Código hexadecimal da cor (ex: "#FF6B6B")
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val icone: String,
    val cor: String
) {
    /**
     * Converte a string de cor hex para Compose Color
     */
    fun getColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(cor))
        } catch (e: Exception) {
            CategoryOthers
        }
    }

    companion object {
        /**
         * Categorias pré-definidas do aplicativo
         */
        fun getDefaultCategories(): List<Category> {
            return listOf(
                Category(
                    id = 1,
                    nome = "Cozinha",
                    icone = "Restaurant",
                    cor = "#FF6B6B"
                ),
                Category(
                    id = 2,
                    nome = "Quarto",
                    icone = "Bed",
                    cor = "#4ECDC4"
                ),
                Category(
                    id = 3,
                    nome = "Sala",
                    icone = "Weekend",
                    cor = "#95E1D3"
                ),
                Category(
                    id = 4,
                    nome = "Banheiro",
                    icone = "Shower",
                    cor = "#6C5CE7"
                ),
                Category(
                    id = 5,
                    nome = "Garagem",
                    icone = "DirectionsCar",
                    cor = "#FD79A8"
                ),
                Category(
                    id = 6,
                    nome = "Escritório",
                    icone = "Work",
                    cor = "#FDCB6E"
                ),
                Category(
                    id = 7,
                    nome = "Outros",
                    icone = "MoreHoriz",
                    cor = "#74B9FF"
                )
            )
        }
    }
}
