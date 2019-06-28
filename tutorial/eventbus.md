## Chapter 4. EventBus overview

This library relies on the ``EventBus`` mechanism / design pattern.

### I usually group my event classes into two categories

1. A file called ``Events.kt`` with ``On*Event`` naming convention. For instance,

```kotlin
class OnRestaurantAddedEvent(val restaurant : Restaurant)
class OnLoggedInEvent
```

2. A file called ``Commands.kt`` with ``*Command`` naming convention. For instance,

```kotlin
class RefreshRestaurantListCommand
class ExitAppCommand
```

### There are three environments to send events

1. Foreground events via GreenRobot EventBus. These events are sent only to the Activities / Fragments that are currently in the foreground / visible to the user. For instance:

```kotlin
ForegroundEventBus.post(OnRestaurantChoosedEvent(restaurant))
``` 

```kotlin
@Subscribe
fun onRestaurantChoosed(event : OnRestaurantChoosedEvent) {}
```

2. Background events via GreenRobot EventBus. These events are sent to all running activities and fragments:

```kotlin
BackgroundEventBus.post(ReloadRestaurantListCommand())
```

```kotlin
@Subscribe
fun reload(command : ReloadRestaurantListCommand) {}
```

If we have two restaurant list activities running in the background, both activities will be notified

3. Background events via ActivityResultEventBus. These events are sent to the background activity that started the intent that is currently running in the foreground (Similar to the vanilla onActivityResult mechanism, but way more cleaner):

```kotlin
override fun onItemChoosed(restaurant : Restaurant)
{
    BackgroundEventBus.post(OnRestaurantChoosedEvent(restaurant))
    finish()
}
```

```kotlin
ActivityRouter.startChooseRestaurantActivity(from = it.context)
OnActivityResult<OnRestaurantChoosedEvent> { event ->
    println(event.restaurant.toString())
}
```

### Standard events and commands

The library defines the following standard events and commands:

1. ``OnEditorModel<>.*Event``, when a model is added or edited in an editor screen (form) - see chapter 9 (todo link)

```kotlin
@Subscribe
override fun onAdded(event : OnEditorModel.AddedEvent<Restaurant>)
{
    BackgroundEventBus.post(OnRestaurantAddedEvent(restaurant = event.model))
    showToast(getString(R.string.restaurant_added))
    finish()
}

@Subscribe
override fun onEdited(event : OnEditorModel.EditedEvent<Restaurant>)
{
    showToast(getString(R.string.restaurant_edited))
    finish()
}
```

2. ``FinishAllActivitiesCommand``. This command can be used to finish background activities. Useful for logout, for instance:

To finish all running activities:

```kotlin
BackgroundEventBus.post(FinishAllActivitiesCommand())
```

Just some activities:

```kotlin
BackgroundEventBus.post(FinishAllActivitiesCommand.OfTypes(listOf(SomeActivity::class.java, AnotherActivity::class.java)))
BackgroundEventBus.post(FinishAllActivitiesCommand.Except(types = listOf(SomeActivity::class.java, AnotherActivity::class.java)))
```

### Next chapter: List screens (todo add link)
