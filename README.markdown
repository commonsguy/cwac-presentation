CWAC-Presentation: Second Screens Supported Succinctly
======================================================

This project
offers a series of classes that wrap around the `Presentation` and
`DisplayManager` of Android 4.2: 

- `PresentationHelper` consolidates basic `DisplayManager` handling, with
a listener to inform you when to show or remove your `Presentation`

- `PresentationFragment` extends `DialogFragment` and adds a bit of
extra logic to allow it to handle a `Presentation` rather than a simple
`Dialog`

- `WebPresentationFragment` simply extends `PresentationFragment` and displays
a `WebView` in the `Presentation`

- `MirroringFragment`, `MirroringWebViewFragment`, and `MirrorPresentationFragment`
leverage the mirroring logic from
[the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts) to help you
display a `Presentation` based upon mirrored content from the main screen

- `PresentationService`, for showing content on an external display from the
background, even if your primary UI is destroyed or otherwise not in the foreground

Installation
------------
This Android library project is 
[available as a JAR](https://gihub.com/commonsguy/cwac-presentation/releases).
If you wish to use the JAR, you will need to also add the JAR from
[the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts) to your
project if you wish to use the `Mirror*` classes. If you are not using the `Mirror*`
classes, then the CWAC-Presentation JAR is sufficient.

NOTE: The JAR name, as of v0.4.2, has a `cwac-` prefix, to help distinguish it from other JARs.

Also note that if you plan to use this as an Android library project
in source form, you
will also need to download [the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts)
(and, if needed, modify this project's configuration to point to your copy of
CWAC-Layouts' library project). Alternatively, download the CWAC-Layouts JAR into
the `libs/` directory of your clone of this project and remove the dependency on
the CWAC-Layouts library project.

This project is also available as
an artifact for use with Gradle. To use that, add the following
blocks to your `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://repo.commonsware.com.s3.amazonaws.com"
    }
}

dependencies {
    compile 'com.commonsware.cwac:presentation:0.4.+'
}
```

Or, if you cannot use SSL, use `http://repo.commonsware.com` for the repository
URL. This should automatically pull down the CWAC-Layouts dependency.

Usage: PresentationHelper
-------------------------
`PresentationHelper` is designed to be used by an `Activity` that wishes to
display a `Presentation` when a suitable `Display` is attached, and stop displaying
the `Presentation` when any prior such `Display` is detached.

To do this:

- Create an instance of `PresentationHelper`, probably in `onCreate()` of the
activity. You will need to supply a `Context` (probably `this`) and something
that implements the `PresentationHelper.Listener` interface.

- Forward the `onPause()` and `onResume()` events to the `PresentationHelper` by
calling the same-named methods on the helper.

- Implement the `showPreso()` method on your `Listener`. This receives a `Display`
object, and you are now able to display a `Presentation` on that `Display`.

- Implement the `clearPreso()` method on your `Listener`. At this point, you
should stop displaying any prior `Presentation`, if there was one. You are passed
a `boolean` value, `true` indicating that the activity is going away, `false`
indicating that we merely lost our `Display`. You can use this value to perhaps
optimize dealing with `Display` changes, without destroying all the data.

You can call `disable()` and `enable()` on the `PresentationHelper`. Calling
`disable()` stops the custom content and reverts the device to normal screen
mirroring mode. Calling `enable()` reverts a previous `disable()` call.

Usage: PresentationFragment
---------------------------
`PresentationFragment` is a thin veneer over `DialogFragment` to allow it to 
work with `Presentation` objects (which themselves inherit from `Dialog`).
This allows you to define the content for a `Presentation` in the form of
a fragment. And, like `DialogFragment`, you can elect to either use it for
a `Presentation` (via a call to `show()`) or use it as an ordinary `Fragment` in
the rest of your UI (via a `FragmentTransaction`). This can help you to work
both in dual-screen and single-screen scenarios.

Your `PresentationFragment` subclass should override `onCreateView()` to define
the contents of the `Presentation` (or what will be shown in the `Fragment`
when used as a regular fragment). The only significant change over any other
`Fragment` is that you should use `getContext()`, instead of `getActivity()`,
for any resources you create, such as inflating a layout. This ensures that
you get the right `Context` for the situation, such as the `Context` associated
with a secondary screen when used for a `Presentation`.

However, when creating the `PresentationFragment`, you also need to call
`setDisplay()`, to provide the `Display` object for use when the fragment is
shown as a `Presentation`. If you are not using it for a `Presentation` in
the current context, this call is not required. A typical approach for handling
`setDisplay()` is to use a factory method:

```
public static YourFragment newInstance(Context ctxt, Display display) {
  YourFragment frag=new YourFragment();

  frag.setDisplay(ctxt, display);

  return(frag);
}
```

Beyond this, `PresentationFragment` is a fairly ordinary `Fragment`.

If you wish to display this fragment in a `Presentation`, call `show()` on the
`PresentationFragment`, supplying your `FragmentManager` and a tag to use for
the fragment itself. To get rid of the `Presentation`, call `dismiss()` on
the `PresentationFragment`.

Usage: WebPresentationFragment
------------------------------
`WebPresentationFragment` is simply a mash-up of `PresentationFragment` and
`WebViewFragment`, to allow a `WebView` to be displayed in a `Presentation`.
You use it just like `WebViewFragment`, except for the need to call
`setDisplay()` (per the `PresentationFragment` instructions above). So,
for example, `getWebView()` returns the `WebView` hosted by the
`WebPresentationFragment`.

Usage: Mirroring Presentation Classes
-------------------------------------
There are three classes that take advantage of the mirroring support included
in [the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts).

`MirroringFragment` works much like a regular `Fragment`. However, instead of
overriding `onCreateView()`, you override `onCreateMirroredContent()`.
`onCreateMirroredContent()` takes the same parameters as does `onCreateView()`,
and your job is the same: create the content to be displayed by the fragment.
The difference is that your returned `View` will be wrapped in a
`MirroringFrameLayout`.

`MirroringWebViewFragment` is a mash-up of `MirroringFragment` and
`WebViewFragment`, to allow a `WebView` to be mirrored. Use `getWebView()`
to retrieve the `WebView` hosted by this fragment.

`MirrorPresentationFragment` is a `PresentationFragment` designed to mirror
the contents of a `MirroringFragment`. To use this, create an instance using
the `newInstance()` factory method, taking a `Context` and the desired
`Display` as parameters. Then, call `setMirror()` on your `MirroringFragment`,
supplying the `MirrorPresentationFragment`. From there, you can `show()`
and `dismiss()` the `MirrorPresentationFragment` as you would any other
`PresentationFragment`. By having the `MirroringFragment` on the main
screen, and having the `MirrorPresentationFragment` on an external display,
whatever the user manipulates on the screen is rendered to the external
display, ideal for presentation settings (e.g., conferences).

Note that `MirroringFragment` suffers the same limitations as does
`MirroringFrameLayout`, in that it will work with fairly ordinary `View`s,
plus `WebView`, but not `SurfaceView` or things that use `SurfaceView`
(e.g., `VideoView`, Maps V2 maps).

Usage: PresentationService
--------------------------
`PresentationService` is an abstract base class for you to extend, where
`PresentationService` handles showing your content on an external display,
and you simply manage that content.

In your `PresentationService` subclass, you will need to implement two
methods:

- `getThemeId()`, which returns the ID of the style resource that you want
to use for content being shown on the external display.

- `buildPresoView()`, which returns the `View` that represents the content to
show on the external display. Note that since this is a `Service`, not an
`Activity`, you cannot use fragments, only views. `buildPresoView()` is
passed a `Context` and a `LayoutInflater` for your use to set up the
content to be displayed.

You may optionally override the standard lifecycle methods (though please chain
to the superclass!) and `buildLayoutParams()`, which returns a
`WindowManager.LayoutParams` describing how your `View` should be applied to
the external display. The default implementation of `buildLayoutParams()` is
probably adequate for your needs.

You may also optionally override the `showPreso()` and `clearPreso()` methods
defined by `PresentationHelper.Listener`, though, once again, please chain
to the superclass implementations.

Then, all you need to do is:

- Add your service to your manifest, along with the `SYSTEM_ALERT_WINDOW`
permission

- Arrange to start and stop the service as needed

Once started, the service will automatically call `buildPresoView()` and
show the content, once an external display is detected.

If things that the user does in your UI should affect the behavior
of the service and its content, use a message bus implementation, such as:

- `LocalBroadcastManager`
- Square's Otto
- greenrobot's EventBus

Your `PresentationService` can receive bus messages and update the `View`
accordingly. Note that there is no present means to *replace* the `View`, so
you may wish to have `buildPresoView()` return a `FrameLayout` or something else
whose contents you can replace *in toto* if needed.

Note that it is safe to call `startService()` on the service multiple times,
if you do not know whether the service is already running and need to ensure
that it is running now.

Dependencies
------------
This project depends on Android 4.2 and higher (API Level 17) to actually
do its work. It should survive on older devices, simply doing nothing.

This project also depends upon
[the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts).

Version
-------
This is version v0.4.1 of this module, meaning it is coming along nicely.

Demo
----
In the `demo/` sub-project you will find a sample project demonstrating the use
of the aforementioned classes, with the exception of `PresentationService`.
There is a separate `demoService/` sub-project with a sample implementation
of `PresentationService`.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with
`commonsware-cwac` and `android` after [searching to see if there already is an answer](https://stackoverflow.com/search?q=[commonsware-cwac]+presentation). Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please post an [issue](https://github.com/commonsguy/cwac-presentation/issues).
Be certain to include complete steps for reproducing the issue.

Do not ask for help via Twitter.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.

Release Notes
-------------
- v0.4.2: updated Gradle, fixed manifest for merging, added `cwac-` prefix to JAR
- v0.4.1: tweak for v0.4.0 of CWAC-Layouts
- v0.4.0: added `PresentationService`
- v0.3.0: migrated to Gradle
- v0.2.0: handle API level diffs, support enable/disable of `PresentationHelper`
- v0.1.0: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

