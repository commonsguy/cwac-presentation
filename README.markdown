CWAC-Presentation: Second Screens Supported Succinctly
======================================================

This project
offers a series of classes that wrap around `Presentation` and
`DisplayManager`: 

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
This project is available as an artifact for use with Gradle.

There are two versions of this library, for AndroidX and for the older Android Support Library.

If you cannot use SSL, use `http://repo.commonsware.com` for the repository URL.

### AndroidX

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    implementation 'com.commonsware.cwac:presentation.x:0.6.1'
}
```

### Android Support Library

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    implementation 'com.commonsware.cwac:presentation:0.5.3'
}
```

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

**NOTE**: In the AndroidX artifacts (`presentation.x`), `PresentationFragment`
extends `androidx.fragment.app.Fragment`. In the legacy artifact (`presentation`),
`PresentationFragment` extends `android.app.Fragment`.

Usage: WebPresentationFragment
------------------------------
`WebPresentationFragment` is simply a mash-up of `PresentationFragment` and
`WebViewFragment`, to allow a `WebView` to be displayed in a `Presentation`.
You use it just like `WebViewFragment`, except for the need to call
`setDisplay()` (per the `PresentationFragment` instructions above). So,
for example, `getWebView()` returns the `WebView` hosted by the
`WebPresentationFragment`.

**NOTE**: In the AndroidX artifacts (`presentation.x`), `WebPresentationFragment`
inherits from `androidx.fragment.app.Fragment`. In the legacy artifact (`presentation`),
`WebPresentationFragment` inherits from `android.app.Fragment`.

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

**NOTE**: In the AndroidX artifacts (`presentation.x`), these fragments
inherit from `androidx.fragment.app.Fragment`. In the legacy artifact (`presentation`),
these fragments inherit from `android.app.Fragment`.

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

You may also optionally override `getWindowType()`. This should return
the window type `int` to be used for the "window" we are going to use for
the external display. The stock implementation of `getWindowType()` uses
`TYPE_TOAST` prior to Android 7.1 and `TYPE_SYSTEM_ALERT` on Android 7.1+.
If you are using Cast Remote Display, you may need to override this method
and return `TYPE_PRIVATE_PRESENTATION` (untested).

Then, all you need to do is to arrange to start and stop the service as needed.
Once started, the service will automatically call `buildPresoView()` and
show the content, once an external display is detected.

If things that the user does in your UI should affect the behavior
of the service and its content, use a message bus implementation, such as
`LocalBroadcastManager`.
Your `PresentationService` can receive bus messages and update the `View`
accordingly. Note that there is no present means to *replace* the `View`, so
you may wish to have `buildPresoView()` return a `FrameLayout` or something else
whose contents you can replace *in toto* if needed.

Note that it is safe to call `startService()` on the service multiple times,
if you do not know whether the service is already running and need to ensure
that it is running now.

Note that using this on Android 7.1+, where a `TYPE_SYSTEM_ALERT` window is
used, requires the user to go into Settings and allow your app to draw over
other apps.

On Android 8.0+, **please** use multi-display instead of `PresentationService`.
However, `PresentationService` will work on Android 8.0+ if needed.

JavaDocs
--------
You can browse
[the JavaDocs for the latest release](http://javadocs.commonsware.com/cwac/presentation/index.html).

Dependencies
------------
This project depends on Android 4.2 and higher (API Level 17) to actually
do its work.

This project also depends upon
[the CWAC-Layouts project](http://github.com/commonsguy/cwac-layouts).

The AndroidX edition of this artifact (`presentation.x`) depends upon
`androidx.fragment:fragment`.

Version
-------
This is version v0.6.1 of this artifact, meaning it is coming along nicely.

Note that the Android Support library edition of this arifact (`presentation`)
remains at 0.5.3. Outside of critical bug fixes, no further work is planned
for this version.

Demo
----
In the `demo/` sub-project you will find a sample project demonstrating the use
of the aforementioned classes, with the exception of `PresentationService`.
There is a separate `demoService/` sub-project with a sample implementation
of `PresentationService`.

Additional Documentation
------------------------
[The Busy Coder's Guide to Android Development](https://commonsware.com/Android)
contains a chapter dedicated to the `Presentation` API. This chapter walks through
a few sample apps that use classes from this library. Another chapter in the book
examines a somewhat larger app that supports output on TVs, etc. by a variety of
means (e.g., direct-to-TV devices like Android TV and Fire TV) including `Presentation`
and this library's classes.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [Stack Overflow](http://stackoverflow.com/questions/ask) tagged with
`commonsware-cwac` and `android` after [searching to see if there already is an answer](https://stackoverflow.com/search?q=[commonsware-cwac]+presentation). Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please post an [issue](https://github.com/commonsguy/cwac-presentation/issues).
The [contribution guidelines](CONTRIBUTING.md)
provide some suggestions for how to create a bug report that will get
the problem fixed the fastest.

You are also welcome to join
[the CommonsWare Community](https://community.commonsware.com/)
and post questions
and ideas to [the CWAC category](https://community.commonsware.com/c/cwac).

Do not ask for help via social media.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.
Again, the [contribution guidelines](CONTRIBUTING.md) provide a bit
of guidance here.
 
Release Notes
-------------
- v0.6.1: fixed less-than bug
- v0.6.0: migrated to AndroidX and started a new artifact (`presentation.x`)
- v0.5.3: fixed less-than bug
- v0.5.2: added support for Android 8.0+
- v0.5.1: updated to new Gradle, Android Plugin for Gradle, etc.
- v0.5.0: better `PresentationService` support for Android 7.1+, demo bug fixes
- v0.4.6: JavaDocs, sources included in repo; source tree reorg; build files update
- v0.4.5: got `PresentationService` working again on Android 5.1
- v0.4.4: updated for Android Studio 1.0 and new AAR publishing system
- v0.4.3: removed `SYSTEM_ALERT_WINDOW` permission requirement
- v0.4.2: updated Gradle, fixed manifest for merging, added `cwac-` prefix to JAR
- v0.4.1: tweak for v0.4.0 of CWAC-Layouts
- v0.4.0: added `PresentationService`
- v0.3.0: migrated to Gradle
- v0.2.0: handle API level diffs, support enable/disable of `PresentationHelper`
- v0.1.0: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

