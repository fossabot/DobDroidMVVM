package ro.dobrescuandrei.mvvm.eventbus

import org.greenrobot.eventbus.EventBus
import ro.andreidobrescu.activityresulteventbus.ActivityResultEventBus

object ForegroundEventBus : EventBus()

object BackgroundEventBus : EventBus()
{
    override fun post(event: Any?)
    {
        super.post(event)

        if (event!=null)
            ActivityResultEventBus.post(event)
    }
}
