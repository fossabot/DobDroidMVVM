package ro.dobrescuandrei.demonewlibs.restaurant.editor

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.api.AddRestaurantRequest
import ro.dobrescuandrei.demonewlibs.api.EditRestaurantRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID
import ro.dobrescuandrei.mvvm.editor.BaseEditorViewModel

class RestaurantEditorViewModel : BaseEditorViewModel<Restaurant, ID>(Restaurant())
{
    override fun add(restaurant : Restaurant) : Observable<ID> =
        AddRestaurantRequest(restaurant).execute()

    override fun edit(restaurant : Restaurant) : Observable<ID> =
        EditRestaurantRequest(restaurant).execute()
}
