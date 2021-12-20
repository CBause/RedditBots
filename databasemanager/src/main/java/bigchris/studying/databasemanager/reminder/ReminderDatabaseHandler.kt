package bigchris.studying.databasemanager.reminder

interface ReminderDatabaseHandler {

    suspend fun getAllReminderCalls() : List<ReminderCall>

    suspend fun saveReminderCall(reminderCall: ReminderCall)

    suspend fun saveReminderCall(vararg reminderCall: ReminderCall)

    suspend fun deleteReminderCall(reminderCall: ReminderCall)

}