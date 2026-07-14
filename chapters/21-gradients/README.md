# Chapter 21: Gradients and partial derivatives

A function of one variable has a single slope at each point. A function of two variables, a
landscape with a height at every `(x, y)`, has a slope that depends on which way you walk. This
chapter builds the two ideas that tame that: the partial derivative and the gradient, and draws
them over a 3D surface.

## Partial derivatives: one direction at a time

Stand somewhere on the landscape. Walk due east, increasing `x` while holding `y` fixed, and ask
how fast the height changes. That rate is the partial derivative with respect to `x`, written
`df/dx`. Walk due north instead, holding `x` fixed, and you get `df/dy`. Each partial is an
ordinary one-variable slope; you have just frozen the other variable to get it.

`ScalarField` (in `org.physics.engine.field`) computes these numerically, by nudging the point a
hair each way and measuring the change (a central difference). That works for any landscape
without anyone solving a derivative by hand. A test checks it against a field whose slopes we
know exactly, the saddle `0.25(x^2 - y^2)`, whose partials are `0.5x` and `-0.5y`.

## The gradient: both partials as one arrow

The gradient bundles the two partials into a single vector:

```
gradient = ( df/dx , df/dy )
```

That little arrow, living in the flat input plane, carries a lot of meaning:

- It points **straight uphill**, in the direction the height climbs fastest. A test confirms
  that on the single peak, from a point off to the side, the gradient points back toward the
  summit.
- Its **length is the steepness** of that climb.
- It is **perpendicular to the contour lines**, the paths of constant height. The flattest way to
  walk is along a contour; the steepest is square across it. A test verifies this: stepping along
  the gradient changes the height far more than stepping at right angles to it.
- It is **zero where the ground is level**, at a peak, a pit, or a saddle. Another test checks the
  gradient vanishes at the top of the hill.

The gradient is the engine of a huge amount of applied maths: to find a minimum of anything, from
a physics energy to a machine-learning error, you repeatedly step in the direction opposite the
gradient, downhill. That is gradient descent.

## The scene

`GradientScene` draws the landscape as an orbitable 3D surface, coloured by height, and lays a
grid of white gradient arrows on the flat ground beneath it. Watch the arrows: they always point
up the nearest slope, run square across the surface's contours, grow long on the steep flanks,
and shrink to nothing under a peak or a saddle. The arrows are normalised so the steepest one is
a fixed length, keeping them readable.

Drag to orbit. Use the left and right arrows to switch landscapes: a single peak (arrows all
point inward to the top), a saddle (arrows sweep around the central pass), an egg-box of waves,
and a pair of peaks (watch the arrows part company along the valley between them).

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.field.ScalarFieldTest"
```

## What to take away from this chapter

- A partial derivative is the slope in one axis direction, with the other variable held fixed.
- The gradient is the pair of partials as a vector; it points straight uphill and its length is
  the steepness.
- The gradient is always perpendicular to the contours and is zero at level points.
- Stepping against the gradient walks you downhill, which is how almost everything gets minimised.
