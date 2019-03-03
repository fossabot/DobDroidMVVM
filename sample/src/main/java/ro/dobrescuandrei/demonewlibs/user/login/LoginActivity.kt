package ro.dobrescuandrei.demonewlibs.user.login

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.Subscribe
import ro.dobrescuandrei.demonewlibs.R
import ro.dobrescuandrei.demonewlibs.model.utils.OnLoggedInEvent
import ro.dobrescuandrei.demonewlibs.router.ActivityRouter
import ro.dobrescuandrei.mvvm.BaseActivity
import ro.dobrescuandrei.utils.setOnEditorActionListener

class LoginActivity : BaseActivity<LoginViewModel>()
{
    override fun viewModelClass() : Class<LoginViewModel> = LoginViewModel::class.java
    override fun layout() : Int = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        loginButton.setOnClickListener {
            viewModel.onLoginClicked(
                username = usernameEditText.text.toString().trim(),
                password = passwordEditText.text.toString().trim())
        }

        passwordEditText.setOnEditorActionListener { actionId ->
            if (actionId==EditorInfo.IME_ACTION_DONE)
                loginButton.performClick()
        }
    }

    @Subscribe
    fun onLoggedIn(event : OnLoggedInEvent)
    {
        ActivityRouter.startRestaurantListActivity(from = this)
        finish()
    }
}