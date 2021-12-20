package bigchris.studying.redditbots.reminder

import android.app.ActivityManager
import android.app.Service
import android.content.*
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bigchris.studying.databasemanager.DatabaseFactory
import bigchris.studying.redditbots.accountmanagement.AccountManagementListener
import bigchris.studying.redditbots.reminder.service.ReminderService
import bigchris.studying.redditbots.utils.Constants
import bigchris.studying.restcaller.response.RestCallResponse
import kotlinx.coroutines.*

class ReminderViewModel : ViewModel(), AccountManagementListener {
    private val TAG = "REMINDERVIEWMODEL"
    var username = ""
        private set
    private var password = ""
    private var appId = ""
    private var appSecret = ""
    private val loginState: MutableLiveData<Boolean> = MutableLiveData(false)
    val loginStateLiveData: LiveData<Boolean> = loginState
    var reminderServiceRunning: Boolean = false
        private set
    var bindingInitialized: Boolean = false
        private set
    private val reminderServiceStopped: MutableLiveData<Boolean> = MutableLiveData(false)
    val reminderServiceStoppedLiveData: LiveData<Boolean> = reminderServiceStopped
    private val reminderServiceBound: MutableLiveData<Boolean> = MutableLiveData(false)
    val reminderServiceBoundLiveData: LiveData<Boolean> = reminderServiceBound
    private var reminderServiceConnection: ReminderServiceConnection? = null
    var serviceHandlingInitialized = false
        private set
    private val reminderData: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    val reminderDataLiveData: LiveData<List<Pair<String,String>>> = reminderData
    private lateinit var updateReminderDataJob: Job

    inner class ReminderServiceConnection() : ServiceConnection {

        private fun confirmBindingCallback() {
            bindingInitialized = true
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            confirmBindingCallback()
            reminderServiceBound.value = true
            updateReminderDataJob = getReminderDataUpdateJob()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
        }

        override fun onBindingDied(name: ComponentName?) {
            Log.d(TAG, "onBindingDied")
        }

        override fun onNullBinding(name: ComponentName?) {
            Log.d(TAG, "onNullBinding")
            confirmBindingCallback()
            reminderServiceBound.value = false
        }

    }

    override fun onAccountManagementDone(username: String, password: String) {
        this.username = username
        this.password = password
        loginState.value = true
    }

    fun setAppIdAndSecret(id: String, secret: String) {
        appId = id
        appSecret = secret
    }

    fun startAndOrBindReminderService(context: Context) {
        if (!serviceHandlingInitialized) {
            reminderServiceStopped.value = false
            serviceHandlingInitialized = true
            val reminderServiceIntent = getReminderServiceIntent(context)
            val isServiceRunning = isReminderServiceRunning(context)
            if ((!isServiceRunning && startReminderService(reminderServiceIntent, context))
                || isServiceRunning
            ) {
                reminderServiceRunning = true
                bindReminderService(reminderServiceIntent, context)
            } else if (!isServiceRunning) {
                reminderServiceRunning = false
            }
        }
    }

    private fun bindReminderService(intent: Intent, context: Context) {
        if (reminderServiceRunning) {
            reminderServiceConnection = ReminderServiceConnection()
            context.bindService(intent, reminderServiceConnection!!, 0)
        }
    }

    private fun startReminderService(intent: Intent, context: Context) : Boolean {
        intent.action = ReminderService.ACTION_START_FOREGROUND_SERVICE
        return context.startService(intent) != null
    }

    fun stopReminderService(context: Context) {
        if (this::updateReminderDataJob.isInitialized)
            updateReminderDataJob.cancel()
        reminderServiceConnection = null
        reminderServiceStopped.value = true
        bindingInitialized = false
        serviceHandlingInitialized = false
        val intent = Intent(context, ReminderService::class.java)
        if (isReminderServiceRunning(context)) {
            reminderServiceRunning = false
            intent.action = ReminderService.ACTION_STOP_FOREGROUND_SERVICE
            context.startService(intent)
        }
    }

    fun unbindService(context: Context) {
        if (this::updateReminderDataJob.isInitialized)
            updateReminderDataJob.cancel()
        if (reminderServiceConnection != null
            && reminderServiceBound.value!!
            && isReminderServiceRunning(context)) {
            context.unbindService(reminderServiceConnection!!)
            bindingInitialized = false
            serviceHandlingInitialized = false
            reminderServiceConnection = null
        }
    }

    private fun getReminderServiceIntent(context: Context) : Intent = Intent(context, ReminderService::class.java).apply {
        putExtra(Constants.USERNAME_KEY, username)
        putExtra(Constants.PASSWORD_KEY, password)
        putExtra(Constants.APP_ID_KEY, appId)
        putExtra(Constants.APP_SECRET_KEY, appSecret)
    }

    private fun isReminderServiceRunning(context: Context) : Boolean {
        val activityManager = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        activityManager.getRunningServices(Int.MAX_VALUE).forEach {
            val serviceName = ReminderService::class.java.toString().removePrefix("class ")
            if (it.service.className == serviceName) {
                reminderServiceRunning = true
                return true
            }
        }
        reminderServiceRunning = false
        return false
    }

    fun startDataBase(context: Context) {
        DatabaseFactory.createDatabase(context, username)

    }

    fun getReminderDataUpdateJob() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            val currentReminderData = DatabaseFactory.getDatabaseHandler().getAllReminderCalls()
            val reminderDataMap = mutableListOf<Pair<String,String>>()
            currentReminderData.forEach {
                reminderDataMap.add(Pair(it.author, getReadableRemindTimeFromNow(it.remindTime.toLong())))
            }
            reminderData.postValue(reminderDataMap)
            delay(Constants.REMINDER_DATABASE_CALL_DELAY.toLong())
        }
    }

    private fun getReadableRemindTimeFromNow(remindTime: Long) : String {
        var diff: Long = remindTime - (System.currentTimeMillis()/1000)
        var days: Long = 0
        var hours: Long = 0
        var minutes: Long = 0
        var temp: Long = diff.rem(3600 * 24)
        days = (diff - temp)/(3600 * 24)
        diff -= days * 3600 * 24
        temp = diff.rem(3600)
        hours = (diff - temp)/(3600)
        diff -= hours * 3600
        temp = diff.rem(60)
        minutes = (diff - temp)/60
        return "${days}d : ${hours}h : ${minutes}m"
    }

}