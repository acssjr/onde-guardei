package com.example.ondeguardei.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ondeguardei.data.Category
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel
import kotlinx.coroutines.launch

/**
 * TELA 5: CATEGORIAS
 *
 * Grid 2 colunas com cards de categoria
 * Cada card:
 * - Ícone grande colorido (48dp)
 * - Nome da categoria (18sp, medium)
 * - Contador de itens (14sp, 70% opacity)
 * - Background: Surface Variant
 * - Altura: 140dp
 * - Cantos: 20dp
 */
@Composable
fun CategoriesScreen(
    viewModel: ItemViewModel,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Mapa de contadores de itens por categoria
    var categoryItemCounts by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }

    // Carregar contadores quando as categorias mudarem
    LaunchedEffect(categories) {
        val counts = mutableMapOf<Long, Int>()
        categories.forEach { category ->
            scope.launch {
                counts[category.id] = viewModel.getItemCountByCategory(category.id)
                categoryItemCounts = counts.toMap()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 48.dp)
            ) {
                Text(
                    text = "Categorias",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Organize seus itens por categoria",
                    fontSize = 14.sp,
                    color = White70
                )
            }

            // Grid de categorias
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        itemCount = categoryItemCounts[category.id] ?: 0,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

/**
 * Card de Categoria para o Grid
 */
@Composable
fun CategoryCard(
    category: Category,
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceVariant,
        tonalElevation = 0.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícone grande colorido
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = category.getColor().copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.icone),
                    contentDescription = category.nome,
                    tint = category.getColor(),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nome da categoria
            Text(
                text = category.nome,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Contador de itens
            Text(
                text = "$itemCount ${if (itemCount == 1) "item" else "itens"}",
                fontSize = 14.sp,
                color = White70
            )
        }
    }
}

/**
 * Mapeia nome do ícone para ImageVector
 */
fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Restaurant" -> Icons.Default.Restaurant
        "Bed" -> Icons.Default.Done // Bed não existe no material, usando Done
        "Weekend" -> Icons.Default.Star // Weekend não existe, usando Star para sala
        "Shower" -> Icons.Default.Place // Shower não existe, usando Place
        "DirectionsCar" -> Icons.Default.Edit // DirectionsCar não existe, usando Edit
        "Work" -> Icons.Default.Build
        "MoreHoriz" -> Icons.Default.MoreHoriz
        else -> Icons.Default.Category
    }
}
