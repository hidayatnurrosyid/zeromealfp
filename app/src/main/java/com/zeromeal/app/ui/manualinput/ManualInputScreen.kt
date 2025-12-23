package com.zeromeal.app.ui.manualinput

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.zeromeal.app.ui.theme.ZeroMealGreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

// Data untuk dropdown
val categories = listOf(
    "Buah", "Sayuran", "Daging", "Ikan", "Susu & Produk Olahan",
    "Roti & Kue", "Makanan Kaleng", "Bumbu & Rempah", "Minuman", "Lainnya"
)

val units = listOf("pcs", "kg", "gram", "liter", "ml", "ons", "bungkus")

val storageLocations = listOf(
    "Kulkas", "Freezer", "Lemari Dapur", "Rak Dapur", "Lainnya"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(
    navController: NavController,
    itemId: String? = null,
    viewModel: ManualInputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showUnitDropdown by remember { mutableStateOf(false) }
    var showStorageDropdown by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    
    // Image Picker
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            viewModel.updateImageUri(it.toString())
        }
    }
    
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(itemId) {
        if (!itemId.isNullOrBlank()) {
            viewModel.loadItem(itemId)
        } else {
            viewModel.resetForm()
        }
    }
    
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            // Error handling bisa ditambahkan snackbar di sini
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Input Manual",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Upload Field
            Column {
                Text(
                    text = "Gambar Produk",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(imageUri)
                                    .build()
                            ),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Add Image",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Klik untuk menambahkan gambar",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Nama Produk
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nama Produk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Masukkan nama makanan atau bahan makanan") },
                    shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                    singleLine = true
                )
            }
            
            // Kategori
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kategori",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
                ) {
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = {},
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        placeholder = { Text("Pilih kategori makanan") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Tanggal Pembelian
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tanggal Pembelian",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val currentDate = uiState.purchaseDate ?: LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    viewModel.updatePurchaseDate(selectedDate)
                                },
                                currentDate.year,
                                currentDate.monthValue - 1,
                                currentDate.dayOfMonth
                            ).show()
                        }
                ) {
                    OutlinedTextField(
                        value = uiState.purchaseDate?.format(dateFormatter) ?: "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = false,
                        placeholder = { Text("mm/dd/yyyy") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date Picker"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // Tanggal Kedaluwarsa
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tanggal Kedaluwarsa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val baseDate = uiState.expirationDate
                                ?: uiState.purchaseDate
                                ?: LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                    viewModel.updateExpirationDate(selectedDate)
                                },
                                baseDate.year,
                                baseDate.monthValue - 1,
                                baseDate.dayOfMonth
                            ).show()
                        }
                ) {
                    OutlinedTextField(
                        value = uiState.expirationDate?.format(dateFormatter) ?: "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = false,
                        placeholder = { Text("mm/dd/yyyy") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date Picker"
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
            
            // Jumlah/Berat
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Jumlah/Berat",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.quantity,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) {
                                viewModel.updateQuantity(it)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = showUnitDropdown,
                        onExpandedChange = { showUnitDropdown = !showUnitDropdown },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = uiState.unit,
                            onValueChange = {},
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitDropdown)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showUnitDropdown,
                            onDismissRequest = { showUnitDropdown = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        viewModel.updateUnit(unit)
                                        showUnitDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Lokasi Penyimpanan
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Lokasi Penyimpanan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "*",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = showStorageDropdown,
                    onExpandedChange = { showStorageDropdown = !showStorageDropdown }
                ) {
                    OutlinedTextField(
                        value = uiState.storageLocation,
                        onValueChange = {},
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        placeholder = { Text("Pilih lokasi penyimpanan") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStorageDropdown)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = showStorageDropdown,
                        onDismissRequest = { showStorageDropdown = false }
                    ) {
                        storageLocations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    viewModel.updateStorageLocation(location)
                                    showStorageDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Catatan Tambahan
            Column {
                Text(
                    text = "Catatan Tambahan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Tambahkan catatan khusus (opsional)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    maxLines = 4
                )
            }
            
            // Error Message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = "Batal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
                
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = viewModel.saveFoodItem()
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZeroMealGreen
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Simpan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

