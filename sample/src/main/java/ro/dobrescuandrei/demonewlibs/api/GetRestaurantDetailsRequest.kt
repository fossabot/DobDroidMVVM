package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Single
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID

class GetRestaurantDetailsRequest
(
    val id : ID
) : BaseRequest<Single<Restaurant>>()
{
    override fun execute() = Single.fromCallable {
        Thread.sleep(1000)
        return@fromCallable Restaurant(id = id, name = "R$id", rating = 5, type = Restaurant.TYPE_NORMAL)
    }
}
