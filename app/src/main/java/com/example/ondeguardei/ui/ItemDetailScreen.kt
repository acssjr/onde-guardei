package com.example.ondeguardei.ui

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ondeguardei.data.Category
import com.example.ondeguardei.data.Item
import com.example.ondeguardei.ui.theme.*
import com.example.ondeguardei.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * TELA 3: DETALHES DO ITEM
 *
 * Layout:
 * - Imagem grande no topo (40% da tela) com ícones de editar/deletar
 * - Card com informações detalhadas
 * - Botões de ação na parte inferior
 */
@Composable
fun ItemDetailScreen(
    itemId: Int,
    viewModel: ItemViewModel,
    onEditClick: (Item) -> Unit,
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val item by viewModel.getItemById(itemId).collectAsState(initial = null)
    var category by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Carregar categoria
    LaunchedEffect(item) {
        item?.let {
            category = viewModel.getCategoryById(it.categoriaId)
        }
    }

    if (item == null) {
        // Loading state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // === HEADER IMAGE (40% da tela) ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                // Imagem de fundo
                if (item!!.imagePath != null) {
                    AsyncImage(
                        model = item!!.imagePath,
                        contentDescription = "Foto de ${item!!.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Primary, Primary.copy(alpha = 0.6f))
                                )
                            )
                    )
                }

                // Gradient overlay bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Background.copy(alpha = 0.7f),
                                    Background
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 32.dp)
                        .align(Alignment.TopStart)
                        .background(Black80, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }

                // Edit and Delete buttons
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 32.dp)
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onEditClick(item!!) },
                        modifier = Modifier.background(Black80, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.background(Black80, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = Error
                        )
                    }
                }
            }

            // === CONTENT ===
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // === INFO CARD ===
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Surface,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Nome
                        Text(
                            text = item!!.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Localização
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Localização",
                                tint = Secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item!!.location,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Categoria
                        if (category != null) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(category!!.getColor().copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(category!!.icone),
                                    contentDescription = category!!.nome,
                                    tint = category!!.getColor(),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = category!!.nome,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        // Descrição
                        if (item!!.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = White50.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Descrição",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = White70
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item!!.description,
                                fontSize = 16.sp,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = White50.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Data de adição
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Adicionado em:",
                                fontSize = 14.sp,
                                color = White70
                            )
                            Text(
                                text = formatDateFull(item!!.createdAt),
                                fontSize = 14.sp,
                                color = White50
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === BOTÕES DE AÇÃO ===
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botão Editar
                    OutlinedButton(
                        onClick = { onEditClick(item!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editar Informações",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Botão Compartilhar
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "📦 ${item!!.name}\n📍 Guardado em: ${item!!.location}\n\n" +
                                            if (item!!.description.isNotBlank())
                                                "💬 ${item!!.description}\n\n"
                                            else "" +
                                                    "Enviado do app Onde Guardei?"
                                )
                                putExtra(Intent.EXTRA_SUBJECT, "Localização de: ${item!!.name}")
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Compartilhar localização")
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compartilhar Localização",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // === DELETE DIALOG ===
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Deletar Item", color = Color.White)
            },
            text = {
                Text(
                    text = "Tem certeza que deseja deletar '${item!!.name}'? Esta ação não pode ser desfeita.",
                    color = White70
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.delete(item!!)
                        showDeleteDialog = false
                        onDeleteSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    )
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = White70)
                }
            },
            containerColor = Surface
        )
    }
}

/**
 * Formata data completa com hora
 */
private fun formatDateFull(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
