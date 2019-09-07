## Chapter 3. Navigation

The navigation is not a library component, but a simple design pattern / practice that I use in my apps. The idea is to create an abstraction layer, hiding the ugly details about opening fragments, dialogs and activities (I'm talking about the Intent / Bundle mechanism), keeping all these ugly details in a single class.

### ActivityRouter

Create a package ``router`` and a class ``ActivityRouter``. Please place here and only here ALL the Intent logic (do NOT use Intents in other places in the code):

```kotlin
object ActivityRouter
{
    fun startLoginActivity(from : Context)
    {
        val i=Intent(from, LoginActivity::class.java)
        from.startActivity(i)
    }
}
```

To start an activity, ALWAYS use the ``ActivityRouter`` and NOT the intent mechanism:
```kotlin
ActivityRouter.startLoginActivity(from = this)
```

The library provides some standard Intent arguments, by using extension methods. For instance,

```kotlin
fun startRestaurantListActivity(from : Context)
{
    val i=Intent(from, RestaurantListActivity::class.java)
    from.startActivity(i)
}

fun startChooseRestaurantActivity(from : Context)
{
    val i=Intent(from, RestaurantListActivity::class.java)
    i.setChooseMode()
    from.startActivity(i)
}
```

```kotlin
ActivityRouter.startChooseRestaurantActivity(from = it.context)
OnActivityResult<OnRestaurantChoosedEvent> { event ->
    println(event.restaurant.toString())
}
```

You can find more info about standard arguments in the next chapters.

### FragmentFactory

Create a ``FragmentFactory`` class to instantiate fragments:

```kotlin
object FragmentFactory
{
    fun newRestaurantListFragment() =
        RestaurantListFragment()
}
```

To instantiate a factory, ALWAYS use the ``FragmentFactory``:

```kotlin
override fun provideFragment() =
    FragmentFactory.newRestaurantListFragment()
```

### ShowDialog

I call the dialog router component a class named ``ShowDialog``. Please put here ALL the dialog instantiation and displaying logic. For instance:

```kotlin
object ShowDialog
{
    fun <T> withList(
        context : Context,
        title: Int,
        cancelable: Boolean = true,
        onClick: ((Int, T) -> (Unit))? = null,
        values: List<T>)
    {

        MaterialDialog(context)
            .title(title)
            .cancelable(cancelable)
            .show() {
                listItems(items = values.map { it.toString() }) { dialog, position, text ->
                    onClick?.invoke(position, values[position])
                }
            }
    }
}
```

To display a dialog,

```kotlin
ShowDialog.withList(context = this,
    title = R.string.choose_type,
    onClick = { index, value ->
        viewModel.notifyChange { restaurant ->
            restaurant.type=index+1
        }
    },
    values = listOf(
        getString(R.string.normal),
        getString(R.string.fast_food)))
```

Please do not stick with this routing / classification method. Extend it according to your needs, for instance:

- You have an app module that is logically separated from the rest of the app: create a class ``MyModuleActivityRouter``
- In ``ShowDialog`` you have only generic dialogs, but you also want to display very specific dialogs: create a class ``ShowDialogOnEvent`` (for instance: ``ShowDialogOnEvent.onUnsavedFormChanges``)
- Separate Intents used to open other apps: ``ExternalActivityRouter``
- Integrate your another app via deep linking: ``MyOtherAppActivityRouter``
- etc etc

### Intent / Bundle arguments

Please use the ``BundleBuilder`` library to pass other non-standard arguments, for instance:

```kotlin
@BundleBuilder
class MyActivity : BaseActivity.WithoutViewModel()
{
    @Arg @JvmField
    var quantity : Int = 0
}
```

Note: the ``MyActivityBundleBuilder.inject`` method will be called automatically in the ``BaseActivity`` class.

In the ``ActivityRouter``:

```kotlin
fun startMyActivity(from : Context, quantity : Int)
{
    MyActivityBundleBuilder()
        .quantity(quantity)
        .startActivity(from)
}
```

Please DO NOT USE [the vanilla intent argument-passing mechanism](https://stackoverflow.com/a/2091482/11536597). It is simply ugly and unclean. If you must use it, please override the ``loadDataFromIntent`` (on an activity) and ``loadDataFromArguments`` (on a fragment) and make sure you call the supermethod.

### Next chapter: [EventBus overview](https://github.com/andob/DobDroidMVVM/blob/master/tutorial/eventbus.md)
