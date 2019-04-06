package ro.dobrescuandrei.demonewlibs.api

abstract class BaseRequest<RESULT_OBSERVABLE>
{
    abstract fun execute() : RESULT_OBSERVABLE
}