package bigchris.studying.databasemanager.reminder

internal class DefaultReminderDatabaseHandler(private val reminderCallDao: ReminderCallDao) : ReminderDatabaseHandler {

    override suspend fun getAllReminderCalls(): List<ReminderCall> {
        val result = mutableListOf<ReminderCall>()
            reminderCallDao.getAll().forEach {
                result.add(
                    ReminderCall(
                        it.id,
                        it.author_fullname!!,
                        it.name!!,
                        it.parent_id!!,
                        it.subreddit!!,
                        it.created_utc!!,
                        it.when_to_remind!!,
                        it.author!!
                    )
                )
            }
        return result
    }

    override suspend fun saveReminderCall(reminderCall: ReminderCall) {
        val result = ReminderCallEntity(
            0,
            reminderCall.subreddit,
            reminderCall.author,
            reminderCall.name,
            reminderCall.parentId,
            reminderCall.creationTime,
            reminderCall.remindTime,
            reminderCall.author
        )
        reminderCallDao.insert(result)
    }

    override suspend fun saveReminderCall(vararg reminderCall: ReminderCall) {
        for (element in reminderCall) {
            saveReminderCall(element)
        }
    }

    override suspend fun deleteReminderCall(reminderCall: ReminderCall) {
        reminderCallDao.delete(reminderCallDao.getEntity(reminderCall.id))
    }
}