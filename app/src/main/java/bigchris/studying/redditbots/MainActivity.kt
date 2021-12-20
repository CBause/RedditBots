package bigchris.studying.redditbots

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bigchris.studying.redditbots.reminder.composables.Reminder
import bigchris.studying.redditbots.ui.theme.RedditBotsTheme
import bigchris.studying.redditbots.utils.Constants
import bigchris.studying.redditbots.utils.NavigationListItem
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    private val TAG = "MAINACTIVITY"
    private val startingDestination: String = Constants.NAV_REMINDER
    private lateinit var baseViewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        baseViewModel.setPreferences(this.getPreferences(Context.MODE_PRIVATE))

        setContent {
            val currentDestination = remember {mutableStateOf(startingDestination)}
            val navigationController = rememberNavController()
            val scaffoldState = rememberScaffoldState()

            LaunchedEffect(true) {
                while (isActive) {
                    if (!baseViewModel.isSnackBarTextListEmpty()) {
                        scaffoldState.snackbarHostState.showSnackbar(
                                message = baseViewModel.getSnackBarString(),
                                duration = SnackbarDuration.Short
                        )
                    } else {
                        delay(Constants.SNACKBAR_DELAY)
                    }
                }
            }

            RedditBotsTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    bottomBar = {
                        BottomNavigation {
                            getNavigationList().forEach {
                                val selected = currentDestination.value == it.constantLink
                                BottomNavigationItem(
                                    selected = selected,
                                    icon = {
                                        Icon(it.icon, null)
                                    },
                                    label = {
                                        Text(text = it.label)
                                    },
                                    selectedContentColor = Color.White,
                                    unselectedContentColor = Color.Gray,
                                    onClick = {
                                        currentDestination.value = it.constantLink
                                        navigationController.navigate(it.constantLink) {
                                            restoreState = true
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Navigation(navigationController, baseViewModel)
                }
            }
        }
    }

    private fun setPauseState(value: Boolean) {
        baseViewModel.setOnPause(value)
    }


    override fun onPause() {
        super.onPause()
        setPauseState(true)
    }

    override fun onResume() {
        super.onResume()
        setPauseState(false)
    }

    @Composable
    fun Navigation(navigationController : NavHostController, baseViewModel: BaseViewModel) {
        NavHost(navController = navigationController, startDestination = startingDestination) {
            composable(Constants.NAV_REMINDER) { Reminder(baseViewModel) }
        }
    }

    private fun getNavigationList() : List<NavigationListItem> = listOf(
        NavigationListItem(Constants.NAV_REMINDER, resources.getString(R.string.nav_reminder), Icons.Default.Notifications)
    )


}