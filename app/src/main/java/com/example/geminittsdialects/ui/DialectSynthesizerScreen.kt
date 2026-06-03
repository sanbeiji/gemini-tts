package com.example.geminittsdialects.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.geminittsdialects.models.DialectData
import com.example.geminittsdialects.models.UserSettings
import com.example.geminittsdialects.viewmodel.DialectUiState
import com.example.geminittsdialects.viewmodel.DialectViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DialectSynthesizerScreen(
    viewModel: DialectViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState(initial = UserSettings())
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedDialect by viewModel.selectedDialect.collectAsState()
    val isMale by viewModel.isMale.collectAsState()
    val textToSynthesize by viewModel.textToSynthesize.collectAsState()

    var showKeyDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // If API key is empty, prompt user immediately
    LaunchedEffect(userSettings.apiKey) {
        if (userSettings.apiKey.isBlank()) {
            showKeyDialog = true
        }
    }

    if (showKeyDialog) {
        var tempKey by remember { mutableStateOf(userSettings.apiKey) }
        val uriHandler = LocalUriHandler.current

        AlertDialog(
            onDismissRequest = { 
                if (userSettings.apiKey.isNotBlank()) {
                    showKeyDialog = false 
                }
            },
            title = { Text("Gemini API Key Required") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This demo requires a Gemini API Key to access the gemini-3.1-flash-tts-preview model.")
                    Text(
                        text = "Get your Gemini API key from AI Studio",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://aistudio.google.com/app/apikey")
                        }
                    )
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        label = { Text("Gemini API Key") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSettings(userSettings.copy(apiKey = tempKey))
                        showKeyDialog = false
                    },
                    enabled = tempKey.isNotBlank()
                ) {
                    Text("Save Key")
                }
            },
            dismissButton = {
                if (userSettings.apiKey.isNotBlank()) {
                    TextButton(onClick = { showKeyDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("About Gemini Dialects") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("This app showcases the advanced regional dialect synthesis of Google's gemini-3.1-flash-tts-preview model.")
                    Text("By injecting precise dialectal pronunciation rules, phonetic maps, and cultural cadence guidelines into the system instructions, Gemini can read text in very specific accents.")
                    Text("Supported features:")
                    Text("• Multiple languages (English, Spanish, German, French, Sinitic/Mandarin/Cantonese)\n• Over 20 custom regional dialects\n• Segmented male/female voice engine switching (Kore/Puck)\n• Seamless local audio caching to reduce network requests.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Gemini Dialects",
                            fontWeight = FontWeight.Bold,
                            fontFamily = com.example.geminittsdialects.theme.IansuiFontFamily
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🗣️", fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "App info")
                    }
                    IconButton(onClick = { showKeyDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Text to Synthesize OutlinedTextField
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Text to Synthesize",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textToSynthesize,
                        onValueChange = { viewModel.onTextChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        placeholder = { Text("Enter text to speak...") },
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Language Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Select Language",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val languages = DialectData.languages
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        languages.forEachIndexed { index, language ->
                            val isSelected = selectedLanguage.id == language.id
                            val displayName = if (language.id == "zh") "Chinese" else language.name
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = languages.size),
                                onClick = { viewModel.onLanguageSelected(language) },
                                selected = isSelected,
                                icon = { SegmentedButtonDefaults.Icon(active = isSelected) }
                            ) {
                                Text(
                                    text = displayName,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Voice Selector (Male/Female)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Voice Gender",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val genders = listOf(
                        false to "Female (Kore)",
                        true to "Male (Puck)"
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        genders.forEachIndexed { index, (value, label) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = genders.size),
                                onClick = { viewModel.onGenderSelected(value) },
                                selected = isMale == value,
                                icon = { SegmentedButtonDefaults.Icon(active = isMale == value) }
                            ) {
                                Text(label)
                            }
                        }
                    }
                }
            }

            // Dialect Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Select Regional Dialect",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        selectedLanguage.dialects.forEach { dialect ->
                            val isSelected = selectedDialect.id == dialect.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onDialectSelected(dialect) },
                                label = { Text(dialect.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }
            }

            // Playback controls / States
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (uiState) {
                        is DialectUiState.Idle -> {
                            Button(
                                onClick = { viewModel.synthesizeAndPlay() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Synthesize & Speak",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        is DialectUiState.Loading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "Synthesizing dialect with Gemini...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        is DialectUiState.Playing -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "Playing dialect voice...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    SpeechWaves()
                                }
                                Button(
                                    onClick = { viewModel.stopPlayback() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Stop Playback", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is DialectUiState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = (uiState as DialectUiState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.resetError() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    ) {
                                        Text("Dismiss")
                                    }
                                    Button(
                                        onClick = { viewModel.synthesizeAndPlay() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Cache clean section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.clearCache() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear cache", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Local Voice Cache", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SpeechWaves(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "speech_waves")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 5) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 400 + i * 150, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_$i"
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(24.dp * scale)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
