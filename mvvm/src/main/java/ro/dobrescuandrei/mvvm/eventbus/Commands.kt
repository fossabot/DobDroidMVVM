package ro.dobrescuandrei.mvvm.eventbus

open class FinishAllActivitiesCommand
{
    open class WithTypes
    (
        val types : List<Class<*>>
    ) : FinishAllActivitiesCommand()
    {
        constructor(type : Class<*>) : this(types = listOf(type))
    }

    class OfTypes : FinishAllActivitiesCommand.WithTypes
    {
        constructor(types: List<Class<*>>) : super(types)
        constructor(type: Class<*>) : super(type)
    }

    class Except : FinishAllActivitiesCommand.WithTypes
    {
        constructor(types: List<Class<*>>) : super(types)
        constructor(type: Class<*>) : super(type)
    }
}
