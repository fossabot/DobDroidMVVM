## Chapter 1. Intro

### Project setup

To import DobDroidMVVM:

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
```
dependencies {
    implementation 'com.github.andob:DobDroidMVVM:1.1.8'
    implementation 'com.chibatching.kotpref:kotpref:2.7.0'
    //other libraries, AppCompat, Retrofit, Glide, ROOM etc etc
}
```

Your application class must look like:

```kotlin
class App : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        Kotpref.init(context = this)

        //define the languages supported by the app
        val locales = LinkedList<Locale>()
        locales.add(Locale(LANGUAGE_ENGLISH))
        locales.add(Locale(LANGUAGE_ROMANIAN))
        LocaleChanger.initialize(applicationContext, locales)

        RxJavaPlugins.setErrorHandler { ex ->
            if (BuildConfig.DEBUG)
                ex.printStackTrace()
        }
    }
}
```

Let your app theme extend NoActionBar: 

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
```

### Library dependencies overview

DobDroidMVVM depends on the following libraries. Please study them before using this library:

- Base MVVM implementation: Google Architecture Components MVVM todo link

- Libraries implementing the EventBus design pattern: GreenRobot EventBus and ActivityResultEventBus todo link

- My misc utilities library: DobDroidMiscUtils todo link

- To detect keyboard opened / closed events: keyboardvisibilityevent todo link

- Utility library to change in app language: LocaleChanger todo link

- My DelarativeAdapter library, the easiest cleanest way to declare RecyclerView adapters todo link

- MaterialSearchView todo link

- RxJava, RxKotlin and RxAndroid todo link

- Library that generates boilerplate Bundle / Intent code for starting activities / fragments: BundleArgs todo link

- SharedPreference object mapper: kotpref todo link
    
This library and all dependent libraries are commercial projects friendly, Apache or MIT licenced.

### Next chapter: Simple screens with loading / error states (todo add link)
