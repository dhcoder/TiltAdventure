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
* Test everything that can be tested (using JUnit 4)
* When classes return mutable values that shouldn't be modified, use an Immutable variant instead. However, if a method
takes in a parameter that should be immutable, just take in the mutable version, and trust the method not to modify it.
The reason for this is, it's easy enough for a class to allocate an immutable field along with the immutable one, and
thus it can avoid thrashing the GC, but for callers, they don't often have that luxury.

## On the fence

There are some rules I currently don't follow but may change my mind about some day

* Using Pools. This would be great to avoid crazy allocations, but then I have to remember to alloc / free everything.
For now, I'm just going to try to preallocate everything up front and not allocate much during the main game loop.
Opts and that sort of stuff make this tricky, so I may see if there's a way to use pools in that case...

* Immutables. I may get rid of them entirely, or on the flip side, I may start requiring them for parameters, if I could
do so without worrying about calling "new Immutable" all the time (maybe pools will help here). It's just so nice and
clean to have the compiler verify things for you.