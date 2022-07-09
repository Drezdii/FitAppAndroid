package com.bartoszdrozd.fitapp.data.workout

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.OnConflictStrategy.Companion.REPLACE
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
    abstract fun get(id: Long): Flow<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    abstract fun getOnce(id: Long): WorkoutWithExercises?

    @Query("SELECT * from exercises where id IN (:ids)")
    abstract suspend fun getExercises(ids: List<Long>): List<ExerciseEntity>

    @Insert(onConflict = IGNORE)
    abstract suspend fun insert(workout: WorkoutEntity): Long

    @Query("DELETE FROM exercises WHERE workout_id=:workoutId")
    abstract suspend fun clearWorkout(workoutId: Long)

    @Query("DELETE FROM workouts WHERE id = :id")
    abstract suspend fun delete(id: Long)

    @Update
    abstract suspend fun update(workout: WorkoutEntity)

    @Transaction
    open suspend fun insert(workouts: List<WorkoutEntity>) {
        for (workout in workouts) {
            insertOrUpdateWorkout(workout)
        }
    }

    @Transaction
    open suspend fun insertOrUpdateWorkout(workout: WorkoutEntity): Long {
        val newId = insert(workout)

        // If inserting didn't fail (it's a new workout)
        return if (newId != -1L) {
            newId
        } else {
            update(workout)
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

        val exerciseIds = insertExercises(workout.exercises)

        val sets = workout.exercises.flatMapIndexed { index: Int, exerciseEntity: ExerciseEntity ->
            exerciseEntity.sets.map { it.copy(exerciseId = exerciseIds[index]) }
        }

        insertSets(sets)

        return workoutId
    }
}