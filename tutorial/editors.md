## Chapter 9. Editor (forms) screens

Editor screens are used to create forms that adds or edits a model.

### Create the ViewModel:

```kotlin
class RestaurantEditorViewModel : BaseEditorViewModel<Restaurant>(Restaurant())
{
    override fun add(restaurant : Restaurant) =
        AddRestaurantRequest(restaurant).execute()

    override fun edit(restaurant : Restaurant) =
        EditRestaurantRequest(restaurant).execute()
        
    override fun onAdded(restaurant : Restaurant) =
        BackgroundEventBus.post(OnRestaurantAddedEvent(restaurant))

    override fun onEdited(restaurant : Restaurant) =
        BackgroundEventBus.post(OnRestaurantEditedEvent(restaurant))
}
```

```kotlin
class OnRestaurantAddedEvent(val restaurant : Restaurant)
class OnRestaurantEditedEvent(val restaurant : Restaurant)
```

### Create the layout

[The layout](https://github.com/andob/DobDroidMVVM/blob/master/sample/src/main/res/layout/activity_restaurant_editor.xml)

### Create the form adapter

Because the kotlin language lacks partial classes, I use inheritance to split the activity implementation into two. The first component is the form adapter. The form adapter is a partial, abstract implementation of the activity that simply binds the model to the view and checks for form validation errors:

```kotlin
abstract class RestaurantEditorAdapter : BaseEditorActivity<Restaurant, RestaurantEditorViewModel>()
{
    override fun viewModelClass() = RestaurantEditorViewModel::class.java
    override fun layout() : Int = R.layout.activity_restaurant_editor

    override fun show(restaurant : Restaurant)
    {
        viewModel.isValid=true

        if (restaurant.type==null)
        {
            typeErrorLabel.visibility=View.VISIBLE
            typeLabel.visibility=View.GONE
            viewModel.isValid=false
        }
        else
        {
            typeErrorLabel.visibility=View.GONE
            typeLabel.visibility=View.VISIBLE
            typeLabel.text=restaurant.getTypeAsString(resources)
        }

        ratingSeekBar.progress=restaurant.rating-1

        //prevent stack overflow exceptions
        if (!nameEditText.isFocused)
            nameEditText.setText(restaurant.name)

        if (TextUtils.isEmpty(restaurant.name))
        {
            nameTextInput.error=getString(R.string.type_name)
            viewModel.isValid=false
        }
        else
        {
            nameTextInput.error=null
        }
    }
}
```

The code is straightforward. The model is binded to the views. Validation views are being showed if there is an error and hided otherwise.

The ``show`` method will be called EACH TIME the model changes, thus providing a realtime, reactive validation and rendering capability. Validation errors will be displayed in realtime. For instance if the user deletes all the text from an EditText, an error will be displayed into the parent TextInputLayout. As soon as the user types something, the error will disappear.

### Create the activity

```kotlin
class RestaurantEditorActivity : RestaurantEditorAdapter()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        toolbar.setupBackIcon()
        toolbar.setTitle(if (viewModel.addMode())
            R.string.add_restaurant
        else R.string.edit_restaurant)
```

Note: there are two behaviours, add and edit.

The activity is responsible for transforming user events into ViewModel changes events. For instance, the user clicks the type button, a dialog is displayed. The user picks a type for the dialog. Next, the ViewModel is notified via the ``notifyChange`` method that the model has changed. Each time ``notifyChange`` is called, the activity is re-rendered, by calling the ``show`` method: 

```
        typeButton.setOnClickListener {
            ShowDialog.withList(context = this,
                title = R.string.choose_type,
                onClick = { index, value ->
                    viewModel.notifyChange { restaurant ->
                        restaurant.type=when (index)
                        {
                            0 -> Restaurant.TYPE_NORMAL
                            1 -> Restaurant.TYPE_FAST_FOOD
                        }
                    }
                },
                values = listOf(
                    getString(R.string.normal),
                    getString(R.string.fast_food)))
        }
```

Another example with a seek bar:

```
        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                viewModel.notifyChange { restaurant ->
                    restaurant.rating=progress+1
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }
        })
```

Another example with an edit text:

```
        nameEditText.setOnTextChangedListener { name ->
            viewModel.notifyChange { restaurant ->
                restaurant.name=name
            }
        }
```

Another example with ActivityResult:

```
parentRestaurantButton.setOnClickListener {
    ActivityRouter.startChooseRestaurantActivity(from = this)
    OnActivityResult<OnRestaurantChoosedEvent> { event ->
        viewModel.notifyChange { restaurant ->
            restaurant.parentId=event.restaurant.id
        }
    }
} 
```

IMPORTANT NOTE: Each form input must be binded to ViewModel change events!!!

Also implement events triggered when the ViewModel finishes adding or editing a model, after the save button is pressed:

```
@Subscribe
fun onAdded(event : OnRestaurantAddedEvent)
{
    showToast(getString(R.string.restaurant_added))
    finish()
}

@Subscribe
fun onEdited(event : OnRestaurantEditedEvent)
{
    showToast(getString(R.string.restaurant_edited))
    finish()
}
```

In this sample, I also used a ``OnRestaurantAddedEvent`` event. This event is catched into ``RestaurantListActivity``

```kotlin
@Subscribe
fun onRestaurantAdded(event : OnRestaurantAddedEvent)
{
    if (chooseMode)
        onItemChoosed(event.restaurant)
}
```

If the list activity is opened in choose mode and the add button is pressed, if the user adds an element, the new element will be automatically chosed from the list, removing from the user the responsibility to search it in the list :)

### Routing the activity:

In your activity router:

```kotlin
fun startAddRestaurantActivity(from : Context)
{
    val i=Intent(from, RestaurantEditorActivity::class.java)
    i.setAddMode()
    from.startActivity(i)
}

fun startEditRestaurantActivity(from : Context, restaurant : Restaurant)
{
    val i=Intent(from, RestaurantEditorActivity::class.java)
    i.setModel(restaurant)
    i.setEditMode()
    from.startActivity(i)
}
```

You can also provide a add template:

```kotlin
fun startAddRestaurantActivity(from : Context, templateRestaurant : Restaurant)
{
    val i=Intent(from, RestaurantEditorActivity::class.java)
    i.setModel(templateRestaurant)
    i.setAddMode()
    from.startActivity(i)
}
```

This is particulary useful if your task is to open the add form with some preselected form fields (useful for UX), for instance:

```kotlin
ActivityRouter.startAddRestaurantActivity(from = this, templateRestaurant = Restaurant(type = Restaurant.TYPE_FAST_FOOD))
```
