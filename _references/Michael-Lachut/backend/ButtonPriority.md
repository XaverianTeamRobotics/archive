---
permalink: /reference/Michael-Lachut/backend/ButtonPriority
---

# ButtonPriority

## About

**Location:**                   utils   <br>
**Type:**                       class   <br>
**Abstract:**                   no

**Description:**
It is what it is. Read the note under the **Methods** section

<br>
<br>

# Methods

While all functions don't need documentation, they all do the same exact thing. lets use `f1` as an example.

```java
public boolean g1HasF1 = true;
public boolean f1(boolean g1) { return g1 == g1HasF1; }
```

What's happening is `g1HasF1`'s default value is `true`, meaning that Gamepad1 should have priority. The boolean input value
should be true if the input being checked is Gamepad1. Here is an usage example:

```java
if ((gamepad2.left_stick_y >= 0.25 | gamepad2.left_stick_y <= -0.25) && priority.f2(false)) {
    spinner.setPower(gamepad2.left_stick_y);
}
```

**NOTE:** by default, `g1HasF2` is `false`

In this scenario, `priority` represents our **ButtonPriority** class.
The first section of the if statement (`gamepad2.left_stick_y >= 0.25 | gamepad2.left_stick_y <= -0.25`) ensures there is no stick drift.
Then we tell our **ButtonPriority** that we want to evaluate `f2`, but we are not using Gamepad1.