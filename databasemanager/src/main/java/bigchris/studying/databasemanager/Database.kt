package bigchris.studying.databasemanager

import androidx.room.Database
import androidx.room.RoomDatabase
import bigchris.studying.databasemanager.reminder.ReminderCallEntity
import bigchris.studying.databasemanager.reminder.ReminderCallDao

@Database(entities = [ReminderCallEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun reminderCallDao(): ReminderCallDao
}