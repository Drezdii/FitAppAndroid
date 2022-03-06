package com.bartoszdrozd.fitapp.data.workout

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.bartoszdrozd.fitapp.SaveWorkoutMutation
import com.bartoszdrozd.fitapp.WorkoutListQuery
import com.bartoszdrozd.fitapp.WorkoutQuery
import com.bartoszdrozd.fitapp.model.workout.Exercise
import com.bartoszdrozd.fitapp.model.workout.Workout
import com.bartoszdrozd.fitapp.model.workout.WorkoutSet
import com.bartoszdrozd.fitapp.model.workout.WorkoutType
import com.bartoszdrozd.fitapp.type.ExerciseInput
import com.bartoszdrozd.fitapp.type.SetInput
import com.bartoszdrozd.fitapp.type.WorkoutInput
import com.bartoszdrozd.fitapp.type.WorkoutTypeCode
import javax.inject.Inject

class WorkoutRemoteDataSource @Inject constructor(
    private val apolloClient: ApolloClient
) : IWorkoutDataSource {
    override suspend fun getUserWorkouts(userId: String): List<Workout> {
        val result = apolloClient.query(WorkoutListQuery(userId)).execute().dataAssertNoErrors

        return result.userWorkouts.map { workout ->
            Workout(
                workout.id.toInt(),
                workout.date,
                workout.startDate,
                workout.endDate,
                WorkoutType.valueOf(workout.type.name)
            )
        }
    }

    override suspend fun getWorkout(id: Int): Workout? {
        val result = apolloClient.query(WorkoutQuery(id.toString())).execute().dataAssertNoErrors
        return result.workout?.let { wrk ->
            Workout(
                wrk.id.toInt(),
                wrk.date,
                wrk.startDate,
                wrk.endDate,
                WorkoutType.valueOf(wrk.type.name),
                exercises = wrk.exercises.map { exr ->
                    Exercise(
                        exr.id.toInt(),
                        exr.exerciseInfoId,
                        sets = exr.sets.map { set ->
                            WorkoutSet(
                                set.id.toInt(),
                                set.reps,
                                set.weight,
                                set.completed
                            )
                        }
                    )
                }
            )
        }
    }

    override suspend fun saveWorkout(workout: Workout): Workout {
        val workoutInput = WorkoutInput(
            id = workout.id.toString(),
            date = workout.date,
            startDate = Optional.Present(workout.startDate),
            endDate = Optional.Present(workout.endDate),
            type = WorkoutTypeCode.valueOf(workout.type.name),
            exercises = workout.exercises.map {
                ExerciseInput(
                    it.id.toString(),
                    it.exerciseInfoId,
                    sets = it.sets.map { set ->
                        SetInput(set.id.toString(), set.reps, set.weight, set.completed)
                    }
                )
            }
        )

        val result =
            apolloClient.mutation(SaveWorkoutMutation(workoutInput)).execute().dataAssertNoErrors

        return Workout()
    }
}