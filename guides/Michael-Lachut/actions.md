So, you would like to do some hyperthreading?
Well get ready to learn.

# Actions.java
Let's start there. That is an action. Or at least it WANTS to be an action.

It has **two** interfaces that you can extend using lambda functions or regular classes.

### Runner
It has a method called run that accepts 0 arguments and returns zero values. Use like so.
```java
Actions.Runner myRunner = () -> foo();
```
That uses a *lambda function*. It's a function, that lambdas.
Replace `foo()` with the function you intend to run.

But now that we know how to run it, *what else do we do*? And how do we *__check if it's busy__*?

### Busy Checker
Same as last time, but it can return a `boolean`. Like so:
```java
Actions.BusyChecker myBusyChecker = () => isCodeWorking();
```
