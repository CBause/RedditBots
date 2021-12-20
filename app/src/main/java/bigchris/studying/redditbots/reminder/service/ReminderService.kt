package bigchris.studying.redditbots.reminder.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.core.app.NotificationCompat
import bigchris.studying.databasemanager.DatabaseFactory
import bigchris.studying.databasemanager.reminder.ReminderCall
import bigchris.studying.databasemanager.reminder.ReminderDatabaseHandler
import bigchris.studying.redditbots.utils.Constants
import bigchris.studying.restcaller.*
import bigchris.studying.restcaller.request.RestCallRequest
import bigchris.studying.restcaller.response.RestCallResponse
import kotlinx.coroutines.*

class ReminderService : Service(), RestCallListener {
    private val TAG = "REMINDERSERVICE"
    private val notificationMessage = "ReminderService still working"
    private val reminderServiceNotificationChannelId = "ReminderServiceNotificationChannel"
    private val binder = LocalBinder()
    private var password = ""
    private var username = ""
    private var appId = ""
    private var appSecret = ""
    private lateinit var restCaller: RestCaller
    private lateinit var remindingJob: Job
    private val reminderDatabaseHandler:
            ReminderDatabaseHandler by lazy {DatabaseFactory.getDatabaseHandler()}

    companion object {
        val ACTION_START_FOREGROUND_SERVICE = "startForegroundService"
        val ACTION_STOP_FOREGROUND_SERVICE = "stopForegroundService"
    }

    inner class LocalBinder() : Binder() {

    }

    override fun onIsAuthorized() {
        startReminding()
    }

    override fun onGetNewMessages(messagesResponse: RestCallResponse.NewMessagesRestCallResponse) {
        val messageData = addWhenToRemindField(messagesResponse.getReminderMessageData())
        val messageList = mutableListOf<ReminderCall>()
        var markAsReadIdString = ""
        messageData.forEach {
            if (it[RestCallResponse.KEY_KIND]!! == RestCallResponse.TYPE_KIND
                && it[RestCallResponse.KEY_TYPE]!! == RestCallResponse.TYPE_VALUE) {
                val reminderCallObject = getReminderCallObject(it)
                markAsReadIdString = markAsReadIdString
                    .plus(it[RestCallResponse.KEY_NAME]!!).plus(",")
                if (it[Constants.WHEN_TO_REMIND_KEY]!!.toString() != "0") {
                    messageList.add(reminderCallObject)
                } else {
                    sendFalseUsageComment(reminderCallObject)
                }
            }
        }
        if (markAsReadIdString.isNotEmpty())
            markReadMessages(markAsReadIdString)
        writeReminderCallsIntoDatabase(messageList)
        messagesResponse.nextMessagesAfter()?.let {
            getNewMessages(it)
        }
    }


    override fun onBind(intent: Intent?): IBinder {
        intent?.let {
            username = it.getStringExtra(Constants.USERNAME_KEY) ?: ""
            password = it.getStringExtra(Constants.PASSWORD_KEY) ?: ""
            appId = it.getStringExtra(Constants.APP_ID_KEY) ?: ""
            appSecret = it.getStringExtra(Constants.APP_SECRET_KEY) ?: ""
            restCaller = RestCallerFactory.getRestCaller(applicationContext,
                    username,
                    password,
                    appId,
                    appSecret,
                    RestCaller.ScopeType.Reminder)
            restCaller.registerListener(this)
        }
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == ACTION_START_FOREGROUND_SERVICE) {
                startForegroundService()
                return Service.START_STICKY
            }
            stopForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, reminderServiceNotificationChannelId)
                .setContentText(notificationMessage)
        startForeground(1, builder.build())
    }

    private fun stopForegroundService() {
        stopForeground(true)
        if (this::restCaller.isInitialized)
            restCaller.stopAuthTokenCoroutine()
        if (this::remindingJob.isInitialized)
            remindingJob.cancel()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O) {
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            val channel = NotificationChannel(reminderServiceNotificationChannelId,
                    TAG,
                    NotificationManager.IMPORTANCE_MIN)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startReminding() : Job
        = CoroutineScope(Dispatchers.IO).launch {
        while(isActive) {
            getNewMessages()
            updateDatabase()
            delay(Constants.REMINDING_JOB_DELAY_IN_MS)
        }
    }

    private fun getNewMessages(after: String? = null) {
        if (this::restCaller.isInitialized) {
            val restCallRequest = RestCallRequest.create(RestCallRequest.API_ENDPOINT_GET_NEW_MESSAGES)
            if (after != null)
                restCallRequest.addToBody(RestCallResponse.KEY_AFTER, after)
            restCallRequest.addToBody(RestCallRequest.KEY_MARK, "true")
            restCaller.triggerJsonGetRequest(restCallRequest)
        }
    }

    private fun addWhenToRemindField(messageData: List<Map<String,String>>) :
            List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()
        try {
            messageData.forEach {
                val created = it[RestCallResponse.KEY_CREATED_UTC]!!.toLong()
                val body = it[RestCallResponse.KEY_BODY]!!
                var desiredTime: Long = 0
                getDaysAndHours(body)?.let {daysAndHoursPair ->
                        desiredTime = calculateWhenToRemind(created, daysAndHoursPair)
                }
                val mutableMessageData = it.toMutableMap()
                mutableMessageData[Constants.WHEN_TO_REMIND_KEY] = desiredTime.toString()
                result.add(mutableMessageData)
            }
        } catch (error: Error) {
            Log.e(TAG, error.toString())
        }
        return result
    }

    private fun getDaysAndHours(timeMessage: String) : Pair<Int, Int>? {
        try {
            var hours = 0
            var days = 0
            var timeMsg = timeMessage.substringAfterLast(" ").lowercase()
                .replace(" ", "")

            if (timeMsg.contains("d") || timeMsg.contains("h")) {
                if (timeMsg.contains("d")) {
                        days = timeMsg.substringBeforeLast("d").toIntOrNull() ?: 0
                }
                if (timeMsg.contains("h")) {
                        hours = timeMsg.substringAfterLast("d")
                            .substringBeforeLast("h").toIntOrNull() ?: 0
                }
                return Pair(days, hours)
            }
        } catch(error: Error) {
            return Pair(0,0)
        }
        return Pair(0,0)
    }

    private fun calculateWhenToRemind(created: Long, daysAndHours: Pair<Int, Int>) : Long {
        val result = created + (daysAndHours.first * 3600 * 24) + (daysAndHours.second * 3600)
        if (result <= (System.currentTimeMillis()/1000))
            return 0
        return result
    }

    private fun logMessageData(messageDataMap: List<Map<String, String>>) {
        messageDataMap.forEach {
            it.forEach {
                Log.d(TAG, "Key: ${it.key}, value: ${it.value}")
            }
        }
    }

    private fun getReminderCallObject(messageData: Map<String, String>) : ReminderCall {
        return ReminderCall(
            0,
            messageData[RestCallResponse.KEY_AUTHOR_FULL]!!,
            messageData[RestCallResponse.KEY_NAME]!!,
            messageData[RestCallResponse.KEY_PARENT_ID]!!,
            messageData[RestCallResponse.KEY_SUBREDDIT]!!,
            messageData[RestCallResponse.KEY_CREATED_UTC]!!,
            messageData[Constants.WHEN_TO_REMIND_KEY]!!,
            messageData[RestCallResponse.KEY_AUTHOR]!!
        )
    }

    private fun markReadMessages(idString: String) {
        val request = RestCallRequest.create(RestCallRequest.API_ENDPOINT_MARK_MESSAGE_READ)
        request.addToBody(RestCallResponse.KEY_ID, idString)
        if (this::restCaller.isInitialized)
            restCaller.triggerStringPostRequest(request)
    }

    private fun writeReminderCallsIntoDatabase(reminderCallList: List<ReminderCall>) {
        CoroutineScope(Dispatchers.IO).launch {
            reminderCallList.forEach {
                reminderDatabaseHandler.saveReminderCall(it)
            }
        }
    }

    private fun updateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val calls = reminderDatabaseHandler.getAllReminderCalls()
            calls.forEach {
                var markForDeletion = false
                if (shouldRemind(it)) {
                    sendReminderComment(it)
                    markForDeletion = true
                } else if (isCallTooLate(it)) {
                    sendSorryForgotToRemindComment(it)
                    markForDeletion = true
                }
                if (markForDeletion) {
                    reminderDatabaseHandler.deleteReminderCall(it)
                }
            }
        }
    }

    private fun shouldRemind(reminderCall: ReminderCall) : Boolean {
        val diff = ((System.currentTimeMillis()/1000) - reminderCall.remindTime.toLong())
        if (diff < Constants.REMINDING_TOO_LATE_TRESHOLD && diff > 0)
            return true
        return false
    }

    private fun isCallTooLate(reminderCall: ReminderCall) : Boolean =
        ((System.currentTimeMillis()/1000) - reminderCall.remindTime.toLong()) >=
                Constants.REMINDING_TOO_LATE_TRESHOLD


    private fun sendComment(reminderCall: ReminderCall, text: String) {
        val req = RestCallRequest.create(RestCallRequest.API_ENDPOINT_COMMENT)
        req.addToBody(RestCallRequest.KEY_API_TYPE, RestCallRequest.VALUE_API_TYPE_JSON)
        req.addToBody(RestCallRequest.KEY_THING_ID, reminderCall.name)
        req.addToBody(RestCallRequest.KEY_TEXT, text)
        restCaller.triggerStringPostRequest(req)
    }

    private fun sendFalseUsageComment(reminderCall: ReminderCall) {
        val text = Constants.COMMENT_STUMP
            .plus("/u/${reminderCall.author}")
            .plus(Constants.COMMENT_FALSE_USAGE_1)
            .plus(username).plus(Constants.COMMENT_FALSE_USAGE_2)
        sendComment(reminderCall, text)
    }

    private fun sendSorryForgotToRemindComment(reminderCall: ReminderCall) {
        val timeText = getSorryCommentTimeText(reminderCall.remindTime.toLong())
        val text = Constants.COMMENT_STUMP
            .plus("/u/${reminderCall.author}")
            .plus(Constants.COMMENT_SORRY_FORGOT_1)
            .plus(timeText)
            .plus(Constants.COMMENT_SORRY_FORGOT_2)
        sendComment(reminderCall, text)
    }

    private fun sendReminderComment(reminderCall: ReminderCall) {
        val text = Constants.COMMENT_STUMP
            .plus("/u/${reminderCall.author}")
            .plus(Constants.COMMENT_REMIND)
        sendComment(reminderCall, text)
    }

    private fun getSorryCommentTimeText(whenToRemind: Long) : String {
        val diff = (System.currentTimeMillis()/1000 - whenToRemind)
        var diffTest = diff
        var hours = 0
        var days = 0
        while (diffTest > 0) {
            diffTest--
            hours++
        }
        if (hours > 24) {
            val reminder = hours.rem(24)
            days = (hours - reminder) / 24
            hours = reminder
        }
        if (days > 0) {
            return "$days ${Constants.COMMENT_DAYS} und $hours ${Constants.COMMENT_HOURS}"
        } else {
            return "$hours ${Constants.COMMENT_HOURS}"
        }
    }

}