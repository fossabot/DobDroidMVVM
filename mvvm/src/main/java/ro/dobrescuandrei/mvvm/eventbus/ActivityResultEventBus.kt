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
            eventListeners.find { it.eventClass==eventClass }
                ?.let { (it.eventConsumer as (EVENT) -> (Unit)).invoke(event) }
        }
    }

    fun dispose(activity : Activity)
    {
        //activity was resumed, dispose event listeners
        if (data[activity]?.isEmpty()==false)
            data[activity]=mutableListOf()
    }

    fun register(activity : Activity, eventListener: ActivityResultTypedEventListener<*>)
    {
        //activity was created
        val eventListeners=data[activity]?:mutableListOf()
        eventListeners.add(eventListener)
        data[activity]=eventListeners
    }

    fun unregister(activity : Activity)
    {
        //activity was destroyed
        if (data.containsKey(activity))
            data.remove(activity)
    }
}

inline fun <reified EVENT> Activity.OnActivityResult(noinline eventListener : (EVENT) -> (Unit))
{
    BackgroundEventBus.activityResultEventBus.register(activity = this,
        eventListener = ActivityResultTypedEventListener(EVENT::class.java, eventListener))
}
