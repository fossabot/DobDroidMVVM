package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID

class EditRestaurantRequest
(
    val restaurant : Restaurant
) : BaseRequest<ID>()
{
    override fun execute() = Observable.fromCallable<ID> {
        Thread.sleep(1000)
        return@fromCallable 100
    }
}