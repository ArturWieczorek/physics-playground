# Chapter 15: The brachistochrone, the fastest descent

This chapter is a puzzle with a famous, counter-intuitive answer. You have a bead that slides
without friction down a wire, from a high point to a lower one off to the side. What shape
should the wire be so the bead arrives in the shortest time? The obvious guess, a straight
ramp, is wrong. The straight line is the shortest path, but not the fastest.

## A bead on a wire

We add two small, general pieces to the engine.

A `Track` (in `org.physics.engine.track`) is a wire, stored as a chain of closely spaced
points. Given a distance travelled along the wire, it can tell you the position there and which
way the wire points (its tangent).

A `Bead` slides along a track. The trick that makes this simple is that the wire holds the bead
sideways for free: the only part of gravity that does anything is the part pointing along the
wire. So instead of a hard constrained-motion problem, we just take gravity, keep the component
along the tangent, and use it to speed the bead up or slow it down. A bead sliding on a wire
becomes almost the same one-line update as a falling particle. A test confirms the bead obeys
energy conservation: at the bottom its speed is exactly `sqrt(2 g h)` for a drop of height `h`,
whatever the shape of the wire.

## Why the cycloid wins

`Curves` builds three wires between the same two points: a straight ramp, a gently bowed arc,
and a cycloid, the curve traced by a point on the rim of a rolling wheel. Release a bead on each
at the same instant and race them:

- The **straight ramp** comes last, despite being the shortest path.
- The **arc** does better.
- The **cycloid** wins.

The reason is that the cycloid drops steeply right at the start. That early plunge buys the bead
a lot of speed immediately, and it carries that speed across the rest of the journey. Trading
some extra distance for early speed more than pays off. A test verifies the ordering: the
cycloid beats the arc, which beats the straight line.

This is the brachistochrone (Greek for "shortest time"). Johann Bernoulli posed it as a
challenge in 1696, and the answer, the cycloid, was found by Newton, Leibniz, and others. It
launched a whole branch of mathematics, the calculus of variations, which asks not "what is the
best number?" but "what is the best shape or path?".

## The scene

`BrachistochroneScene` runs the race. The three wires are drawn in different colours, with a
bead on each and a running clock for each in the readout. Watch the cycloid bead pull ahead
even though it takes the longest road. Press R to race again.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it, or its number. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.track.TrackTest"
```

## What to take away from this chapter

- A frictionless bead on a wire is driven only by the component of gravity along the wire.
- Its speed depends only on how far it has dropped, never on the path: `v = sqrt(2 g h)`.
- The fastest descent between two points is a cycloid, not a straight line, because dropping
  steeply early banks speed for the whole trip.
- Asking for the best shape, not the best number, is the calculus of variations.
