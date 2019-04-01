package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID

class GetRestaurantDetailsRequest
(
    val id : ID
) : BaseRequest<Restaurant>()
{
    override fun execute() : Observable<Restaurant> = Observable.fromCallable {
        Thread.sleep(1000)
        return@fromCallable Restaurant(id = id, name = "R$id", rating = 5, type = Restaurant.TYPE_NORMAL)
    }
}
