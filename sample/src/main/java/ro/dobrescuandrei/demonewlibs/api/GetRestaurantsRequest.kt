package ro.dobrescuandrei.demonewlibs.api

import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.RestaurantFilter
import ro.dobrescuandrei.utils.yieldListOf

class GetRestaurantsRequest
(
    val search : String?,
    val filter : RestaurantFilter,
    val limit : Int,
    val offset : Int
) : BaseRequest<List<Restaurant>>()
{
    override fun execute() = yieldListOf<Restaurant> {
        if (offset<400)
            for (i in offset+1..offset+limit)
                yield(Restaurant(name = "R$i", rating = 5, type = Restaurant.TYPE_NORMAL))
    }
}
