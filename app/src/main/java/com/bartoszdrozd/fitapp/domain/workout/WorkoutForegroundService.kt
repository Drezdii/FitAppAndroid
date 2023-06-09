package com.bartoszdrozd.fitapp.domain.workout

import android.Manifest
import android.app.Notification
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.utils.programIdToNameId
import com.bartoszdrozd.fitapp.utils.toWorkoutDuration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.Timer
import java.util.TimerTask

class WorkoutForegroundService : Service() {
    private val activeWorkouts = mutableMapOf<Long, Timer>()

    // Which workout is currently using the service's notification
    private var mainWorkoutId: Long? = null
    private val notificationId = 1234

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val START_WORKOUT = "com.bartoszdrozd.fitapp.START_WORKOUT"
        const val STOP_WORKOUT = "com.bartoszdrozd.fitapp.STOP_WORKOUT"
        const val STOP_SERVICE = "com.bartoszdrozd.fitapp.STOP_SERVICE"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_SERVICE) {
            stopSelf()
        }

        val workoutId = intent?.getLongExtra("workoutId", -1) ?: -1
        val startDate = intent?.getLongExtra("startDate", 0) ?: 0
        val programId = intent?.getIntExtra("programId", 0) ?: 0
        val programWeek = intent?.getIntExtra("programWeek", 0) ?: 0

        if (intent?.action == STOP_WORKOUT && workoutId in activeWorkouts) {
            activeWorkouts[workoutId]?.cancel()
            activeWorkouts.remove(workoutId)

            if (workoutId != mainWorkoutId) {
                NotificationManagerCompat.from(this).cancel(workoutId.toInt())
            } else {
                mainWorkoutId = activeWorkouts.keys.firstOrNull()
                mainWorkoutId?.toInt()?.let { NotificationManagerCompat.from(this).cancel(it) }
            }

            if (activeWorkouts.isEmpty()) {
                stopSelf()
            }
        }

        if (intent?.action == START_WORKOUT) {
            if (workoutId !in activeWorkouts) {
                val contentIntent = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(
                        Intent(
                            Intent.ACTION_VIEW,
                            "fitapp://workout/${workoutId}".toUri()
                        )
                    )
                    getPendingIntent(workoutId.toInt(), PendingIntent.FLAG_IMMUTABLE)
                }

                val programLabel =
                    if (programId != 0) "${this.getString(programIdToNameId(programId))} Week $programWeek" else null
                
                val builder = Notification.Builder(this, "WORKOUT_ACTIVE")
                    .setSmallIcon(R.drawable.ic_deadlift)
                    .setContentTitle(programLabel ?: "Active workout")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setContentIntent(contentIntent)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    builder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
                }

                if (activeWorkouts.isEmpty()) {
                    mainWorkoutId = workoutId
                    startForeground(notificationId, builder.build())
                }

                val timer = Timer()

                activeWorkouts[workoutId] = timer

                timer.scheduleAtFixedRate(object : TimerTask() {
                    val startDateInstant = Instant.fromEpochSeconds(startDate)

                    override fun run() {
                        val durationText =
                            Clock.System.now().minus(startDateInstant).toWorkoutDuration()

                        builder.setContentText("Workout is currently active\nDuration: $durationText")

                        with(NotificationManagerCompat.from(this@WorkoutForegroundService)) {
                            if (ActivityCompat.checkSelfPermission(
                                    this@WorkoutForegroundService,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // Use the service's notification if only one workout is in progress
                                if (activeWorkouts.size == 1 || mainWorkoutId == workoutId) {
                                    notify(notificationId, builder.build())
                                } else {
                                    notify(workoutId.toInt(), builder.build())
                                }
                            }
                        }
                    }

                }, 0, 1000)
            }
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        // Clean up any leftover timers when user closes the application
        for (timer in activeWorkouts.values) {
            timer.cancel()
        }

        stopSelf()
    }
}