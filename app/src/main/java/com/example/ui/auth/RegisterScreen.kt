package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.SmartRestaurantApp
import com.example.model.Restaurant
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// High-contrast, Executive Restaurant Management Palette
private val ForestGreenDeep = Color(0xFF143828)
private val ForestGreenMedium = Color(0xFF1E523A)
private val ForestGreenLight = Color(0xFFE8F1EC)
private val WarmGold = Color(0xFFC88A2D)
private val WarmGoldLight = Color(0xFFFBF4E8)
private val CardBackground = Color(0xFFFFFFFF)
private val ScreenBackground = Color(0xFFF6F8F6)
private val CharcoalText = Color(0xFF1C1B1F)
private val BodyText = Color(0xFF333333)
private val SecondaryText = Color(0xFF555555)
private val FieldBorderColor = Color(0xFFB0BEC5)
private val ErrorRed = Color(0xFFBA1A1A)
private val ErrorRedLight = Color(0xFFFFEDEC)
private val SuccessGreen = Color(0xFF2E7D32)
private val SuccessGreenLight = Color(0xFFE8F5E9)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val container = remember(context) { (context.applicationContext as SmartRestaurantApp).container }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var isDemoDataInserting by remember { mutableStateOf(false) }
    var demoDataError by remember { mutableStateOf<String?>(null) }
    var registeredSuccessRestaurant by remember { mutableStateOf<Restaurant?>(null) }

    // Listen for register event
    LaunchedEffect(viewModel) {
        viewModel.authEvents.collect { event ->
            if (event is AuthEvent.RegisterSuccess) {
                registeredSuccessRestaurant = event.restaurant
            }
        }
    }

    if (registerState.isRegistrationSuccess && registeredSuccessRestaurant == null) {
        registeredSuccessRestaurant = registerState.registeredRestaurant
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
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Branding Section (FOOD WISE Logo unchanged)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, WarmGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_food_wise_logo),
                        contentDescription = "Food Wise Logo",
                        modifier = Modifier.size(68.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "FOOD WISE",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreenDeep,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Restaurant Onboarding & Multi-Tenant Setup",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                }

                // =========================================================================
                // REGISTRATION SUCCESS OVERLAY CARD (With Load Demo Data & Go to Dashboard)
                // =========================================================================
                if (registerState.isRegistrationSuccess || registeredSuccessRestaurant != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Success",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Text(
                                text = "Restaurant Registered Successfully",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDeep,
                                textAlign = TextAlign.Center
                            )

                            val restName = registeredSuccessRestaurant?.restaurantName
                                ?: registerState.restaurantName.ifBlank { "Your Restaurant" }
                            val manager = registeredSuccessRestaurant?.ownerName
                                ?: registerState.ownerName.ifBlank { "Manager" }

                            Text(
                                text = "Welcome $manager! Restaurant profile \"$restName\" has been permanently created with isolated multi-tenant data storage.",
                                fontSize = 14.sp,
                                color = BodyText,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            HorizontalDivider(color = ForestGreenLight, modifier = Modifier.padding(vertical = 4.dp))

                            if (demoDataError != null) {
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
                                        Text(demoDataError ?: "", color = ErrorRed, fontSize = 13.sp)
                                    }
                                }
                            }

                            if (isDemoDataInserting) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(WarmGoldLight, RoundedCornerShape(14.dp))
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = WarmGold,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("register_loading_indicator")
                                    )
                                    Text(
                                        text = "Loading sample menu items, inventory, expiry dates, and sales dataset for \"$restName\"...",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ForestGreenDeep,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                // 1. Load Demo Data Button (Explicitly targets newly registered restaurant)
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isDemoDataInserting = true
                                            demoDataError = null
                                            try {
                                                val targetRestId = registeredSuccessRestaurant?.id
                                                    ?: container.authRepository.getCurrentRestaurantId()
                                                    ?: container.authRepository.getActiveRestaurantId().firstOrNull()
                                                    ?: 0L

                                                if (targetRestId > 0L) {
                                                    // CRITICAL: updateProfile = false preserves restaurant name and owner name
                                                    val res = container.demoDataRepository.loadDemoData(
                                                        restaurantId = targetRestId,
                                                        updateProfile = false,
                                                        profileId = "royal_spice"
                                                    )
                                                    if (res.isSuccess) {
                                                        onNavigateToHome()
                                                    } else {
                                                        demoDataError = res.exceptionOrNull()?.message ?: "Failed to insert demo data."
                                                    }
                                                } else {
                                                    demoDataError = "Active restaurant session not found. Please try entering dashboard."
                                                }
                                            } catch (e: Exception) {
                                                demoDataError = e.message ?: "An error occurred while loading demo data."
                                            } finally {
                                                isDemoDataInserting = false
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("register_load_demo_data_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White)
                                        Text("Load Demo Data", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                // 2. Direct Continue to Dashboard Button
                                Button(
                                    onClick = onNavigateToHome,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("register_continue_dashboard_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenDeep),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("Go to Operations Dashboard", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }

                                // 3. Continue to Login Button
                                OutlinedButton(
                                    onClick = onNavigateToLogin,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("register_continue_login_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenDeep)
                                ) {
                                    Text("Continue to Login", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                } else {
                    // =========================================================================
                    // REGISTRATION FORM CARD (High Contrast, M3 Professionalism)
                    // =========================================================================
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
                                text = "Register Your Property",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDeep
                            )
                            Text(
                                text = "Provide business information to configure your dedicated inventory and billing environment.",
                                fontSize = 13.sp,
                                color = SecondaryText
                            )

                            if (!registerState.generalError.isNullOrBlank()) {
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
                                        Text(registerState.generalError ?: "", color = ErrorRed, fontSize = 13.sp)
                                    }
                                }
                            }

                            // Restaurant Name
                            OutlinedTextField(
                                value = registerState.restaurantName,
                                onValueChange = { viewModel.onRegisterRestaurantNameChange(it) },
                                label = { Text("Restaurant Name *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("e.g. Royal Spice Bistro") },
                                leadingIcon = { Icon(Icons.Filled.Restaurant, contentDescription = null, tint = ForestGreenMedium) },
                                isError = registerState.restaurantNameError != null,
                                supportingText = registerState.restaurantNameError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_restaurant_name_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Owner / Manager Name
                            OutlinedTextField(
                                value = registerState.ownerName,
                                onValueChange = { viewModel.onRegisterOwnerNameChange(it) },
                                label = { Text("Owner / General Manager Name *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("e.g. Chef Anand") },
                                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = ForestGreenMedium) },
                                isError = registerState.ownerNameError != null,
                                supportingText = registerState.ownerNameError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_owner_name_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Email Address
                            OutlinedTextField(
                                value = registerState.email,
                                onValueChange = { viewModel.onRegisterEmailChange(it) },
                                label = { Text("Corporate Email Address *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("manager@restaurant.com") },
                                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = ForestGreenMedium) },
                                isError = registerState.emailError != null,
                                supportingText = registerState.emailError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Phone
                            OutlinedTextField(
                                value = registerState.phone,
                                onValueChange = { viewModel.onRegisterPhoneChange(it) },
                                label = { Text("Contact Phone Number *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("+91 9876543210") },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = ForestGreenMedium) },
                                isError = registerState.phoneError != null,
                                supportingText = registerState.phoneError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_phone_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Address
                            OutlinedTextField(
                                value = registerState.address,
                                onValueChange = { viewModel.onRegisterAddressChange(it) },
                                label = { Text("Property Address *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("MG Road, Central District, Bengaluru") },
                                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ForestGreenMedium) },
                                isError = registerState.addressError != null,
                                supportingText = registerState.addressError?.let { { Text(it, color = ErrorRed) } },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_address_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Password
                            OutlinedTextField(
                                value = registerState.password,
                                onValueChange = { viewModel.onRegisterPasswordChange(it) },
                                label = { Text("Password (min. 6 characters) *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("Create secure password") },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = ForestGreenMedium) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.toggleRegisterPasswordVisibility() },
                                        modifier = Modifier.testTag("register_password_visibility_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (registerState.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (registerState.isPasswordVisible) "Hide password" else "Show password",
                                            tint = SecondaryText
                                        )
                                    }
                                },
                                visualTransformation = if (registerState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = registerState.passwordError != null,
                                supportingText = registerState.passwordError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // Confirm Password
                            OutlinedTextField(
                                value = registerState.confirmPassword,
                                onValueChange = { viewModel.onRegisterConfirmPasswordChange(it) },
                                label = { Text("Confirm Password *", fontWeight = FontWeight.Medium) },
                                placeholder = { Text("Re-enter password") },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = ForestGreenMedium) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.toggleRegisterConfirmPasswordVisibility() },
                                        modifier = Modifier.testTag("register_confirm_password_visibility_toggle")
                                    ) {
                                        Icon(
                                            imageVector = if (registerState.isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (registerState.isConfirmPasswordVisible) "Hide password" else "Show password",
                                            tint = SecondaryText
                                        )
                                    }
                                },
                                visualTransformation = if (registerState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = registerState.confirmPasswordError != null,
                                supportingText = registerState.confirmPasswordError?.let { { Text(it, color = ErrorRed) } },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_confirm_password_input"),
                                colors = authTextFieldColors(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    viewModel.register()
                                })
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.register()
                                },
                                enabled = !registerState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("register_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenDeep),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (registerState.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Text("Create Restaurant Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Link back to Login
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have a registered account?",
                            fontSize = 14.sp,
                            color = BodyText
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.testTag("register_back_to_login_button")
                        ) {
                            Text(
                                text = "Log In",
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
}

@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = CharcoalText,
    unfocusedTextColor = CharcoalText,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = ForestGreenDeep,
    focusedBorderColor = ForestGreenDeep,
    unfocusedBorderColor = FieldBorderColor,
    focusedLabelColor = ForestGreenDeep,
    unfocusedLabelColor = SecondaryText
)
