package com.example.ondeguardei

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ondeguardei.ui.*
import com.example.ondeguardei.ui.theme.Background
import com.example.ondeguardei.ui.theme.OndeGuardeiTheme
import com.example.ondeguardei.viewmodel.ItemViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permissão concedida ou negada
        // TODO: Mostrar feedback ao usuário se negada
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge (status bar transparente)
        enableEdgeToEdge()

        // Solicitar permissão de câmera
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            OndeGuardeiTheme {
                OndeGuardeiApp()
            }
        }
    }
}

@Composable
fun OndeGuardeiApp() {
    val navController = rememberNavController()
    val viewModel: ItemViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        bottomBar = {
            // Bottom Navigation
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
        ) {
            // === TELA 1: HOME ===
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onItemClick = { itemId ->
                        navController.navigate("item_detail/$itemId")
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // === TELA 2: ADICIONAR ITEM ===
            composable(Screen.Add.route) {
                // TODO: Implementar tela de adicionar com câmera
                // Por enquanto, vamos usar a tela antiga ou criar uma simples
                CameraScreen(
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    },
                    onImageCaptured = { path ->
                        // TODO: Navegar para formulário com imagem capturada
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            // === TELA 5: CATEGORIAS ===
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onCategoryClick = { category ->
                        // TODO: Filtrar itens por categoria
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            // === TELA 3: DETALHES DO ITEM ===
            composable(
                route = "item_detail/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable

                ItemDetailScreen(
                    itemId = itemId,
                    viewModel = viewModel,
                    onEditClick = { item ->
                        // TODO: Navegar para tela de edição
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onDeleteSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // === TELA 4: BUSCA ===
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onItemClick = { itemId ->
                        navController.navigate("item_detail/$itemId")
                    },
                    onBackClick = {
                        viewModel.clearSearch()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
