package com.example.ondeguardei.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel

/**
 * Rotas de navegação do app
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Add : Screen("add", "Adicionar", Icons.Default.Add)
    object Categories : Screen("categories", "Categorias", Icons.Default.Apps)
    object ItemDetail : Screen("item_detail/{itemId}", "Detalhes", Icons.Default.Info)
    object Search : Screen("search", "Busca", Icons.Default.Search)
}

/**
 * Itens do Bottom Navigation
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Add,
    Screen.Categories
)

/**
 * App Scaffold com Bottom Navigation
 */
@Composable
fun AppScaffold(
    viewModel: ItemViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        containerColor = Background,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                // TODO: Implementar HomeScreen
                Text("Home Screen - Em construção", color = Color.White)
            }

            composable(Screen.Add.route) {
                // TODO: Implementar AddItemScreen
                Text("Add Screen - Em construção", color = Color.White)
            }

            composable(Screen.Categories.route) {
                // TODO: Implementar CategoriesScreen
                Text("Categories Screen - Em construção", color = Color.White)
            }

            composable(Screen.ItemDetail.route) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                if (itemId != null) {
                    // TODO: Implementar ItemDetailScreen
                    Text("Item Detail Screen - Em construção", color = Color.White)
                }
            }

            composable(Screen.Search.route) {
                // TODO: Implementar SearchScreen
                Text("Search Screen - Em construção", color = Color.White)
            }
        }
    }
}

/**
 * BOTTOM NAVIGATION BAR
 *
 * Especificações:
 * - 3 itens: Home, Adicionar, Categorias
 * - Background: Surface (#1C1C1E)
 * - Ícone selecionado usa Primary color com indicator background (Primary 20% opacity)
 * - Ícones não selecionados: branco 50% opacity
 * - Labels sempre visíveis (12sp)
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier,
        containerColor = Surface,
        tonalElevation = 0.dp,
        contentColor = Color.White
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = if (selected) Primary else White50
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Primary else White50
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to start destination to avoid building large back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = Primary20,
                    unselectedIconColor = White50,
                    unselectedTextColor = White50
                )
            )
        }
    }
}

/**
 * Extended FAB para adicionar item
 * (Pode ser usado opcionalmente em vez do Bottom Nav item)
 */
@Composable
fun AddItemFAB(
    onClick: () -> Unit,
    expanded: Boolean = true,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Primary,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (expanded) {
            // Extended FAB
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Item"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Adicionar Item")
            }
        } else {
            // Regular FAB
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar Item"
            )
        }
    }
}
