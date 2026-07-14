# Chapter 20: The Laplace transform, and its poles

This chapter visualises a tool that engineers use every day and that most people only ever meet
as a table of formulas: the Laplace transform. Seen the right way it is not a formula at all but
a landscape, and its most important features, the poles, are spikes you can walk around.

## From a signal to a surface

The Laplace transform takes a function of time, `f(t)`, and produces a function `F(s)` of a
complex number `s = sigma + i*omega`:

```
F(s) = integral from 0 to infinity of f(t) e^(-s t) dt
```

Because `s` is complex, `F(s)` lives over a plane, not a line. Read the plane like a map: the
real part `sigma` (left to right) measures growth or decay, and the imaginary part `omega` (front
to back) measures oscillation. At each point we can ask how big the transform is, `|F(s)|`, and
use that as a height. The transform becomes a surface.

`LaplaceFunction` (in `org.physics.engine.transform`) gives the magnitude for a few signals whose
transforms are famous:

- **cos(t)** and **sin(t)**: transforms `s/(s^2+1)` and `1/(s^2+1)`.
- **e^(-t)**: transform `1/(s+1)`.
- **the constant 1**: transform `1/s`.

## Poles: the spikes that say everything

A pole is a value of `s` where the transform blows up to infinity, because the denominator hits
zero. On the surface, a pole is a spike shooting to the sky, and where the spikes sit tells you
everything about the signal:

- **cos(t)** and **sin(t)** oscillate at frequency 1 and never decay, so their poles sit right on
  the imaginary axis at `s = +/- i`, at height 1 up the oscillation axis. A test confirms the
  magnitude explodes as `omega` approaches 1.
- **e^(-t)** decays and does not oscillate, so its single pole sits out on the negative real axis
  at `s = -1`. The further left a pole, the faster the decay. A test confirms the spike is there
  and that the surface is calm elsewhere.
- **the constant 1** has its pole at the origin.

Engineers read a system's stability straight off this map: poles on the left half mean the
system settles, poles on the right mean it runs away, and poles on the axis mean it rings
forever. That is the whole language of control theory, and it is just "where are the spikes?".

## The scene

`LaplaceScene` draws the magnitude surface over the complex plane as an orbitable coloured
wireframe (the same 3D approach as the quantum scenes). Drag to rotate. Use the left and right
arrows to switch signals and watch the poles jump to new places: a pair up the imaginary axis for
the oscillations, a lone spike on the negative real axis for the decay. The spikes really do go
to infinity, so their height is capped to keep them on screen, and the colour runs from cool in
the valleys to hot at the peaks.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.transform.LaplaceFunctionTest"
```

## What to take away from this chapter

- The Laplace transform turns a signal into a surface over the complex plane; `sigma` is decay,
  `omega` is oscillation.
- Poles are the points where the transform is infinite, drawn as spikes.
- Where the poles sit captures the signal: on the imaginary axis for pure oscillation, on the
  negative real axis for decay.
- Reading those pole locations is how engineers judge whether a system is stable.
