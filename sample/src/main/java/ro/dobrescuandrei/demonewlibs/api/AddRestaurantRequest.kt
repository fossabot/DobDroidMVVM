package ro.dobrescuandrei.demonewlibs.api

import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID

class AddRestaurantRequest
(
    val restaurant : Restaurant
) : BaseRequest<Unit>()
{
    override fun execute() {}
}