package ro.dobrescuandrei.mvvm.details

import ro.dobrescuandrei.mvvm.list.BaseListViewModel

abstract class BaseDetailsViewModel<MODEL : Any> : BaseListViewModel<Any, Unit>(Unit)
{
    lateinit var model : MODEL
}