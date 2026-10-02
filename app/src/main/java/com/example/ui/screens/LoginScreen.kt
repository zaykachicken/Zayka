package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed
import com.example.ui.theme.ZaykaRedDark

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun LoginScreen(
    viewModel: ZaykaViewModel,
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val authLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    var showGoogleEmailDialog by remember { mutableStateOf(false) }
    var googleEmailInput by remember { mutableStateOf("") }
    var googleNameInput by remember { mutableStateOf("") }

    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetNewPasswordInput by remember { mutableStateOf("") }
    var resetSuccessMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Decorative Top Gradient Arc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            com.example.ui.theme.PrimaryDark,
                            com.example.ui.theme.Primary,
                            Color(0xFFFB7185)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with optional Back and Guest actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(44.dp))
                }

                TextButton(
                    onClick = {
                        viewModel.continueAsGuest(onSuccess = onContinueAsGuest)
                    },
                    modifier = Modifier.testTag("skip_login_button")
                ) {
                    Text(
                        text = "Skip for now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Branding Emblem
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp)),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zayka_logo),
                    contentDescription = "Zayka Logo Emblem",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ZAYKA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Text(
                text = "Authentic Flavors • Delivered Fast",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Highlights Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                FeatureBadge(icon = Icons.Filled.ElectricBolt, text = "30m Delivery")
                FeatureBadge(icon = Icons.Filled.Star, text = "4.8★ Rated")
                FeatureBadge(icon = Icons.Filled.Security, text = "100% Safe")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authentication Container Card (Email Sign In / Sign Up / Password Reset & Google / OTP)
            Card(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                var selectedTab by remember { mutableStateOf(0) } // 0: Sign In, 1: Sign Up, 2: Google / OTP
                var emailInput by remember { mutableStateOf("") }
                var passwordInput by remember { mutableStateOf("") }
                var showPassword by remember { mutableStateOf(false) }

                var nameInput by remember { mutableStateOf("") }
                var signUpPhoneInput by remember { mutableStateOf("") }
                var confirmPasswordInput by remember { mutableStateOf("") }
                var showConfirmPassword by remember { mutableStateOf(false) }

                var phoneInput by remember { mutableStateOf("") }
                var otpInput by remember { mutableStateOf("") }
                var isOtpSent by remember { mutableStateOf(false) }
                var generatedOtp by remember { mutableStateOf("") }

                val inputColors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimaryLight,
                    unfocusedTextColor = TextPrimaryLight,
                    focusedLabelColor = com.example.ui.theme.Primary,
                    unfocusedLabelColor = TextMutedLight,
                    focusedBorderColor = com.example.ui.theme.Primary,
                    unfocusedBorderColor = BorderLight,
                    cursorColor = com.example.ui.theme.Primary,
                    focusedContainerColor = Color(0xFFF8F9FA),
                    unfocusedContainerColor = Color(0xFFF8F9FA)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Sign in to Zayka"
                            1 -> "Create Your Account"
                            else -> "Quick Verification"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Unlock live tracking, saved addresses, member-exclusive offers & fast reordering.",
                        fontSize = 13.sp,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Selector: [ Sign In | Sign Up | Google/OTP ]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F3F5))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val tabs = listOf("Sign In", "Sign Up", "Google / OTP")
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) com.example.ui.theme.Primary else Color.Transparent)
                                    .clickable {
                                        viewModel.clearAuthError()
                                        selectedTab = index
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondaryLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Error Alert Banner if auth operation failed
                    AnimatedVisibility(
                        visible = authError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("login_error_banner"),
                            shape = RoundedCornerShape(12.dp),
                            color = com.example.ui.theme.SecondaryLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.Secondary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "Error Info",
                                        tint = com.example.ui.theme.Secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = authError ?: "",
                                        fontSize = 12.sp,
                                        color = com.example.ui.theme.SecondaryDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearAuthError() },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("dismiss_error_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Dismiss error",
                                            tint = com.example.ui.theme.SecondaryDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (authError?.contains("Google", ignoreCase = true) == true) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.clearAuthError()
                                            showGoogleEmailDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Primary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Sign in with Google Email ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // --- TAB 0: EMAIL SIGN IN ---
                    if (selectedTab == 0) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. foodie@zayka.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_login_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            placeholder = { Text("Enter password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showPassword) "Hide password" else "Show password",
                                        tint = TextMutedLight
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_login_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    resetEmailInput = emailInput.ifBlank { resetEmailInput }
                                    resetSuccessMsg = null
                                    showPasswordResetDialog = true
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.Primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (emailInput.isBlank() || passwordInput.isBlank()) {
                                    viewModel.setAuthError("Please enter both email and password.")
                                    return@Button
                                }
                                viewModel.signInWithEmail(emailInput, passwordInput, onSuccess = onLoginSuccess)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_email_sign_in"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Primary),
                            enabled = !authLoading
                        ) {
                            if (authLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Signing In...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Text("Sign In", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Don't have an account?", fontSize = 13.sp, color = TextSecondaryLight)
                            TextButton(onClick = {
                                viewModel.clearAuthError()
                                selectedTab = 1
                            }) {
                                Text("Sign Up", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.Primary)
                            }
                        }
                    }

                    // --- TAB 1: EMAIL SIGN UP ---
                    if (selectedTab == 1) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Rahul Sharma") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. name@example.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signUpPhoneInput,
                            onValueChange = { signUpPhoneInput = it },
                            label = { Text("Mobile Number (Optional)") },
                            placeholder = { Text("e.g. 9876543210") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_phone_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password (min 4 chars)") },
                            placeholder = { Text("Create a password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextMutedLight
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = { confirmPasswordInput = it },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Re-enter password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                    Icon(
                                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextMutedLight
                                    )
                                }
                            },
                            visualTransformation = if (showConfirmPassword) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_confirm_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val cleanEmail = emailInput.trim()
                                val cleanPass = passwordInput.trim()
                                val cleanConfirm = confirmPasswordInput.trim()

                                if (nameInput.isBlank()) {
                                    viewModel.setAuthError("Please enter your full name.")
                                    return@Button
                                }
                                if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                    viewModel.setAuthError("Please enter a valid email address.")
                                    return@Button
                                }
                                if (cleanPass.length < 4) {
                                    viewModel.setAuthError("Password must be at least 4 characters long.")
                                    return@Button
                                }
                                if (cleanPass != cleanConfirm) {
                                    viewModel.setAuthError("Passwords do not match. Please verify.")
                                    return@Button
                                }
                                viewModel.signUpWithEmail(cleanEmail, cleanPass, nameInput, onSuccess = onLoginSuccess)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_email_sign_up"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Primary),
                            enabled = !authLoading
                        ) {
                            if (authLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Creating Account...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Text("Create Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Already have an account?", fontSize = 13.sp, color = TextSecondaryLight)
                            TextButton(onClick = {
                                viewModel.clearAuthError()
                                selectedTab = 0
                            }) {
                                Text("Sign In", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.Primary)
                            }
                        }
                    }

                    // --- TAB 2: GOOGLE & MOBILE PHONE OTP ---
                    if (selectedTab == 2) {
                        OutlinedButton(
                            onClick = {
                                val act = activity ?: (context as? Activity)
                                if (act != null) {
                                    viewModel.signInWithGoogle(
                                        activity = act,
                                        onSuccess = onLoginSuccess,
                                        onFailure = { errorMsg ->
                                            if (errorMsg.contains("No Google account", ignoreCase = true) ||
                                                errorMsg.contains("found on this device", ignoreCase = true) ||
                                                errorMsg.contains("cancelled", ignoreCase = true)
                                            ) {
                                                showGoogleEmailDialog = true
                                            } else {
                                                viewModel.setAuthError(errorMsg)
                                            }
                                        }
                                    )
                                } else {
                                    showGoogleEmailDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("google_auth_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                        ) {
                            GoogleLogoIcon()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3C4043)
                            )
                        }

                        TextButton(
                            onClick = { showGoogleEmailDialog = true },
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Or sign in with Google Email",
                                fontSize = 12.sp,
                                color = Color(0xFF1A73E8),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                            Text(
                                text = "  OR WITH PHONE OTP  ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMutedLight
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() || it == '+' || it == ' ' }) {
                                    phoneInput = input
                                }
                            },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("e.g. 9876543210") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = com.example.ui.theme.Primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+91", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_number_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    val cleaned = phoneInput.replace("+91", "").replace(" ", "").trim()
                                    if (cleaned.length < 10) {
                                        viewModel.setAuthError("Please enter a valid 10-digit mobile number.")
                                        return@Button
                                    }
                                    val code = (100000..999999).random().toString()
                                    generatedOtp = code
                                    isOtpSent = true
                                    otpInput = code
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_send_otp"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = com.example.ui.theme.Primary
                                ),
                                enabled = phoneInput.isNotBlank() && !authLoading
                            ) {
                                Text("Send Verification Code", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Code sent: $generatedOtp (Auto-filled)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = {
                                            val code = (100000..999999).random().toString()
                                            generatedOtp = code
                                            otpInput = code
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Resend", fontSize = 11.sp, color = com.example.ui.theme.Primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) otpInput = it },
                                label = { Text("6-Digit Verification Code") },
                                placeholder = { Text("Enter 6-digit code") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary, modifier = Modifier.size(18.dp))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_otp_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = inputColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (otpInput.length != 6) {
                                        viewModel.setAuthError("Please enter the 6-digit verification code.")
                                        return@Button
                                    }
                                    val formatted = if (phoneInput.startsWith("+91")) phoneInput else "+91 $phoneInput"
                                    viewModel.signInWithPhoneNumber(formatted, otpInput, onSuccess = onLoginSuccess)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("phone_auth_submit_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = com.example.ui.theme.Primary
                                ),
                                enabled = !authLoading
                            ) {
                                if (authLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Verifying...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Verify & Continue", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            TextButton(
                                onClick = {
                                    isOtpSent = false
                                    otpInput = ""
                                },
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text("Change Phone Number", fontSize = 12.sp, color = com.example.ui.theme.Primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue as Guest Button
                    OutlinedButton(
                        onClick = {
                            viewModel.continueAsGuest(onSuccess = onContinueAsGuest)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("continue_as_guest_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimaryLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderLight)
                    ) {
                        Text(
                            text = "Continue as Guest",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer / Legal notice
            Text(
                text = "By signing in, you agree to Zayka's\nTerms of Service and Privacy Policy",
                fontSize = 11.sp,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Google Email Direct Sign-In Dialog for devices/emulators without configured Play Services
        if (showGoogleEmailDialog) {
            AlertDialog(
                onDismissRequest = { showGoogleEmailDialog = false },
                icon = {
                    GoogleLogoIcon(modifier = Modifier.size(28.dp))
                },
                title = {
                    Text(
                        text = "Sign in with Google",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Enter your Google account email to sign in directly.",
                            fontSize = 13.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = googleEmailInput,
                            onValueChange = { googleEmailInput = it },
                            label = { Text("Google Email Address") },
                            placeholder = { Text("e.g. name@gmail.com") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dialog_google_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.example.ui.theme.Primary,
                                focusedLabelColor = com.example.ui.theme.Primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = googleNameInput,
                            onValueChange = { googleNameInput = it },
                            label = { Text("Your Name (Optional)") },
                            placeholder = { Text("e.g. Rahul Sharma") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dialog_google_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.example.ui.theme.Primary,
                                focusedLabelColor = com.example.ui.theme.Primary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanEmail = googleEmailInput.trim()
                            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                viewModel.setAuthError("Please enter a valid Google email.")
                                return@Button
                            }
                            showGoogleEmailDialog = false
                            viewModel.signInWithGoogleDirect(
                                email = cleanEmail,
                                displayName = if (googleNameInput.isNotBlank()) googleNameInput else null,
                                onSuccess = onLoginSuccess,
                                onFailure = { err -> viewModel.setAuthError(err) }
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Primary),
                        modifier = Modifier.testTag("dialog_btn_submit_google")
                    ) {
                        Text("Sign In", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showGoogleEmailDialog = false },
                        modifier = Modifier.testTag("dialog_btn_cancel_google")
                    ) {
                        Text("Cancel", color = TextSecondaryLight)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }

        // Password Reset / Forgot Password Dialog
        if (showPasswordResetDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordResetDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = com.example.ui.theme.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "Reset Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Enter your registered account email and new password to reset your credentials.",
                            fontSize = 13.sp,
                            color = TextSecondaryLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (resetSuccessMsg != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = resetSuccessMsg ?: "",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = resetEmailInput,
                            onValueChange = { resetEmailInput = it },
                            label = { Text("Account Email") },
                            placeholder = { Text("e.g. name@example.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.example.ui.theme.Primary,
                                focusedLabelColor = com.example.ui.theme.Primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = resetNewPasswordInput,
                            onValueChange = { resetNewPasswordInput = it },
                            label = { Text("New Password (min 4 chars)") },
                            placeholder = { Text("Enter new password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_new_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = com.example.ui.theme.Primary,
                                focusedLabelColor = com.example.ui.theme.Primary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanEmail = resetEmailInput.trim()
                            val cleanNewPass = resetNewPasswordInput.trim()

                            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                viewModel.setAuthError("Please enter a valid email address.")
                                return@Button
                            }
                            if (cleanNewPass.length < 4) {
                                viewModel.setAuthError("New password must be at least 4 characters long.")
                                return@Button
                            }

                            viewModel.resetUserPassword(
                                email = cleanEmail,
                                newPass = cleanNewPass,
                                onSuccess = {
                                    resetSuccessMsg = "Password updated! You can now sign in with your new password."
                                },
                                onFailure = { err ->
                                    viewModel.setAuthError(err)
                                }
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Primary),
                        modifier = Modifier.testTag("btn_confirm_password_reset")
                    ) {
                        Text("Reset Password", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPasswordResetDialog = false },
                        modifier = Modifier.testTag("btn_cancel_password_reset")
                    ) {
                        Text("Close", color = TextSecondaryLight)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 3.2.dp.toPx()
        val center = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f)

        // Red top arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 100f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Yellow left arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 20f,
            sweepAngle = 100f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Blue right arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 280f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Blue crossbar
        drawLine(
            color = Color(0xFF4285F4),
            start = androidx.compose.ui.geometry.Offset(center.x - 1f, center.y),
            end = androidx.compose.ui.geometry.Offset(w - stroke / 2f, center.y),
            strokeWidth = stroke
        )
    }
}

@Composable
private fun FeatureBadge(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
