package ro.dobrescuandrei.demonewlibs.api

import ro.dobrescuandrei.demonewlibs.model.Restaurant

class AddRestaurantRequest
(
    val restaurant : Restaurant
) : BaseRequest<Unit>()
{
    override fun execute() {}
}