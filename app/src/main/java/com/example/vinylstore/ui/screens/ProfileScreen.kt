package com.example.vinylstore.ui.screens

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.vinylstore.data.remote.dto.MusicRecommendation
import com.example.vinylstore.viewmodel.AuthViewModel
import com.example.vinylstore.viewmodel.MusicViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun getProfileImageFile(context: Context, userId: Long): File {
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val profileDir = File(storageDir, "profiles")
    if (!profileDir.exists()) {
        profileDir.mkdirs()
    }
    return File(profileDir, "profile_$userId.jpg")
}

suspend fun saveImageToPersistentStorage(context: Context, userId: Long, sourceUri: Uri): String? {
    return try {
        val destinationFile = getProfileImageFile(context, userId)
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        inputStream?.use { input ->
            FileOutputStream(destinationFile).use { output ->
                input.copyTo(output)
                output.flush()
            }
        }
        if (destinationFile.exists() && destinationFile.length() > 0) {
            destinationFile.absolutePath
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    userId: Long,
    isAdmin: Boolean,
    authViewModel: AuthViewModel,
    musicViewModel: MusicViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToAdminProducts: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var isProcessingImage by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    //cargar imagen de perfil desde Room
    val profileImageUri by authViewModel.getProfileImageUri(userId).collectAsState(initial = null)
    
    LaunchedEffect(userId, profileImageUri) {
        if (userId > 0) {
            profileImageUri?.let { filePath ->
                try {
                    val file = File(filePath)
                    if (file.exists() && file.length() > 0) {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        selectedImageUri = uri
                    } else {
                        selectedImageUri = null
                    }
                } catch (e: Exception) {
                    selectedImageUri = null
                }
            } ?: run {
                selectedImageUri = null
            }
        } else {
            selectedImageUri = null
        }
    }
    
    //estados del MusicViewModel
    val recommendations by musicViewModel.recommendations.collectAsState()
    val isLoadingMusic by musicViewModel.isLoading.collectAsState()
    val musicError by musicViewModel.error.collectAsState()
    
    //cargar recomendaciones al montar el componente de forma segura
    LaunchedEffect(Unit) {
        try {
            musicViewModel.loadTopTracks()
        } catch (e: Exception) {
            //manejar error silenciosamente para no bloquear la UI
        }
    }
    
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
    )
    
    //animación de pulso para el indicador de carga
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageFile != null) {
            val sourceUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile!!
            )
            coroutineScope.launch {
                val savedPath = saveImageToPersistentStorage(context, userId, sourceUri)
                savedPath?.let { filePath ->
                    authViewModel.saveProfileImage(userId, filePath)
                    val profileFile = File(filePath)
                    if (profileFile.exists()) {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            profileFile
                        )
                        selectedImageUri = uri
                    }
                }
            }
            isProcessingImage = false
        } else {
            isProcessingImage = false
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { sourceUri ->
            selectedImageUri = sourceUri
            coroutineScope.launch {
                val savedPath = saveImageToPersistentStorage(context, userId, sourceUri)
                savedPath?.let { filePath ->
                    authViewModel.saveProfileImage(userId, filePath)
                    val profileFile = File(filePath)
                    if (profileFile.exists()) {
                        val finalUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            profileFile
                        )
                        selectedImageUri = finalUri
                    }
                }
            }
        }
    }
    
    fun launchCamera() {
        if (permissionsState.allPermissionsGranted) {
            isProcessingImage = true
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val tempFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            imageFile = tempFile
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            cameraLauncher.launch(uri)
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isProcessingImage) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.scale(scale)
                        )
                    }
                } else if (selectedImageUri != null) {
                    val profilePainter = rememberAsyncImagePainter(
                        model = selectedImageUri
                    )
                    Image(
                        painter = profilePainter,
                        contentDescription = "Imagen de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f),
                    enabled = !isProcessingImage
                ) {
                    if (isProcessingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Camera, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isProcessingImage) "Procesando..." else "Cámara")
                }
                
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Photo, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galería", maxLines = 1)
                    }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Nombre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = userName.uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = onNavigateToOrderHistory
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Historial de Compras",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(Icons.Default.ShoppingBag, contentDescription = null)
                }
            }
            
            if (isAdmin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = onNavigateToAdminDashboard
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dashboard de Administración",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(Icons.Default.Dashboard, contentDescription = null)
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = onNavigateToAdminProducts
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Gestión de Productos",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            }
            
            //sección de recomendaciones musicales
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Recomendaciones para ti",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { musicViewModel.loadTopTracks() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                        }
                    }
                    
                    if (isLoadingMusic) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (musicError != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = "Error: ${musicError}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    } else if (recommendations.isEmpty()) {
                        Text(
                            text = "No hay recomendaciones disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recommendations.forEach { recommendation ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            val painter = rememberAsyncImagePainter(
                                                model = recommendation.imageUrl
                                            )
                                            Image(
                                                painter = painter,
                                                contentDescription = recommendation.trackName,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            if (painter.state is coil.compose.AsyncImagePainter.State.Loading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.Center),
                                                    strokeWidth = 2.dp
                                                )
                                            }
                                            if (painter.state is coil.compose.AsyncImagePainter.State.Error) {
                                                Icon(
                                                    Icons.Filled.MusicNote,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.Center)
                                                )
                                            }
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = recommendation.trackName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = recommendation.artistName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}
