package com.example.ondeguardei

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.*
import com.example.ondeguardei.ui.theme.OndeGuardeiTheme
import com.example.ondeguardei.viewmodel.ItemViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled in composables
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            OndeGuardeiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OndeGuardeiApp()
                }
            }
        }
    }
}

@Composable
fun OndeGuardeiApp() {
    val navController = rememberNavController()
    val viewModel: ItemViewModel = viewModel()

    val items by viewModel.allItems.collectAsState(initial = emptyList())
    var capturedImagePath by remember { mutableStateOf<String?>(null) }
    var editingItem by remember { mutableStateOf<Item?>(null) }

    NavHost(navController = navController, startDestination = "item_list") {
        // Item list screen
        composable("item_list") {
            ItemListScreen(
                items = items,
                onItemClick = { item ->
                    navController.navigate("item_detail/${item.id}")
                },
                onAddClick = {
                    capturedImagePath = null
                    editingItem = null
                    navController.navigate("add_edit_item")
                },
                onDeleteItem = { item ->
                    viewModel.delete(item)
                }
            )
        }

        // Add/Edit item screen
        composable("add_edit_item") {
            val item = editingItem
            AddEditItemScreen(
                itemName = item?.name ?: "",
                itemLocation = item?.location ?: "",
                itemDescription = item?.description ?: "",
                itemImagePath = capturedImagePath ?: item?.imagePath,
                onNavigateBack = {
                    capturedImagePath = null
                    editingItem = null
                    navController.popBackStack()
                },
                onSave = { name, location, description, imagePath ->
                    if (item != null) {
                        // Update existing item
                        viewModel.update(
                            item.copy(
                                name = name,
                                location = location,
                                description = description,
                                imagePath = imagePath
                            )
                        )
                    } else {
                        // Create new item
                        viewModel.insert(
                            Item(
                                name = name,
                                location = location,
                                description = description,
                                imagePath = imagePath
                            )
                        )
                    }
                    capturedImagePath = null
                    editingItem = null
                    navController.popBackStack()
                },
                onCameraClick = {
                    navController.navigate("camera")
                }
            )
        }

        // Item detail screen
        composable(
            route = "item_detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable
            val item by viewModel.getItemById(itemId).collectAsState(initial = null)

            item?.let { currentItem ->
                ItemDetailScreen(
                    item = currentItem,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onEdit = {
                        editingItem = currentItem
                        capturedImagePath = null
                        navController.navigate("add_edit_item")
                    },
                    onDelete = {
                        viewModel.delete(currentItem)
                        navController.popBackStack()
                    }
                )
            }
        }

        // Camera screen
        composable("camera") {
            CameraScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onImageCaptured = { path ->
                    capturedImagePath = path
                    navController.popBackStack()
                }
            )
        }
    }
}
