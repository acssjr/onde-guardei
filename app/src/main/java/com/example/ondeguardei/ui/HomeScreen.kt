package com.example.ondeguardei.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * TELA 1: HOME
 *
 * Layout:
 * - Top: Greeting "Olá, [Nome]" + ícones de notificação/settings
 * - Search bar
 * - Seção "Adicionados Recentemente" com hero card do último item
 * - Seção "Estatísticas" com 2 stats cards lado a lado
 * - Seção "Todos os Itens" com LazyColumn de item cards
 */
@Composable
fun HomeScreen(
    viewModel: ItemViewModel,
    onItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.allItems.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Estatísticas
    val totalItems = items.size
    val todayItems = remember(items) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        items.count { it.createdAt >= today }
    }

    // Item mais recente para o Hero Card
    val latestItem = items.maxByOrNull { it.createdAt }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 48.dp, // Status bar padding
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // === HEADER: Greeting + Icons ===
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Olá! 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "O que você está procurando?",
                            fontSize = 14.sp,
                            color = White70
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { /* TODO: Notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificações",
                                tint = White70
                            )
                        }
                        IconButton(onClick = { /* TODO: Settings */ }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações",
                                tint = White70
                            )
                        }
                    }
                }
            }

            // === SEARCH BAR ===
            item {
                CustomSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = onSearchClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // === SEÇÃO: ADICIONADOS RECENTEMENTE ===
            item {
                Column {
                    Text(
                        text = "Adicionado Recentemente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    HeroCard(
                        item = latestItem,
                        onClick = {
                            latestItem?.let { onItemClick(it.id) }
                        }
                    )
                }
            }

            // === SEÇÃO: ESTATÍSTICAS ===
            item {
                Column {
                    Text(
                        text = "Estatísticas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total de Itens
                        StatsCard(
                            icon = Icons.Default.ShoppingCart,
                            iconColor = Primary,
                            label = "Total de Itens",
                            value = totalItems.toString(),
                            change = if (todayItems > 0) "+$todayItems hoje" else null,
                            modifier = Modifier.weight(1f)
                        )

                        // Itens Adicionados Hoje
                        StatsCard(
                            icon = Icons.Default.DateRange,
                            iconColor = Secondary,
                            label = "Adicionados Hoje",
                            value = todayItems.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // === SEÇÃO: TODOS OS ITENS ===
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Todos os Itens",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${items.size} itens",
                        fontSize = 14.sp,
                        color = White70
                    )
                }
            }

            // Lista de itens
            if (items.isEmpty()) {
                item {
                    EmptyState(
                        message = "Nenhum item cadastrado",
                        description = "Clique em 'Adicionar' para começar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    )
                }
            } else {
                items(items = items, key = { it.id }) { item ->
                    ItemListCard(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                }
            }

            // Espaço extra no final para evitar sobreposição com bottom nav
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Empty State genérico
 */
@Composable
fun EmptyState(
    message: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = White70
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = White50
        )
    }
}
