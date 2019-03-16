package ro.dobrescuandrei.mvvm.eventbus

import android.app.Activity
import org.greenrobot.eventbus.EventBus

open class EventBusFacade
{
    private val greenRobotEventBus = EventBus()

    @PublishedApi
    internal val activityResultEventBus = ActivityResultEventBus()

    fun register(target : Any)
    {
        try {greenRobotEventBus.register(target)}
        catch (ex : Exception) {}
    }

    fun unregister(target : Any)
    {
        if (target is Activity)
            activityResultEventBus.unregister(target)

        try {greenRobotEventBus.unregister(target)}
        catch (ex : Exception) {}
    }

    fun post(event : Any)
    {
        activityResultEventBus.post(event)
        greenRobotEventBus.post(event)
    }
}

object BackgroundEventBus : EventBusFacade()
object ForegroundEventBus : EventBusFacade()
