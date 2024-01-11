package com.google.codelabs.buildyourfirstmap.classes
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ActivityManager {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val interval = 5L

    val activityList = listOf(
        "Takes a stroll through the forest",
        "Trips over a rock",
        "Falls into a pit",
        "Constructs a shelter from branches",
        "Catches a fish in the river"
    )

    init {
        leaveShelter()
    }

    fun leaveShelter() {
        val activityTask = Runnable {
            randomActivity()
        }
        scheduler.scheduleAtFixedRate(activityTask, 0, interval, TimeUnit.SECONDS)
    }

    fun joinShelter() {
        scheduler.shutdown()
    }

    private fun randomActivity() {
        val randomIndex = (0 until activityList.size).random()
        val storyMessage = activityList[randomIndex]
        println(storyMessage)
    }
}