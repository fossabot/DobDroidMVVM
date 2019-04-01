package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.model.Restaurant

class EditRestaurantRequest
(
    val restaurant : Restaurant
) : BaseRequest<Unit>()
{
    override fun execute() = Observable.fromCallable<Unit> {
        Thread.sleep(1000)
    }
}