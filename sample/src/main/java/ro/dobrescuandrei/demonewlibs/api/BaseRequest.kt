package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable

abstract class BaseRequest<T>
{
    abstract fun execute() : Observable<T>
}