package com.bartoszdrozd.fitapp.data.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.bartoszdrozd.fitapp.data.entities.ExerciseEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutSetEntity
import com.bartoszdrozd.fitapp.data.entities.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WorkoutDao {
    @Query("SELECT * FROM workouts")
    abstract fun getAll(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    abstract suspend fun get(id: Long): WorkoutWithExercises?

    @Query("SELECT server_id FROM workouts where id = :id")
    abstract suspend fun getRealId(id: Long): Long?

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = IGNORE)
    abstract suspend fun insertWorkouts(workouts: List<WorkoutEntity>): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertExercises(exercises: List<ExerciseEntity>): LongArray

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertSets(sets: List<WorkoutSetEntity>): LongArray

    @Transaction
    open suspend fun saveWorkout(
        workout: WorkoutEntity,
        exercises: List<ExerciseEntity>,
        sets: List<WorkoutSetEntity>
    ): Long {
        val workoutId = insertWorkout(workout)

        val exerciseEntities = exercises.map {
            it.copy(workoutId = workoutId)
        }

        val exerciseIds = insertExercises(exerciseEntities)

        val groups = sets.groupBy { it.exerciseId }

        val setEntities = mutableListOf<WorkoutSetEntity>()

        groups.onEachIndexed { index, entry ->
            entry.value.mapTo(setEntities) {
                it.copy(exerciseId = exerciseIds[index])
            }
        }

        insertSets(setEntities)

        return workoutId
    }
}