## Chapter 2. Simple screen with loading / error states

### Example - a login screen

The BaseActivity / BaseFragment / BaseViewModel classes are the core of the system. Theese classes contains methods for handling loading / error states, among other things:

- showing a loading dialog: ``ViewModel.showLoading()`` or (not recommended) ``Activity.showLoadingDialog()``
- hiding the loading dialog: ``ViewModel.hideLoading()`` or (not recommended) ``Activity.hideLoadingDialog()``
- showing an error: ``ViewModel.showError(message : String)`` or ``ViewModel.showError(stringResourceId : Strng)`` or ``ViewModel.showError(exception : Throwable)`` or (not recommended) ``Activity.showToast()``

Let's create our first ViewModel:

```kotlin
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
                showLoading()

                LoginRequest(username, password).execute()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
```

As you see, in the ViewModel you can change the loading / error state of the activity / fragment. Internally, the ``BaseViewModel`` uses its ``errorLiveData : MutableLiveData<ErrorHolder>`` and ``loadingLiveData : MutableLiveData<Boolean>`` Live Datas to communicate with the activity.

We need to store our logged in user data. To do so, use the kotpref library. Create a ``Preferences`` class:

```kotlin
object Preferences : KotprefModel()
{
    var userId by cached(stringPref())
    var username by cached(stringPref())
}
```

You can also use EventBus mechanism to communicate between components. Create an ``Events`` class containing:

```kotlin
class OnLoggedInEvent
```

For more about the EventBus, please see Chapter 4 - EventBus overview todo add link

Back to the ``LoginViewModel``, handle on success / error cases:

```
                    .subscribeBy(onSuccess = { user ->
                        hideLoading()

                        Preferences.userId=user.id
                        Preferences.username=user.name

                        ForegroundEventBus.post(OnLoggedInEvent())
                    }, onError = {
                        hideLoading()

                        showError(R.string.invalid_username_or_password)
                    })
            }
        }
    }
}
```

For demo purposes, I used a ``LoginRequest`` class that provides a ``Single<User>`` RxJava observable object and mimics an API call:

```kotlin
class LoginRequest
(
    val username : String,
    val password : String
) : BaseRequest<Single<User>>()
{
    override fun execute() = Single.fromCallable {
        Thread.sleep(1000)
        return@fromCallable User(name = "asdf")
    }
}
```

You can use Retrofit for this, for instance:

```kotlin
class LoginRequest
(
    val username : String,
    val password : String
) : BaseRequest<Single<User>>()
{
    override fun execute() = ApiClient.Instance.login(this)
}
```

```kotlin
@POST("/login")
fun login(
    @Body body : LoginRequest
) : Single<User>
``` 

### Next step: create a ``LoginActivity`` class:

```kotlin
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
    }
```

Note: each component provided by this library can be used as an Activity or as a Fragment. For instance, to create a ``LoginFragment`` simply extend ``BaseFragment`` and replace ``onCreate`` with ``onActivityCreated``.

When the ``LoginViewModel`` will complete the API call, a ``OnLoggedInEvent`` event will be received. Use the routing mechanism to navigate between screens (see Chapter 3 - Navigation todo add link)

```kotlin
    @Subscribe
    fun onLoggedIn(event : OnLoggedInEvent)
    {
        ActivityRouter.startRestaurantListActivity(from = this)
        finish()
    }
}
```

### Custom LiveData objects

On the activity / fragment, you can observe the live data changes:

```kotlin
viewModel.errorLiveData.observe { errorHolder ->
    println(errorHolder.toString())
}

viewModel.loadingLiveData.observeNullable { isLoading ->
    println(isLoading)
}
```

With ``observe``, you will always receive a non nullable object in the lambda, whereas with ``observeNullable``, you can receive a null ``isLoading`` value (this is the default Architecture Components MVVM behaviour).

You can also create and use your own live data object:

```kotlin
val processProgressPercent : MutableLiveData<Float> by lazy { MutableLiveData<Float>() }

viewModel.processProgressPercent.observe { processProgressPercent ->
    println(processProgressPercent)
}
        
viewModel.processProgressPercent.value=4f
```

### Screens without ViewModels

If your activity / fragment is so simple that is doesn't event need a ViewModel, you can use ``BaseActivity / BaseFragment.WithoutViewModel``:

```kotlin
class AboutActivity : BaseActivity.WithoutViewModel()
{
    override fun layout() = R.layout.activity_about
}
```

### Next chapter: Navigation (todo add link)
