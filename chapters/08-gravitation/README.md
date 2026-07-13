# Chapter 8: Gravitation and orbits

In ch04 gravity was a flat downward pull, the same everywhere. That is only the local view,
what you feel standing on a huge planet. The real law is grander: every mass in the universe
pulls on every other. This chapter builds that law and uses it to make things orbit, tracing
the same ellipses that planets have followed for billions of years.

## Newton's law of universal gravitation

Any two masses attract each other along the line joining them. The pull is stronger for
heavier masses and much weaker as they get farther apart:

```
force = G * m1 * m2 / distance^2
```

`G` is the gravitational constant, a fixed number that sets the overall strength. The crucial
part is dividing by distance squared. This is an inverse-square law, and it means the pull
fades fast: move twice as far away and the force drops not to a half but to a quarter. Our test
`pullFollowsInverseSquare` checks exactly that, and `inverseSquareForce` checks the full formula
and that the two bodies feel equal and opposite pulls (Newton's third law again).

`Gravitation` (in `org.physics.engine.force`) applies this between every pair of bodies. It
adds one practical touch: a small "softening" distance so that if two bodies pass extremely
close, the force stays finite instead of exploding to infinity and flinging them off the
screen. Ideal point masses would not need it, but it keeps the simulation stable.

## Why things orbit

An orbit is one of those ideas that sounds hard and is actually simple once you see it. A
planet is not held up by anything. It is falling toward the star, constantly. The trick is that
it is also moving sideways fast enough that, by the time it has fallen a bit, it has also moved
along, and the star is now in a slightly different direction. It keeps falling and keeps
missing. That perpetual falling-and-missing is an orbit.

There is a special sideways speed for which the falling exactly balances the sideways motion
and the path is a perfect circle:

```
circular speed = sqrt(G * M / radius)
```

Go slower and the orbit is an ellipse that dips closer on one side; go faster and it stretches
into a longer ellipse; faster still and the body escapes altogether. All of these come straight
out of the same inverse-square law. Kepler observed the ellipses a lifetime before Newton
explained them with this one equation.

## Conservation, again

Two quantities stay fixed as a body orbits, and they are worth watching for:

- **Angular momentum**, position "crossed" with velocity, times mass. It stays constant, which
  is why a body speeds up as it swings in close and slows down as it drifts out far, sweeping
  equal areas in equal times. That is Kepler's second law.
- **Energy**, kinetic plus gravitational. Also constant for an undisturbed orbit.

The test `circularOrbitIsStable` gives a body the circular speed, runs it for a full period,
and checks three things: the radius barely wanders (it stays a circle), the angular momentum is
unchanged, and the body returns to where it began. The stability comes for free from the
semi-implicit Euler integrator we chose back in ch03, which is exactly the kind of method that
keeps orbits from spiralling away.

## The scene

`OrbitScene` puts a heavy star at the centre and starts three planets circling it at different
radii. Each planet trails a fading line so you can see the shape of its path. Click anywhere to
drop a new planet; it launches at the circular speed for that distance and settles into its own
ring. Add several and nudge them near each other to watch their paths bend as they tug on one
another. Press R to return to the starting system.

## Running it

```
./gradlew desktop:run
```

Press 4 for the orbits. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.force.GravitationTest"
```

## What to take away from this chapter

- Every mass attracts every other with a force that falls off as the square of the distance.
- An orbit is continual falling combined with enough sideways speed to keep missing.
- There is a circular speed, sqrt(G M / r); slower or faster gives ellipses or escape.
- Angular momentum and energy are conserved, giving Kepler's laws and stable orbits.

Next, ch09, we meet a force with the same inverse-square shape but a decisive twist: it can
push as well as pull. Electric charges, and the invisible fields around them.
