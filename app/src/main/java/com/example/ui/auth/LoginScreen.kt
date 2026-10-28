package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R

// High-contrast, Executive Restaurant Management Palette
private val ForestGreenDeep = Color(0xFF143828)
private val ForestGreenMedium = Color(0xFF1E523A)
private val ForestGreenLight = Color(0xFFE8F1EC)
private val WarmGold = Color(0xFFC88A2D)
private val CardBackground = Color(0xFFFFFFFF)
private val ScreenBackground = Color(0xFFF6F8F6)
private val CharcoalText = Color(0xFF1C1B1F)
private val BodyText = Color(0xFF333333)
private val SecondaryText = Color(0xFF555555)
private val ErrorRed = Color(0xFFBA1A1A)
private val ErrorRedLight = Color(0xFFFFEDEC)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val demoState by viewModel.demoState.collectAsStateWithLifecycle()

    var isDemoLoadingLocal by remember { mutableStateOf(false) }

    // Collect successful authentication events
    LaunchedEffect(viewModel) {
        viewModel.authEvents.collect { event ->
            if (event is AuthEvent.LoginSuccess) {
                onNavigateToHome()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ScreenBackground
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // FOOD WISE Branding (Unchanged logo vector)
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, WarmGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_food_wise_logo),
                        contentDescription = "Food Wise Logo",
                        modifier = Modifier.size(74.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "FOOD WISE",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreenDeep,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Operations & Smart Inventory Management",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                }

                // Authentication Card (High Contrast M3 Surface)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Manager Sign In",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDeep
                        )
                        Text(
                            text = "Enter your credentials to manage your restaurant's billing, inventory, and analytics.",
                            fontSize = 13.sp,
                            color = SecondaryText
                        )

                        // General Error Banner
                        if (!loginState.generalError.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ErrorRedLight),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorRed)
                                    Text(loginState.generalError ?: "", color = ErrorRed, fontSize = 13.sp)
                                }
                            }
                        }

                        // Demo Error Banner
                        if (!demoState.errorMessage.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = ErrorRedLight),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorRed)
                                    Text(demoState.errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
                                }
                            }
                        }

                        // Email Field
                        OutlinedTextField(
                            value = loginState.email,
                            onValueChange = { viewModel.onLoginEmailChange(it) },
                            label = { Text("Manager Email Address", fontWeight = FontWeight.Medium) },
                            placeholder = { Text("manager@restaurant.com") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = ForestGreenMedium) },
                            isError = loginState.emailError != null,
                            supportingText = loginState.emailError?.let { { Text(it, color = ErrorRed) } },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            colors = authTextFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        // Password Field
                        OutlinedTextField(
                            value = loginState.password,
                            onValueChange = { viewModel.onLoginPasswordChange(it) },
                            label = { Text("Password", fontWeight = FontWeight.Medium) },
                            placeholder = { Text("Enter your password") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = ForestGreenMedium) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.toggleLoginPasswordVisibility() },
                                    modifier = Modifier.testTag("login_password_visibility_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (loginState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (loginState.isPasswordVisible) "Hide password" else "Show password",
                                        tint = SecondaryText
                                    )
                                }
                            },
                            visualTransformation = if (loginState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = loginState.passwordError != null,
                            supportingText = loginState.passwordError?.let { { Text(it, color = ErrorRed) } },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            colors = authTextFieldColors(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            })
                        )

                        // Remember Email Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onRememberEmailChange(!loginState.rememberEmail) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = loginState.rememberEmail,
                                onCheckedChange = { viewModel.onRememberEmailChange(it) },
                                colors = CheckboxDefaults.colors(checkedColor = ForestGreenDeep),
                                modifier = Modifier.testTag("login_remember_checkbox")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remember email on this device", fontSize = 13.sp, color = BodyText)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Login Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login()
                            },
                            enabled = !loginState.isLoading && !isDemoLoadingLocal && !demoState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenDeep),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (loginState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Sign In to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        HorizontalDivider(
                            color = ForestGreenLight,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Try Demo Restaurant Button
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                isDemoLoadingLocal = true
                                viewModel.tryDemoRestaurant("royal_spice") {
                                    isDemoLoadingLocal = false
                                    onNavigateToHome()
                                }
                            },
                            enabled = !loginState.isLoading && !isDemoLoadingLocal && !demoState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("try_demo_button")
                                .testTag("login_demo_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmGold)
                        ) {
                            if (isDemoLoadingLocal || demoState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = WarmGold, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Setting up Demo Session...", fontSize = 14.sp, color = ForestGreenDeep, fontWeight = FontWeight.Medium)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = WarmGold)
                                    Text("Explore Demo Restaurant", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ForestGreenDeep)
                                }
                            }
                        }
                    }
                }

                // Register Navigation Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New restaurant owner or manager?",
                        fontSize = 14.sp,
                        color = BodyText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.testTag("login_to_register_button")
                    ) {
                        Text(
                            text = "Register Restaurant",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenDeep
                        )
                    }
                }
            }
        }
    }
}
