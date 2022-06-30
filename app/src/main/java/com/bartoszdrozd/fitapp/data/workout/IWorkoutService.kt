package com.bartoszdrozd.fitapp.data.workout

import com.bartoszdrozd.fitapp.data.dtos.ProgramCycleDTO
import com.bartoszdrozd.fitapp.data.dtos.WorkoutDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IWorkoutService {
    @GET("workouts/user/{userId}")
    suspend fun getWorkouts(@Path("userId") userId: String): Response<List<WorkoutDTO>>

    @GET("workouts/{workoutId}")
    suspend fun getWorkout(@Path("workoutId") workoutId: Long): Response<WorkoutDTO?>

    @POST("workouts")
    suspend fun saveWorkout(@Body workout: WorkoutDTO): Response<WorkoutDTO>

    @POST("workouts/program")
    suspend fun saveProgramCycle(@Body programCycle: ProgramCycleDTO): Response<ProgramCycleDTO>
}