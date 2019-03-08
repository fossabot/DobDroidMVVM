package ro.dobrescuandrei.demonewlibs.api

import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.ID

class GetRestaurantDetailsRequest
(
    val id : ID
) : BaseRequest<Restaurant>()
{
    override fun execute() = Restaurant(id = id, name = "R$id", rating = 5, type = Restaurant.TYPE_NORMAL)
}
