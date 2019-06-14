package ro.dobrescuandrei.mvvm.utils

fun Throwable.getRootMessage() : String
{
    if (cause!=null)
        return cause!!.getRootMessage()

    return message?:""
}
