package com.example.ondeguardei.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ondeguardei.data.Category
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel
import java.io.File
import java.io.FileOutputStream

/**
 * TELA 2: ADICIONAR/EDITAR ITEM
 *
 * Features:
 * - Opção entre CameraX (custom) ou Câmera Nativa (Intent)
 * - Preview da imagem capturada
 * - Formulário estilizado com TextFields outlined
 * - Dropdown de categorias com ícones coloridos
 * - Validação de campos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    viewModel: ItemViewModel,
    editingItem: Item? = null,
    initialImagePath: String? = null,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())

    // Form state
    var imagePath by remember { mutableStateOf(initialImagePath ?: editingItem?.imagePath) }
    var name by remember { mutableStateOf(editingItem?.name ?: "") }
    var location by remember { mutableStateOf(editingItem?.location ?: "") }
    var description by remember { mutableStateOf(editingItem?.description ?: "") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showCameraOptions by remember { mutableStateOf(imagePath == null) }
    var nameError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    // Carregar categoria do item em edição
    LaunchedEffect(editingItem, categories) {
        if (editingItem != null && categories.isNotEmpty()) {
            selectedCategory = categories.find { it.id == editingItem.categoriaId }
        } else if (categories.isNotEmpty() && selectedCategory == null) {
            selectedCategory = categories.last() // Default: "Outros"
        }
    }

    // Launcher para câmera nativa (Samsung/Google Camera)
    val tempPhotoFile = remember {
        File.createTempFile("temp_photo_", ".jpg", context.cacheDir)
    }
    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempPhotoFile
        )
    }

    val nativeCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Copiar foto para storage interno
            val finalFile = File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg")
            tempPhotoFile.copyTo(finalFile, overwrite = true)
            imagePath = finalFile.absolutePath
            showCameraOptions = false
        }
    }

    // Launcher para galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Copiar imagem da galeria para storage interno
            val inputStream = context.contentResolver.openInputStream(it)
            val finalFile = File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                FileOutputStream(finalFile).use { output ->
                    input.copyTo(output)
                }
            }
            imagePath = finalFile.absolutePath
            showCameraOptions = false
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
            // === HEADER ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(top = 32.dp)
                    .semantics { contentDescription = "Cabeçalho da tela de adicionar item" },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.semantics {
                        contentDescription = "Voltar para tela anterior"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editingItem != null) "Editar Item" else "Adicionar Item",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // === CONTENT ===
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // === FOTO ===
                if (showCameraOptions) {
                    // Opções de câmera
                    Text(
                        text = "Adicionar Foto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Opção 1: Câmera Nativa (Recomendado)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                nativeCameraLauncher.launch(photoUri)
                            }
                            .semantics { contentDescription = "Usar câmera nativa do aparelho" },
                        shape = RoundedCornerShape(16.dp),
                        color = Primary.copy(alpha = 0.2f),
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Recomendado",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Câmera Nativa (Recomendado)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Usa a câmera do seu aparelho (Samsung, Google)",
                                    fontSize = 12.sp,
                                    color = White70
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Opção 2: Galeria
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "Escolher foto da galeria" },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escolher da Galeria")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    // Preview da imagem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceVariant)
                    ) {
                        imagePath?.let {
                            AsyncImage(
                                model = File(it),
                                contentDescription = "Preview da foto do item",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Botão para trocar foto
                        IconButton(
                            onClick = { showCameraOptions = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Black80, CircleShape)
                                .semantics { contentDescription = "Trocar foto do item" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Trocar foto",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // === FORMULÁRIO ===
                Text(
                    text = "Informações do Item",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Nome
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Nome do item") },
                    placeholder = { Text("Ex: Carregador do notebook") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics { contentDescription = "Campo de nome do item" },
                    singleLine = true,
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Campo obrigatório", color = Error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = White50,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = White70
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Localização
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        locationError = false
                    },
                    label = { Text("Onde está guardado") },
                    placeholder = { Text("Ex: Gaveta do escritório") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Ícone de localização",
                            tint = Secondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics { contentDescription = "Campo de localização do item" },
                    singleLine = true,
                    isError = locationError,
                    supportingText = if (locationError) {
                        { Text("Campo obrigatório", color = Error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = White50,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = White70
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categoria
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.semantics {
                        contentDescription = "Seletor de categoria do item"
                    }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.nome ?: "Selecione uma categoria",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        leadingIcon = {
                            selectedCategory?.let { category ->
                                Icon(
                                    imageVector = getCategoryIcon(category.icone),
                                    contentDescription = "Ícone da categoria ${category.nome}",
                                    tint = category.getColor()
                                )
                            }
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = White50,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = White70
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Surface)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.nome, color = Color.White) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryIcon(category.icone),
                                        contentDescription = "Categoria ${category.nome}",
                                        tint = category.getColor()
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "Selecionar categoria ${category.nome}"
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (opcional)") },
                    placeholder = { Text("Adicione detalhes sobre o item...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .semantics { contentDescription = "Campo de descrição opcional do item" },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = White50,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = White70
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // === BOTÕES ===
                Button(
                    onClick = {
                        // Validação
                        var hasError = false
                        if (name.isBlank()) {
                            nameError = true
                            hasError = true
                        }
                        if (location.isBlank()) {
                            locationError = true
                            hasError = true
                        }

                        if (!hasError) {
                            if (editingItem != null) {
                                // Atualizar
                                viewModel.update(
                                    editingItem.copy(
                                        name = name,
                                        location = location,
                                        description = description,
                                        imagePath = imagePath,
                                        categoriaId = selectedCategory?.id ?: 7
                                    )
                                )
                            } else {
                                // Criar novo
                                viewModel.insert(
                                    Item(
                                        name = name,
                                        location = location,
                                        description = description,
                                        imagePath = imagePath,
                                        categoriaId = selectedCategory?.id ?: 7
                                    )
                                )
                            }
                            onSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .semantics {
                            contentDescription = if (editingItem != null)
                                "Salvar alterações do item"
                            else
                                "Adicionar novo item"
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    ),
                    enabled = !showCameraOptions // Desabilitar se não tem foto
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (editingItem != null) "Salvar Alterações" else "Adicionar Item",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Cancelar e voltar" }
                ) {
                    Text("Cancelar", color = White70)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
