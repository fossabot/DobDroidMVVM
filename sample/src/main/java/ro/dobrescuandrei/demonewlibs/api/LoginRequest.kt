package ro.dobrescuandrei.demonewlibs.api

import io.reactivex.Observable
import ro.dobrescuandrei.demonewlibs.model.User

class LoginRequest
(
    val username : String,
    val password : String
) : BaseRequest<User>()
{
    override fun execute() = Observable.fromCallable {
        Thread.sleep(1000)
        return@fromCallable User(name = "asdf")
    }
}