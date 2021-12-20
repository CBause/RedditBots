package bigchris.studying.databasemanager.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReminderCallEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "subreddit") val subreddit: String?,
    @ColumnInfo(name = "author_fullname") val author_fullname: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "parent_id") val parent_id: String?,
    @ColumnInfo(name = "created_utc") val created_utc: String?,
    @ColumnInfo(name = "when_to_remind") val when_to_remind: String?,
    @ColumnInfo(name = "author") val author: String?)