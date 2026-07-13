# Chapter 4: Forces and Newton's second law

Until now we have been feeding acceleration to the integrator by hand. Real physics does not
work that way. In the real world we have forces, pushes and pulls, and the motion follows
from them. This chapter turns that around: we add up the forces on each particle, and let one
famous law do the rest.

## Newton's second law

The law is short:

```
force = mass * acceleration        F = m a
```

Rearranged, it tells us how a thing moves when pushed:

```
acceleration = force / mass        a = F / m
```

Read it out loud and it matches your intuition. The harder you push (more force), the more
something accelerates. The heavier it is (more mass), the less it accelerates for the same
push. Shoving a shopping trolley and shoving a car with the same effort gives very different
results, and the difference is their mass.

A particle now carries a running force total. During a step we:

1. reset that total to zero,
2. let each force add its share,
3. divide the total by the mass to get acceleration, and move.

The `Particle` gained four small methods for this: `resetForce`, `addForce`, `force`, and
`acceleration` (which is just `force / mass`).

## A force is a thing you can add

We introduce a `Force` (in `org.physics.engine.force`): anything that pushes on the
particles. It has one job, `apply`, where it adds its contribution to the bodies' force
totals.

Notice `apply` receives the whole list of particles, not just one. That is deliberate. Some
forces act on each body on its own (gravity near the ground). Others depend on pairs or
groups (the pull between two planets, which we build in ch08). Handing over the whole list
lets both kinds fit the same simple shape.

Our first force is `UniformGravity`: a constant downward pull. The force on a body is its
mass times the gravitational acceleration, `F = m g`.

## The World

`World` (in `org.physics.engine.core`) ties it together. It holds the particles and the
forces, and its `step(dt)` runs the same three beats we described:

```java
for (Particle body : bodies) body.resetForce();      // 1. forget last step
for (Force force : forces)   force.apply(bodies);     // 2. add up the pushes
for (Particle body : bodies) {                        // 3. move each body
  Vector2 a = body.acceleration();                    //    a = F / m
  Vector2 v = body.velocity().add(a.scale(dt));       //    semi-implicit Euler
  body.setVelocity(v);
  body.setPosition(body.position().add(v.scale(dt)));
}
```

The move is semi-implicit Euler, the stable workhorse we settled on in ch03. Every scene from
here on is really just a `World` with some particles and forces poured into it.

## The tests, and a lovely consequence

`WorldTest` checks the law from a few angles:

- A 10 newton push on a 2 kilogram body gives an acceleration of exactly 5. That is `a = F/m`
  with nothing else going on.
- Under the same push, a 1 kilogram body ends up moving four times as fast as a 4 kilogram
  one. More mass, less motion, in exact inverse proportion.
- Under gravity, a 0.01 kilogram feather and a 100 kilogram hammer fall *exactly* together.
  This is the surprising one, and it falls straight out of the maths: gravity's force is
  proportional to mass (`F = m g`), but acceleration divides by mass again (`a = F/m`), so the
  mass cancels and everything accelerates the same. Drop a hammer and a feather with no air,
  as the Apollo 15 crew did on the Moon, and they land together.
- A body dropped for one second reaches a speed of `g` times one second, to the digit.

Run them:

```
./gradlew :engine:test --tests "org.physics.engine.core.WorldTest"
```

## What to take away from this chapter

- Motion comes from forces through Newton's second law, `a = F / m`.
- A particle sums the forces on it each step; the World resets, applies forces, then moves.
- Forces see all the bodies, so both per-body and pair forces share one shape.
- Because gravity's force grows with mass but acceleration divides by it, everything falls at
  the same rate. The tests prove it.

Next, ch05, we add our first force that pushes back harder the more you disturb it, a spring,
and finally put something on the screen: the app grows its first visible scene.
