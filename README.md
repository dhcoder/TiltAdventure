# TiltAdventure

## Goal

An old school zelda-inspired game which uses tilt controls (and other inputs that take advantage of the average
smart phone) for navigation and interaction with the world (battles and puzzles).

## Building for the first time

This is a Gradle project. The first time you open the project on your machine, you should do it by choosing
`File -> Import Project...` and selected the `build.gradle` file located in the root directory. If you ever pull down
from GitHub and get strange project errors, you may first want to try opening up the Gradle toolbar and clicking on the
"Sync" button to see if that fixes things.

## Organization

This project is based on [libdgx](http://libgdx.badlogicgames.com/) and, as such, is divided into platforms:

* android/
* desktop/
* core/

Most of the game logic is under core/, whereas the target-specific folders exist only to publish the game to their
proper format.

There are also some utility modules:

* dhcoder.support/
* dhcoder.libgdx/
* dhcoder.test/

dhcoder.test is a small set of utility classes that help me out in my unit tests. dhcoder.support is a growing set of
general utility classes that I could, in theory, port over to any other Java application. dhcoder.libgdx, in contrast,
is a set of utility classes that build on top of libgdx itself.

## JDK / ADK

In your project settings, you need to add a JDK named "TiltAdv JDK" that points to JDK 1.6 or later, and "TiltAdv
ADK" that points to Android API 18 or later.

This extra step allows each contributor to have their own JDK setup (1.6 here, 1.8 there), and those details are
all abstracted away behind a common, shared name. The language level should be set to 6, though, because of Android
limitations.

You may need to additionally create an `ANDROID_HOME` environment variable that points to the location of your
Android SDK. Do this if Gradle complains about not finding the SDK dir.
 
## Code Style

This project is meant to be modified using IntelliJ IDEA, as its project settings are set up so that you can use the
`Code -> Reformat Code...` option before submitting code for review.
 
There are some additional rules followed by this codebase...

* [Never use nulls](https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained). Instead, use `Opt`.
* For callbacks, Java has all these funky names you have to memorize (like `Predicate` and `Consumer`). Use `Action` and
`Func`, instead (see `dhcoder.support.lambda`)
* Prefer composition over inheritance. Really, avoid inheritance. 
    * An abstract base class is OK to provide default implementations of interface methods or allow base methods to be
    protected instead of public like they are in an interface, but no matter what, you should never use `super` except
    in constructors. Having to rely on `super` to do magical things behind your back is a way towards codebase
    fragility.
* Values returned from getXXX methods should be treated as immutable. Values passed in as parameters should also be
treated as immutable.
    * If I wasn't worried about performance and unnecessary allocations, I would just have made immutable versions of
    each class, but alas, we need to sacrifice safety for a more responsive program.
* Fields should never be public. Access them through a getXXX or setXXX method if you need to change their values.
    * Following this rule makes it easier to know I can just modify a single setXXX method and that it will work
    everywhere.
* Avoid all allocations that I have any control over while the user can control the player.
    * This appeases the android GC from nibbling away at your game performance.
    * **Run Android monitor and track allocations**
    * Use Pools
    * Avoid `StringUtils.format` in code paths that get called a lot (this does a couple of minor allocations under the
      hood.
    * Consider triggering the GC while the game is transitioning, say, from one room to another, etc., where a drop in
      frame rate is less noticeable.

## On the fence

There are some rules I currently don't follow religiously but may change my mind about some day

* Test everything that can be tested (using JUnit 4)
    * Tests should be FAST
    * Tests should never write to a file, make a database connection, etc.
    