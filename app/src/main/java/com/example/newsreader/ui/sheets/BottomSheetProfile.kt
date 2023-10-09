package com.example.newsreader.ui.sheets

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.newsreader.R
import com.example.newsreader.helper.retrofit.viewModels.DataViewModel
import com.example.newsreader.helper.utils.HelperUtils.TAG
import com.example.newsreader.helper.utils.HelperUtils.showToast
import com.example.newsreader.helper.utils.SharedPrefUtils

private var dismissCallback: (() -> Unit)? = null
private var dataViewModel: DataViewModel? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetProfile(viewModel: DataViewModel, onDismiss: () -> Unit) {
    dismissCallback = onDismiss
    dataViewModel = viewModel

    val context = LocalContext.current
    val sharedPrefUtils = SharedPrefUtils(context)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
    ) {
        when (sharedPrefUtils.token == null) {
            true -> Registration(sharedPrefUtils)
            false -> Profile(sharedPrefUtils)
        }
    }
}

@Composable
fun Registration(sharedPrefUtils: SharedPrefUtils) {
    val resources = LocalContext.current.resources
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TabRow for the two tabs "Login" and "Register"
        TabRow(
            selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()
        ) {
            Tab(text = { Text(resources.getString(R.string.login)) }, selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 })
            Tab(text = { Text(resources.getString(R.string.register)) }, selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 })
        }

        // Content of the selected tab
        when (selectedTabIndex) {
            0 -> LoginTabContent(sharedPrefUtils)
            1 -> RegisterTabContent()
        }
    }
}

@Composable
fun LoginTabContent(sharedPrefUtils: SharedPrefUtils) {
    val context = LocalContext.current

    var usernameState by remember { mutableStateOf(TextFieldValue()) }
    var passwordState by remember { mutableStateOf(TextFieldValue()) }

    var isButtonEnabled by remember { mutableStateOf(true) }
    var isProgressEnabled by remember { mutableStateOf(false) }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var isPasswordValid by remember { mutableStateOf(true) }

    fun isUsernameValid(username: String): Boolean {
        val regex = "^[a-zA-Z0-9_]{3,20}$"
        return username.matches(Regex(regex))
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.username)) },
                onValueChange = {
                    usernameState = it
                    usernameError = if (isUsernameValid(it.text)) null else context.getString(R.string.username_validation)
                },
                singleLine = true,
                value = usernameState
            )

            usernameError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(2.dp))
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.password)) },
                onValueChange = {
                    passwordState = it
                    // Check password validity when it changes
                    isPasswordValid = isPasswordValid(it.text)
                },
                singleLine = true,
                value = passwordState,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility) painterResource(id = R.drawable.ic_visible_on)
                    else painterResource(id = R.drawable.ic_visible_off)
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = image,
                            contentDescription = null
                        )
                    }
                },
                isError = !isPasswordValid,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            )

            if (!isPasswordValid) {
                Text(
                    text = context.getString(R.string.password_validation),
                    color = Color.Red
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
        item {
            Row {
                Button(enabled = isButtonEnabled, onClick = {
                    if (isUsernameValid(usernameState.text.trim()) && isPasswordValid(passwordState.text.trim())) {
                        isButtonEnabled = false
                        isProgressEnabled = true
                        dataViewModel?.login(sharedPrefUtils, usernameState.text.trim(), passwordState.text.trim())
                    } else {
                        if (usernameState.text.trim().isEmpty() && passwordState.text.trim().isEmpty())
                            showToast(context, context.getString(R.string.empty_fields))
                    }
                }) {
                    Text(text = context.getString(R.string.login))
                }
                Spacer(modifier = Modifier.padding(8.dp))
                AnimatedVisibility(visible = isProgressEnabled) {
                    CircularProgressIndicator()
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }

    // Observer
    dataViewModel?.loginLiveData?.observe(LocalLifecycleOwner.current) {
        Log.d(TAG, "LoginTabContent: reached: $it")
        if (it == null) return@observe
        isButtonEnabled = true
        isProgressEnabled = false
        when (it) {
            false -> {
                showToast(context, context.resources.getString(R.string.login_failed))
                return@observe
            }

            true -> {
                dataViewModel?.showBottomBar?.value = true
                showToast(context, context.resources.getString(R.string.login_successfully))
                dismissCallback?.invoke()
            }
        }
        dataViewModel?.resetLogin()
    }
}

@Composable
fun RegisterTabContent() {
    val context = LocalContext.current

    var usernameState by remember { mutableStateOf(TextFieldValue()) }
    var passwordState by remember { mutableStateOf(TextFieldValue()) }

    var isButtonEnabled by remember { mutableStateOf(true) }
    var isProgressEnabled by remember { mutableStateOf(false) }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var isPasswordValid by remember { mutableStateOf(true) }

    fun isUsernameValid(username: String): Boolean {
        val regex = "^[a-zA-Z0-9_]{3,20}$"
        return username.matches(Regex(regex))
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.username)) },
                onValueChange = {
                    usernameState = it
                    usernameError = if (isUsernameValid(it.text)) null else context.getString(R.string.username_validation)
                },
                singleLine = true,
                value = usernameState
            )

            usernameError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(2.dp))
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.password)) },
                onValueChange = {
                    passwordState = it
                    // Check password validity when it changes
                    isPasswordValid = isPasswordValid(it.text)
                },
                singleLine = true,
                value = passwordState,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisibility) painterResource(id = R.drawable.ic_visible_on)
                    else painterResource(id = R.drawable.ic_visible_off)
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = image,
                            contentDescription = null
                        )
                    }
                },
                isError = !isPasswordValid,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            )

            if (!isPasswordValid) {
                Text(
                    text = context.getString(R.string.password_validation),
                    color = Color.Red
                )
            }
        }
        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
        item {
            Row {
                Button(enabled = isButtonEnabled, onClick = {
                    if (isUsernameValid(usernameState.text.trim()) && isPasswordValid(passwordState.text.trim())) {
                        isButtonEnabled = false
                        isProgressEnabled = true
                        dataViewModel?.register(usernameState.text.trim(), passwordState.text.trim())
                    } else {
                        if (usernameState.text.trim().isEmpty() && passwordState.text.trim().isEmpty())
                            showToast(context, context.getString(R.string.empty_fields))
                    }
                }) {
                    Text(text = context.getString(R.string.register))
                }
                Spacer(modifier = Modifier.padding(8.dp))
                AnimatedVisibility(visible = isProgressEnabled) {
                    CircularProgressIndicator()
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }

    // Observer
    dataViewModel?.registerLiveData?.observe(LocalLifecycleOwner.current) {
        Log.d(TAG, "RegisterTabContent: reached")
        if (it == null) return@observe
        isButtonEnabled = true
        isProgressEnabled = false
        when (it.Success) {
            true -> {
                showToast(context, context.getString(R.string.registered_successfully))
                dismissCallback?.invoke()
            }

            false -> {
                showToast(context, it.Message)
            }
        }
        dataViewModel?.resetRegister()
    }
}

@Composable
fun Profile(sharedPrefUtils: SharedPrefUtils) {
    val resources = LocalContext.current.resources
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hey ${sharedPrefUtils.userName}!",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                dataViewModel?.showBottomBar?.value = false
                sharedPrefUtils.userName = null
                sharedPrefUtils.token = null
                dismissCallback?.invoke()
            },
        ) {
            Text(text = resources.getString(R.string.logout))
        }
    }
}