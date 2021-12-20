package bigchris.studying.redditbots.accountmanagement.composables

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bigchris.studying.redditbots.BaseViewModel
import bigchris.studying.redditbots.R
import bigchris.studying.redditbots.accountmanagement.AccountManagementViewModel
import bigchris.studying.redditbots.accountmanagement.AccountManagementListener

@Composable
fun AccountManagementComposable(listener: AccountManagementListener, internalAccountType: String, title: String, baseViewModel: BaseViewModel) {
    val TAG = "ACCOUNTMANAGEMENTCOMPOSABLE"
    val viewModel: AccountManagementViewModel = viewModel()
    viewModel.listener = listener
    viewModel.internalAccountType = internalAccountType
    viewModel.createAccountManagerWithContext((LocalContext.current as ComponentActivity))
    var accounts = viewModel.getAccounts()
    val username = remember {mutableStateOf("")}
    val password = remember {mutableStateOf("")}
    val appId = remember {mutableStateOf("")}
    val appSecret = remember {mutableStateOf("")}
    val addAccountSuccessText = stringResource(R.string.success_add_account)
    val addAccountErrorText = stringResource(R.string.error_add_account)
    val removeAccountSuccessText = stringResource(R.string.success_remove_account)
    val removeAccountErrorText = stringResource(R.string.error_remove_account)
    val loginSuccessText = stringResource(R.string.success_login)
    val loginErrorText = stringResource(R.string.error_login)
    val emptyFieldError = stringResource(R.string.error_empty_field)

    viewModel.addAccountSuccessLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetAddAccountMessages()
            accounts = viewModel.getAccounts()
            baseViewModel.submitSnackBarText(addAccountSuccessText)
        }
    }

    viewModel.addAccountErrorLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetAddAccountMessages()
            baseViewModel.submitSnackBarText(addAccountErrorText)
        }
    }

    viewModel.removeAccountSuccessLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetRemoveAccountMessages()
            accounts = viewModel.getAccounts()
            baseViewModel.submitSnackBarText(removeAccountSuccessText)
        }
    }

    viewModel.removeAccountErrorLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetRemoveAccountMessages()
            baseViewModel.submitSnackBarText(removeAccountErrorText)
        }
    }

    viewModel.loginSuccessLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetLoginMessages()
            baseViewModel.submitSnackBarText(loginSuccessText)
        }
    }

    viewModel.loginErrorLiveData.observe((LocalContext.current as ComponentActivity)) {
        if (it) {
            viewModel.resetLoginMessages()
            baseViewModel.submitSnackBarText(loginErrorText)
        }
    }



    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(0.7f)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    val text = stringResource(R.string.title_accountManagement).plus(": ").plus(title)
                    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(text = stringResource(R.string.add_account_title))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = stringResource(R.string.add_account_username))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    TextField(value = username.value, onValueChange = {
                        username.value = it
                    })
                }

                Row(modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = stringResource(R.string.add_account_password))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    TextField(value = password.value, onValueChange = {
                        password.value = it
                    })
                }

                Row(modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = stringResource(R.string.add_account_app_identifier))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    TextField(value = appId.value, onValueChange = {
                        appId.value = it
                    })
                }

                Row(modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = stringResource(R.string.add_account_app_secret))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    TextField(value = appSecret.value, onValueChange = {
                        appSecret.value = it
                    })
                }


                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        if (password.value.isNotEmpty()
                                && username.value.isNotEmpty()
                                && appSecret.value.isNotEmpty()
                                && appId.value.isNotEmpty()) {
                            viewModel.onAddAccount(
                                username.value,
                                password.value,
                                internalAccountType
                            )
                            baseViewModel.saveAppIdAndSecret(username.value, appId.value, appSecret.value)
                            username.value = ""
                            appSecret.value = ""
                            appId.value = ""
                            password.value = ""
                        } else {
                            baseViewModel.submitSnackBarText(emptyFieldError)
                        }
                    }) {
                        Text(text = stringResource(R.string.add_account_submit))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.select_account_title))
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth(1f)) {
                    accounts?.forEach {
                        item {
                            Row(modifier = Modifier.height(40.dp)
                                                    .fillMaxWidth(1f)
                                                    .clickable(onClick = {
                                                        viewModel.onLogin(it, internalAccountType)
                                                    }),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically)
                            {
                                Text(text = it)
                                IconButton(onClick = {
                                    viewModel.onRemoveAccount(it, internalAccountType)
                                    username.value = " "
                                    username.value = ""
                                    password.value = ""
                                    appId.value = ""
                                    appSecret.value = ""
                                }) {
                                    Icon(Icons.Filled.Delete, null)
                                }
                            }
                        }
                    } ?: item {
                        Row(
                            modifier = Modifier.fillMaxWidth(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = stringResource(R.string.select_account_no_accounts_message))
                        }
                    }
                }
            }
        }
    }


}