# TiltAdventure

## Goal

An old school zelda-inspired game which uses tilt controls (and other inputs that take advantage of the average
smart phone) for navigation and interaction with the world (battles and puzzles).

## Organization
This project is based on [libdgx](http://libgdx.badlogicgames.com/) and, as such, is divided into various sections:

* android/
* desktop/
* core/

Most of the game logic is under core/, whereas the target-specific folders exist only to publish the game to their
proper format.

There is also a utility module (d9n.utils) which the main project depends on.
 
## Code Style

This project is meant to be modified using IntelliJ IDEA, as its project settings are set up so that you can use the
`Code -> Reformat Code...` option before submitting code for review.
 
There are some additional rules followed by this codebase...

* Never use nulls. Instead, use `Opt`.
* For predicate logic, Java has all these funky names you have to memorize. Use `Action` and `Func`, instead.
* Prefer composition over inheritance. Really, avoid inheritance. (Of course interface implementation is fine). 
    * An abstract base class is OK to provide default implementations of interface methods, but basically you should never
use `super` except in constructors.
* Test everything that can be tested (using JUnit 4)