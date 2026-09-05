package com.docscanner.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.docscanner.app.R
import com.docscanner.app.presentation.common.ConfirmationDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentUser != null) {
                            stringResource(R.string.auth_profile_title)
                        } else {
                            if (selectedTab == 0) stringResource(R.string.auth_title_sign_in) else stringResource(R.string.auth_title_sign_up)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (currentUser != null) {
                // Profile view for authenticated user
                val user = currentUser!!
                val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(user.createdAt))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = user.displayName.ifBlank { "Scanly User" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Account Type",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Standard Cloud (10 GB)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Member Since",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.auth_btn_sign_out),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Sign In / Sign Up Tabs
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            viewModel.clearError()
                        },
                        text = { Text(stringResource(R.string.auth_btn_sign_in)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            viewModel.clearError()
                        },
                        text = { Text(stringResource(R.string.auth_btn_sign_up)) }
                    )
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text(stringResource(R.string.auth_name)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.auth_email)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.auth_password)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        if (selectedTab == 0) {
                            viewModel.signIn(email, password) { onNavigateBack() }
                        } else {
                            viewModel.signUp(email, password, displayName) { onNavigateBack() }
                        }
                    }),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (selectedTab == 0) {
                            viewModel.signIn(email, password) { onNavigateBack() }
                        } else {
                            viewModel.signUp(email, password, displayName) { onNavigateBack() }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (selectedTab == 0) stringResource(R.string.auth_btn_sign_in) else stringResource(R.string.auth_btn_sign_up),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.auth_btn_sign_out),
            message = stringResource(R.string.auth_sign_out_confirm),
            confirmLabel = stringResource(R.string.auth_btn_sign_out),
            dismissLabel = stringResource(R.string.cancel),
            onConfirm = {
                viewModel.signOut {
                    showSignOutDialog = false
                    onNavigateBack()
                }
            },
            onDismiss = { showSignOutDialog = false }
        )
    }
}
