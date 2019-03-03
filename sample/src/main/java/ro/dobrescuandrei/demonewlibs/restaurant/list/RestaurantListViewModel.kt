package ro.dobrescuandrei.demonewlibs.restaurant.list

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.api.GetRestaurantsRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.RestaurantFilter
import ro.dobrescuandrei.mvvm.list.BaseListViewModel

class RestaurantListViewModel : BaseListViewModel<Restaurant, RestaurantFilter>(RestaurantFilter())
{
    override fun getItems() : Observable<List<Restaurant>> =
        GetRestaurantsRequest(search, filter, limit(), offset).execute()
}