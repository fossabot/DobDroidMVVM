package ro.dobrescuandrei.mvvm.eventbus

import android.app.Activity

class ActivityResultTypedEventListener<EVENT>
(
    val eventClass : Class<EVENT>,
    val eventConsumer : (EVENT) -> (Unit)
)

class ActivityResultEventBus
{
    private val data : MutableMap<Activity, MutableList<ActivityResultTypedEventListener<*>>> = mutableMapOf()

    fun <EVENT : Any> post(event : EVENT)
    {
        val eventClass=event::class.java
        for ((activity, eventListeners) in data)
        {
            eventListeners.removeAll { eventListener ->
                if (eventListener.eventClass==eventClass)
                {
                    (eventListener.eventConsumer as (EVENT) -> (Unit)).invoke(event)
                    return@removeAll true
                }

                return@removeAll false
            }
        }
    }

    fun register(activity : Activity, eventListener: ActivityResultTypedEventListener<*>)
    {
        val eventListeners=data[activity]?:mutableListOf()
        eventListeners.add(eventListener)
        data[activity]=eventListeners
    }

    fun unregister(activity : Activity)
    {
        if (data.containsKey(activity))
            data.remove(activity)
    }
}

inline fun <reified EVENT> Activity.OnActivityResult(noinline eventListener : (EVENT) -> (Unit))
{
    BackgroundEventBus.activityResultEventBus.register(activity = this,
        eventListener = ActivityResultTypedEventListener(EVENT::class.java, eventListener))

    ForegroundEventBus.activityResultEventBus.register(activity = this,
        eventListener = ActivityResultTypedEventListener(EVENT::class.java, eventListener))
}
