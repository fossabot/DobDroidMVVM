package ro.dobrescuandrei.mvvm.details

import ro.dobrescuandrei.mvvm.list.BaseListViewModel
import ro.dobrescuandrei.mvvm.utils.Identifiable

abstract class BaseDetailsViewModel<MODEL : Identifiable<*>> : BaseListViewModel<Any, Unit>(Unit)
{
    lateinit var model : MODEL
}