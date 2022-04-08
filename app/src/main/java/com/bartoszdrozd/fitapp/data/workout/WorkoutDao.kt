package com.bartoszdrozd.fitapp.data.workout

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutWithExercises
import com.bartoszdrozd.fitapp.model.workout.WorkoutType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Dao
abstract class WorkoutDao {
    @Query("SELECT * FROM workouts")
    abstract fun getAll(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    abstract fun get(id: Long): Flow<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    abstract fun getOnce(id: Long): WorkoutWithExercises?

    @Query("SELECT id from workouts WHERE server_id = :serverId")
    abstract fun getIdByServerId(serverId: Long): Long?

    @Query("SELECT * FROM workouts WHERE server_id = :id")
    abstract fun getByServerId(id: Long): WorkoutEntity

    @Query("SELECT server_id FROM workouts where id = :id")
    abstract suspend fun getRealId(id: Long): Long?

    @Query("SELECT * from exercises where id IN (:ids)")
    abstract suspend fun getExercises(ids: List<Long>): List<ExerciseEntity>

    @Insert(onConflict = IGNORE)
    abstract suspend fun insert(workout: WorkoutEntity): Long

    @Query("DELETE FROM exercises WHERE workout_id=:workoutId")
    abstract suspend fun clearWorkout(workoutId: Long)

    @Query("UPDATE workouts SET date=:date, start_date=:startDate, end_date=:endDate, type=:type WHERE server_id=:id")
    abstract suspend fun updateWorkoutByServerId(
        id: Long,
        date: LocalDate,
        startDate: Instant?,
        endDate: Instant?,
        type: WorkoutType
    )

    @Update
    abstract suspend fun update(workout: WorkoutEntity)

    @Transaction
    open suspend fun updateWorkout(workout: WorkoutEntity) {
        if (workout.serverId != null) {
            val id = getIdByServerId(workout.serverId)!!
            update(workout.copy(id = id))
        } else {
            update(workout)
        }
    }

    @Transaction
    open suspend fun insertRemoteWorkouts(workouts: List<WorkoutEntity>) {
        // ID: 0 in all workouts
        // ServerID: Set
        // Look for each workout in local db
        // Insert if not found, else update
        for (workout in workouts) {
            val id = getIdByServerId(workout.serverId!!)
            if (id == null) {
                insert(workout)
            } else {
                updateWorkout(workout)
            }
        }
    }

    @Transaction
    open suspend fun insertOrUpdateWorkout(workout: WorkoutEntity): Long {
        val newId = insert(workout)

        // If inserting didn't fail (it's a new workout)
        if (newId != -1L) {
            return newId
        }

        updateWorkout(workout)

        return if (workout.serverId != null) {
            getIdByServerId(workout.serverId)!!
        } else {
            // If adding local-only workout (no serverId set)
            workout.id
        }
    }

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertExercises(exercises: List<ExerciseEntity>): LongArray

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertSets(sets: List<WorkoutSetEntity>): LongArray

    @Transaction
    open suspend fun saveWorkout(
        workout: WorkoutEntity
    ): Long {
        val workoutId = insertOrUpdateWorkout(workout)
        clearWorkout(workoutId)
        val exerciseEntities = workout.exercises.map {
            it.copy(workoutId = workoutId)
        }

        val exerciseIds = insertExercises(exerciseEntities)

        val sets = workout.exercises.flatMapIndexed { index: Int, exerciseEntity: ExerciseEntity ->
            exerciseEntity.sets.map { it.copy(exerciseId = exerciseIds[index]) }
        }

        insertSets(sets)

        return workoutId
    }
}