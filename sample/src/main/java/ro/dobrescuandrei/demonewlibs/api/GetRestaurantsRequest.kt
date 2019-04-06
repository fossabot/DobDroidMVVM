package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Single
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.RestaurantFilter
import ro.dobrescuandrei.utils.yieldListOf

class GetRestaurantsRequest
(
    val filter : RestaurantFilter
) : BaseRequest<Single<List<Restaurant>>>()
{
    override fun execute() = Single.fromCallable {
        Thread.sleep(1000)
        return@fromCallable yieldListOf<Restaurant> {
            if (filter.offset<400)
                for (i in filter.offset+1..filter.offset+filter.limit)
                    yield(Restaurant(name = "R$i", rating = 5, type = Restaurant.TYPE_NORMAL))
        }
    }
}
