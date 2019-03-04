package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable
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
    override fun execute() = Observable.fromCallable {
        Thread.sleep(1000)
        return@fromCallable yieldListOf<Restaurant> {
            if (offset<400)
                for (i in offset+1..offset+limit)
                    yield(Restaurant(id = i, name = "R$i", rating = 5, type = Restaurant.TYPE_NORMAL))
        }
    }
}
