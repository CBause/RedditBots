package bigchris.studying.redditbots.reminder.composables

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import bigchris.studying.redditbots.BaseViewModel
import bigchris.studying.redditbots.R
import bigchris.studying.redditbots.accountmanagement.composables.AccountManagementComposable
import bigchris.studying.redditbots.reminder.ReminderViewModel
import bigchris.studying.redditbots.utils.Constants

@Composable
fun Reminder(baseViewModel: BaseViewModel) {
    val TAG = "REMINDERCOMPOSABLE"
    val viewModel: ReminderViewModel = viewModel()
    val loggedIn = viewModel.loginStateLiveData.observeAsState(false)
    val reminderServiceBound = viewModel.reminderServiceBoundLiveData.observeAsState(false)
    val reminderServiceStopped = viewModel.reminderServiceStoppedLiveData.observeAsState(false)
    val appPaused = baseViewModel.pausedLiveData.observeAsState(true)
    val reminderData = viewModel.reminderDataLiveData.observeAsState(mutableListOf<Pair<String,String>>())
    val successServiceBound = stringResource(R.string.success_service_bound)
    val errorServiceCouldNotStart = stringResource(R.string.error_service_could_not_start)
    val errorServiceBinding = stringResource(R.string.error_service_binding)
    val serviceStopped = stringResource(R.string.service_stopped)
    val context = LocalContext.current.applicationContext

    if (!loggedIn.value) {
        Box(modifier = Modifier.fillMaxSize(1f)) {
            AccountManagementComposable(
                    viewModel,
                    Constants.ACCOUNT_SUBTYPE_REMINDER,
                    stringResource(R.string.title_reminder),
                    baseViewModel
            )
        }
    } else {
        baseViewModel.getAppIdAndSecret(viewModel.username)?.let {
            viewModel.setAppIdAndSecret(it.first, it.second)
        }
        viewModel.startDataBase(LocalContext.current)
        if (!appPaused.value) {
            if (!reminderServiceStopped.value) {
                viewModel.startAndOrBindReminderService(context)
                if (viewModel.reminderServiceRunning) {
                    if (reminderServiceBound.value) {
                        baseViewModel.submitSnackBarText(successServiceBound)
                    } else if (viewModel.bindingInitialized) {
                        baseViewModel.submitSnackBarText(errorServiceBinding)
                    }
                } else {
                    baseViewModel.submitSnackBarText(errorServiceCouldNotStart)
                }
            } else {
                baseViewModel.submitSnackBarText(serviceStopped)
            }
        } else {
            viewModel.unbindService(context)
        }
        Box(
                modifier = Modifier.fillMaxSize(1f).padding(20.dp),
                contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.fillMaxWidth(0.7f).fillMaxHeight(1f)) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModel.startAndOrBindReminderService(context)
                            },
                            enabled = (!viewModel.reminderServiceRunning || !reminderServiceBound.value)
                        ) {
                            if (viewModel.reminderServiceRunning && !reminderServiceBound.value) {
                                Text(text = stringResource(R.string.bind_service))
                            } else {
                                Text(text = stringResource(R.string.start_service))
                            }
                        }
                        Button(onClick = {
                            viewModel.stopReminderService(context)
                        }, enabled = (viewModel.reminderServiceRunning)) {
                            Text(text = stringResource(R.string.stop_service))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(R.string.staged_to_be_reminded))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth(1f)) {
                        if (reminderData.value.isEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(1f),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = stringResource(R.string.database_empty))
                                }
                            }
                        } else {
                            reminderData.value.forEach {
                                item {
                                    Text(
                                        text = stringResource(R.string.username)
                                            .plus(" ").plus(it.first)
                                    )

                                    Text(
                                        text = stringResource(R.string.remind_time)
                                            .plus(" ").plus(it.second)
                                    )

                                    Divider(color = Color.Gray, thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
