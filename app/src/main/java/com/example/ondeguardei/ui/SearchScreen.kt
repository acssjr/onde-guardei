package com.example.ondeguardei.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ondeguardei.data.Category
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel

/**
 * TELA 4: BUSCA/RESULTADOS
 *
 * Layout:
 * - Search bar no topo (sempre ativo)
 * - Chips de filtro rápido abaixo: "Todos", categorias
 * - LazyColumn com resultados (item list cards)
 * - Empty state se não houver resultados
 */
@Composable
fun SearchScreen(
    viewModel: ItemViewModel,
    onItemClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    // Filtrar itens baseado na busca e categoria selecionada
    val filteredItems = remember(searchQuery, selectedCategoryId, allItems) {
        var items = allItems

        // Filtrar por busca
        if (searchQuery.isNotBlank()) {
            items = items.filter { item ->
                item.name.contains(searchQuery, ignoreCase = true) ||
                        item.location.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
            }
        }

        // Filtrar por categoria
        if (selectedCategoryId != null) {
            items = items.filter { it.categoriaId == selectedCategoryId }
        }

        items
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // === HEADER COM SEARCH BAR ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(top = 32.dp) // Status bar padding
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }

                    CustomSearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onSearch = { /* Busca em tempo real */ },
                        placeholder = "Buscar itens...",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // === CHIPS DE FILTRO ===
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip "Todos"
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary20,
                                selectedLabelColor = Primary,
                                containerColor = SurfaceVariant,
                                labelColor = White70
                            )
                        )
                    }

                    // Chips de categorias
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.nome) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getCategoryIcon(category.icone),
                                    contentDescription = null,
                                    tint = category.getColor(),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = category.getColor().copy(alpha = 0.2f),
                                selectedLabelColor = Color.White,
                                containerColor = SurfaceVariant,
                                labelColor = White70
                            )
                        )
                    }
                }
            }

            Divider(color = White50.copy(alpha = 0.1f), thickness = 1.dp)

            // === RESULTADOS ===
            if (filteredItems.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        message = if (searchQuery.isBlank()) {
                            "Comece a digitar para buscar"
                        } else {
                            "Nenhum item encontrado"
                        },
                        description = if (searchQuery.isBlank()) {
                            "Digite o nome, localização ou descrição"
                        } else {
                            "Tente buscar com outras palavras-chave"
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header de resultados
                    item {
                        Text(
                            text = "${filteredItems.size} ${if (filteredItems.size == 1) "resultado encontrado" else "resultados encontrados"}",
                            fontSize = 14.sp,
                            color = White70
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Lista de itens
                    items(items = filteredItems, key = { it.id }) { item ->
                        ItemListCard(
                            item = item,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}
