package com.example.ondeguardei.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    itemName: String = "",
    itemLocation: String = "",
    itemDescription: String = "",
    itemImagePath: String? = null,
    onNavigateBack: () -> Unit,
    onSave: (String, String, String, String?) -> Unit,
    onCameraClick: () -> Unit
) {
    var name by remember { mutableStateOf(itemName) }
    var location by remember { mutableStateOf(itemLocation) }
    var description by remember { mutableStateOf(itemDescription) }
    var imagePath by remember { mutableStateOf(itemImagePath) }
    var showImageOptionsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copy image to app's private storage
            try {
                val fileName = "IMG_${System.currentTimeMillis()}.jpg"
                val destFile = File(context.filesDir, fileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                imagePath = destFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemName.isEmpty()) "Adicionar Item" else "Editar Item") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { showImageOptionsDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (imagePath != null && File(imagePath).exists()) {
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = "Foto do item",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toque para adicionar foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do item") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Location field
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Onde está guardado") },
                placeholder = { Text("Ex: Gaveta da cozinha, Armário do quarto...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                placeholder = { Text("Adicione detalhes sobre o item...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (name.isNotBlank() && location.isNotBlank()) {
                        onSave(name, location, description, imagePath)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && location.isNotBlank()
            ) {
                Text("Salvar", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }

    // Image options dialog
    if (showImageOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showImageOptionsDialog = false },
            title = { Text("Adicionar foto") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showImageOptionsDialog = false
                            onCameraClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tirar foto")
                    }
                    TextButton(
                        onClick = {
                            showImageOptionsDialog = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escolher da galeria")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageOptionsDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
