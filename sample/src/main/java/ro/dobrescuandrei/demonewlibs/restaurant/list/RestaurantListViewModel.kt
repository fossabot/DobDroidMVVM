package ro.dobrescuandrei.demonewlibs.restaurant.list

import io.reactivex.Single
import ro.dobrescuandrei.demonewlibs.api.GetRestaurantsRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.RestaurantFilter
import ro.dobrescuandrei.mvvm.list.BaseListViewModel

class RestaurantListViewModel : BaseListViewModel<Restaurant, RestaurantFilter>(RestaurantFilter())
{
    override fun getItems(filter : RestaurantFilter) : Single<List<Restaurant>> =
        GetRestaurantsRequest(filter).execute()
}