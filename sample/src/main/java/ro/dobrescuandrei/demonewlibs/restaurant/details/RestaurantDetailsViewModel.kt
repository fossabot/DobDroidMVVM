package ro.dobrescuandrei.demonewlibs.restaurant.details

import ro.dobrescuandrei.demonewlibs.api.GetRestaurantDetailsRequest
import ro.dobrescuandrei.demonewlibs.model.Restaurant
import ro.dobrescuandrei.demonewlibs.model.utils.FirstPageHeader
import ro.dobrescuandrei.demonewlibs.model.utils.SecondPageHeader
import ro.dobrescuandrei.mvvm.details.BaseDetailsViewModel
import ro.dobrescuandrei.utils.yieldListOf

class RestaurantDetailsViewModel : BaseDetailsViewModel<Restaurant>()
{
    var firstPageStickyHeaderIndex  : Int = Int.MAX_VALUE
    var secondPageStickyHeaderIndex : Int = Int.MAX_VALUE

    override fun getItems() : List<Any>
    {
        val restaurant=GetRestaurantDetailsRequest(model.id).execute()

        return yieldListOf<Any> {
            firstPageStickyHeaderIndex=index()
            yield(FirstPageHeader())
            for (i in 1..10)
                yield(restaurant)

            secondPageStickyHeaderIndex=index()
            yield(SecondPageHeader())
            for (i in 1..10)
                yield(restaurant)
        }
    }
}
