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

There is also a utility module (dhcoder.support) which the main project depends on and a very small test module
(dhcoder.test) which provides helper classes for unit testing.
 
## Code Style

This project is meant to be modified using IntelliJ IDEA, as its project settings are set up so that you can use the
`Code -> Reformat Code...` option before submitting code for review.
 
There are some additional rules followed by this codebase...

* Never use nulls. Instead, use `Opt`.
* For callbacks, Java has all these funky names you have to memorize (like `Predicate` and `Consumer`). Use `Action` and
`Func`, instead.
* Prefer composition over inheritance. Really, avoid inheritance. (Of course interface implementation is fine). 
    * An abstract base class is OK to provide default implementations of interface methods or allow base methods to be
    protected instead of public like they are in an interface, but no matter what, you should never use `super` except
    in constructors. Having to rely on `super` to do magical things behind your back is a way towards codebase
    fragility.
* Values returned from getXXX methods should be treated as immutable. Values passed in as parameters should also be
treated as immutable.
* Fields should never be public. Access them through a getXXX or setXXX method if you need to change this.

## On the fence

There are some rules I currently don't follow but may change my mind about some day

* Using Pools. This would be great to avoid crazy allocations, but then I have to remember to alloc / free everything.
For now, I'm just going to try to preallocate everything up front and not allocate much during the main game loop.
Opts and that sort of stuff make this tricky, so I may see if there's a way to use pools in that case...
* Test everything that can be tested (using JUnit 4)
    * Tests should be FAST
    * Tests should never write to a file, make a database connection, etc.
    