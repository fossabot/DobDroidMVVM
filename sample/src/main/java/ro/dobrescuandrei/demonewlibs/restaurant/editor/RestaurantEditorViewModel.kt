package ro.dobrescuandrei.demonewlibs.restaurant.editor

import ro.dobrescuandrei.demonewlibs.api.AddRestaurantRequest
import ro.dobrescuandrei.demonewlibs.api.EditRestaurantRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.mvvm.editor.BaseEditorViewModel

class RestaurantEditorViewModel : BaseEditorViewModel<Restaurant>(Restaurant())
{
    override fun add(restaurant : Restaurant) =
        AddRestaurantRequest(restaurant).execute()

    override fun edit(restaurant : Restaurant) =
        EditRestaurantRequest(restaurant).execute()
}
