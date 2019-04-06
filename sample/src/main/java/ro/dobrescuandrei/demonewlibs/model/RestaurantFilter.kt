package ro.dobrescuandrei.demonewlibs.model

import ro.andreidobrescu.basefilter.BaseFilter

class RestaurantFilter
(
    var rating : Int? = null,
    var type : Int? = null
) : BaseFilter()