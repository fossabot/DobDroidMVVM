package ro.dobrescuandrei.demonewlibs.user.login

import android.text.TextUtils
import ro.dobrescuandrei.demonewlibs.R
import ro.dobrescuandrei.demonewlibs.api.LoginRequest
import ro.dobrescuandrei.demonewlibs.model.utils.OnLoggedInEvent
import ro.dobrescuandrei.demonewlibs.model.utils.Preferences
import ro.dobrescuandrei.mvvm.BaseViewModel
import ro.dobrescuandrei.mvvm.eventbus.ForegroundEventBus

class LoginViewModel : BaseViewModel()
{
    fun onLoginClicked(username : String, password : String)
    {
        when
        {
            TextUtils.isEmpty(username) -> showError(R.string.please_type_username)
            TextUtils.isEmpty(password) -> showError(R.string.please_type_password)
            else ->
            {
                try
                {
                    val user=LoginRequest(username, password).execute()

                    Preferences.userId=user.id
                    Preferences.username=user.name

                    ForegroundEventBus.post(OnLoggedInEvent())
                }
                catch (ex : Exception)
                {
                    showError(R.string.invalid_username_or_password)
                }
            }
        }
    }
}