package bigchris.studying.databasemanager

import android.content.Context
import androidx.room.Room
import bigchris.studying.databasemanager.reminder.DefaultReminderDatabaseHandler
import bigchris.studying.databasemanager.reminder.ReminderDatabaseHandler

object DatabaseFactory {
    private lateinit var database: Database

    fun createDatabase(context: Context, username: String) {
        if (!this::database.isInitialized) {
            database =
                Room.databaseBuilder(context, Database::class.java, "${username}_reminderDatabase").build()
        }
    }

    fun getDatabaseHandler() : ReminderDatabaseHandler =
        DefaultReminderDatabaseHandler(database.reminderCallDao())
}