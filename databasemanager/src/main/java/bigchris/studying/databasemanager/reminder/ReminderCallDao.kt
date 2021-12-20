package bigchris.studying.databasemanager.reminder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReminderCallDao {

    @Query("SELECT * FROM remindercallentity")
    fun getAll() : List<ReminderCallEntity>

    @Query("SELECT * FROM remindercallentity WHERE id = (:id) LIMIT 1")
    fun getEntity(id: Int) : ReminderCallEntity

    @Insert
    fun insert(reminderCallEntity: ReminderCallEntity)

    @Insert
    fun insert(vararg reminderCallEntities: ReminderCallEntity)

    @Delete
    fun delete(reminderCallEntity: ReminderCallEntity)

}