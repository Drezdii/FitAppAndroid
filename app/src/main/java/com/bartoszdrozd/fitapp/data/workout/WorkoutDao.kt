package com.bartoszdrozd.fitapp.data.workout

import androidx.room.Dao
import androidx.room.Insert
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

    @Query("SELECT * from exercises where id IN (:ids)")
    abstract suspend fun getExercises(ids: List<Long>): List<ExerciseEntity>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertWorkouts(workouts: List<WorkoutEntity>): List<Long>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertExercises(exercises: List<ExerciseEntity>): LongArray

    @Insert(onConflict = REPLACE)
    abstract suspend fun insertSets(sets: List<WorkoutSetEntity>): LongArray

    @Transaction
    open suspend fun saveWorkout(
        workout: WorkoutEntity
    ): Long {
        val workoutId = insertWorkout(workout)
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