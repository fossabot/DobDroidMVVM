package ro.dobrescuandrei.mvvm.eventbus

import android.app.Activity
import org.greenrobot.eventbus.EventBus

object ForegroundEventBus : EventBus()

object BackgroundEventBus : EventBus()
{
    @PublishedApi
    internal val activityResultEventBus = ActivityResultEventBus()

    override fun unregister(subscriber: Any?)
    {
        super.unregister(subscriber)

        if (subscriber!=null&&subscriber is Activity)
            activityResultEventBus.unregister(subscriber)
    }

    override fun post(event: Any?)
    {
        super.post(event)

        if (event!=null)
            activityResultEventBus.post(event)
    }
}
