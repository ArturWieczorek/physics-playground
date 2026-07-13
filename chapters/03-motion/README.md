# Chapter 3: Motion

In ch00 we said a simulation nudges everything forward by a tiny time step, over and over.
Now we build the thing that does the nudging. It turns out there is more than one way to do
it, and the choice has real consequences. By the end of this chapter you will have watched
one method invent energy out of nowhere and another stay honest, all proven by a test.

## The particle

First we need something to move. A `Particle` (in `org.physics.engine.core`) is a point with
three things:

- a **position** (where it is), a `Vector2`
- a **velocity** (how fast and which way it is going), a `Vector2`
- a **mass** (how heavy it is), a number

Unlike our vectors, a particle is mutable: the whole point of a simulation is to change its
position and velocity on every step. It also knows its kinetic energy, one half m v squared,
which we will use to check our work.

## Integration: adding up tiny steps

Moving a particle forward means answering the two questions from ch00 in order:

```
how does velocity change?   ->   velocity += acceleration * dt
how does position change?   ->   position += velocity * dt
```

Doing this repeatedly is called numerical integration. An `Integrator` is an object that
knows one particular recipe for it. We give it a particle, a way to find the acceleration
(an `AccelerationField`), and the time step `dt`, and it advances the particle once.

Why hand it an `AccelerationField` rather than a fixed acceleration? Because forces usually
depend on where things are. A spring pulls harder the further you stretch it; gravity from a
planet is stronger when you are closer. The field lets a smart integrator re-check the
acceleration after it moves, which is what makes the good ones good.

## Three recipes

We build three, from worst to best, because seeing the worst is how you understand why the
others exist.

**Explicit Euler.** The obvious one. Move using the current velocity, then update the
velocity:

```
position += velocity * dt
velocity += acceleration * dt
```

It reads perfectly and it is subtly wrong. On anything that swings back and forth, it slowly
pumps in energy. A spring winds itself up; an orbit spirals outward. It never settles.

**Semi-implicit Euler.** Swap the two lines. Update the velocity first, then move using that
just-updated velocity:

```
velocity += acceleration * dt
position += velocity * dt
```

That one swap is the difference between a simulation that blows up and one that behaves. The
energy no longer grows without limit; it wobbles a little and stays put. This is our
workhorse for most of the course. Physicists call it symplectic, which just means it respects
the long-term energy budget.

**Velocity Verlet.** Spend a little more to get real accuracy. Move using velocity and
acceleration, then look at the acceleration *again* at the new spot, and update velocity with
the average of the two:

```
position += velocity * dt + 0.5 * a_old * dt*dt
a_new     = acceleration at the new position
velocity += 0.5 * (a_old + a_new) * dt
```

Checking the acceleration twice per step costs more, but for a steady force such as gravity
it is exact. We reach for it when accuracy matters, like orbits (ch08) and molecules (ch11).

## The test that proves it

The interesting test in `IntegratorTest` sets up an ideal spring: a unit mass pulled toward
the origin with a force equal to its displacement, so acceleration is `-position`. Left
alone, such a spring bounces forever with a perfectly fixed total energy. We run it for about
sixteen full swings and measure how much the energy changed:

- **Explicit Euler** gains more than half its energy again. The test asserts the drift is
  large, on purpose, to document the flaw.
- **Semi-implicit Euler** stays within a few percent. The test asserts the drift is under 10
  percent.
- **Velocity Verlet** barely moves. The test asserts under 1 percent.

There is also a test that velocity Verlet reproduces the schoolbook drop `0.5 * g * t^2` for
constant gravity, exactly, and one that every method coasts in a straight line when no force
acts.

Run them:

```
./gradlew :engine:test --tests "org.physics.engine.integrate.IntegratorTest"
```

## What to take away from this chapter

- A particle is a position, a velocity, and a mass; the simulation changes the first two.
- Integration is adding up tiny steps; force updates velocity, velocity updates position.
- The order of those two updates matters: explicit Euler drifts, semi-implicit Euler is
  stable, velocity Verlet is stable and accurate.
- We do not take this on faith. A test measures energy over time and holds each method to the
  behaviour we expect.

Next, ch04, we stop hand-feeding acceleration and start with forces. We add up the forces on
a particle, divide by its mass (Newton's second law), and let a small world step everything
together.
