package com.example.ondeguardei

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.*
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel

class MainActivity : ComponentActivity() {
    private var cameraPermissionGranted by mutableStateOf(false)
    private var showPermissionDialog by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        cameraPermissionGranted = isGranted
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge (status bar transparente)
        enableEdgeToEdge()

        // Solicitar permissão de câmera
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            OndeGuardeiTheme {
                OndeGuardeiApp(
                    showPermissionDialog = showPermissionDialog,
                    onDismissPermissionDialog = { showPermissionDialog = false },
                    onRequestPermissionAgain = {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        showPermissionDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun OndeGuardeiApp(
    showPermissionDialog: Boolean = false,
    onDismissPermissionDialog: () -> Unit = {},
    onRequestPermissionAgain: () -> Unit = {}
) {
    val navController = rememberNavController()
    val viewModel: ItemViewModel = viewModel()

    // Estado para item em edição
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var initialImagePath by remember { mutableStateOf<String?>(null) }

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
                    },
                    onAddClick = {
                        editingItem = null
                        initialImagePath = null
                        navController.navigate(Screen.Add.route)
                    }
                )
            }

            // === TELA 2: ADICIONAR/EDITAR ITEM ===
            composable(Screen.Add.route) {
                AddEditItemScreen(
                    viewModel = viewModel,
                    editingItem = editingItem,
                    initialImagePath = initialImagePath,
                    onNavigateBack = {
                        editingItem = null
                        initialImagePath = null
                        navController.popBackStack()
                    },
                    onSuccess = {
                        editingItem = null
                        initialImagePath = null
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // === TELA 5: CATEGORIAS ===
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onCategoryClick = { category ->
                        // Navegar para home e filtrar por categoria
                        // TODO: Implementar filtro de categoria
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
                        editingItem = item
                        initialImagePath = item.imagePath
                        navController.navigate(Screen.Add.route)
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

    // === PERMISSION DIALOG ===
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = onDismissPermissionDialog,
            title = {
                Text(
                    text = "Permissão de Câmera",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Para tirar fotos dos seus itens, o app precisa acessar a câmera do seu aparelho. " +
                            "Você pode conceder a permissão agora ou usar fotos da galeria.",
                    color = White70,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onRequestPermissionAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissPermissionDialog) {
                    Text("Agora Não", color = White70)
                }
            },
            containerColor = Surface
        )
    }
}
