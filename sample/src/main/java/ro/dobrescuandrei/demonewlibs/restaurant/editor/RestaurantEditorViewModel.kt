package ro.dobrescuandrei.demonewlibs.restaurant.editor

import ro.dobrescuandrei.demonewlibs.api.AddRestaurantRequest
import ro.dobrescuandrei.demonewlibs.api.EditRestaurantRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.OnRestaurantAddedEvent
import ro.dobrescuandrei.demonewlibs.model.utils.OnRestaurantEditedEvent
import ro.dobrescuandrei.mvvm.editor.BaseEditorViewModel
import ro.dobrescuandrei.mvvm.eventbus.BackgroundEventBus

class RestaurantEditorViewModel : BaseEditorViewModel<Restaurant>(Restaurant())
{
    override fun add(restaurant : Restaurant) =
        AddRestaurantRequest(restaurant).execute()

    override fun edit(restaurant : Restaurant) =
        EditRestaurantRequest(restaurant).execute()

    override fun onAdded(restaurant : Restaurant) =
        BackgroundEventBus.post(OnRestaurantAddedEvent(restaurant))

    override fun onEdited(restaurant : Restaurant) =
        BackgroundEventBus.post(OnRestaurantEditedEvent(restaurant))
}
