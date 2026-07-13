# Chapter 6: Collisions, momentum, and energy

So far our particles have ignored each other and floated through the walls. This chapter makes
them solid: they bounce off the edges of the box and off one another. Doing that correctly
leads us to two of the deepest ideas in physics, the conservation of momentum and of energy.

## Forces push over time, constraints fix things now

A collision is not really a force you apply gently over many steps. It happens in an instant:
one moment the balls are approaching, the next they are moving apart. So we handle it
differently. After the world has applied its forces and moved everything, it runs a list of
**constraints**, each of which reaches in and fixes what just went wrong: a ball that has
sunk into a wall, or two balls that now overlap.

We added a `Constraint` (in `org.physics.engine.collide`) and taught the `World` to resolve
its constraints at the end of each step, right after moving the particles. The same idea
returns in ch12, where the threads holding a piece of cloth together are constraints too.

To collide, particles also needed a size. `Particle` gained a radius, and an inverse mass:
one divided by the mass, with a pinned particle counting as infinitely heavy (inverse mass
zero). Collision maths is written in terms of inverse mass because the real question is "how
much does a given kick move this thing?", and an immovable wall is just something a kick does
not move at all.

## Bouncing off walls

`BoxBounds` keeps every particle inside a rectangle. If a ball has pushed past a wall, we set
it back against the wall and flip the velocity component pointing into it. The bounce keeps a
fraction of the speed, the restitution: 1 is a perfect bounce, 0 stops dead against the wall,
and values between are the everyday case.

## Bouncing off each other

`ParticleCollisions` is the heart of the chapter. When two balls overlap it does two things:

1. **Separate them.** Push them apart along the line joining their centres until they only
   just touch, sharing the move by inverse mass so the lighter one moves more and an immovable
   one not at all.
2. **Bounce them.** Apply an impulse, a sudden kick, along that same line. The size of the
   kick is chosen to obey the laws below.

The formula for the impulse looks compact but every piece earns its place:

```
closingSpeed = (velocityB - velocityA) . normal          how fast they are coming together
impulse      = -(1 + restitution) * closingSpeed / (invMassA + invMassB)
velocityA   -= impulse * invMassA                          heavier A changes less
velocityB   += impulse * invMassB
```

The two ends get equal and opposite kicks, which is Newton's third law again, and it is
exactly why momentum comes out conserved.

## The two conservation laws

These are laws the tests check directly, because they are the real proof that our collisions
are honest.

- **Momentum is conserved, always.** Momentum is mass times velocity, added up over all the
  balls. Because the collision gives equal and opposite kicks, the total is identical before
  and after, whether the bounce is springy or dead. `momentumIsConserved` checks this for two
  unequal masses.
- **Kinetic energy is conserved only when the bounce is perfect.** With a restitution of 1 the
  collision is elastic and the total energy of motion is unchanged, as `elasticCollision-
  ConservesEnergy` shows. Turn the restitution down and some energy is lost to the "squish";
  `inelasticCollisionLosesEnergy` confirms momentum still balances but energy drops.

There is also the classic result that two equal masses in a head-on elastic hit simply swap
velocities. The mover stops dead and the target shoots off with the incoming speed, exactly as
in a Newton's cradle. That is `equalMassesSwapVelocities`.

## The scene, and a menu

`CollisionScene` sets up a break shot: a triangular rack of resting balls and a cue ball fired
into them. With no gravity and perfect bounces, the balls scatter and keep going, trading
momentum at every hit and rebounding off the walls. Click to fire the cue ball toward your
pointer; press R to rack up again.

The app now holds more than one scene, so it grew a small menu. The number keys switch
between scenes, and a line of text at the top names the current one and lists the controls.
Every chapter from here adds another scene to that list.

## Running it

```
./gradlew desktop:run
```

Press 1 for the spring, 2 for the collisions. To run this chapter's tests:

```
./gradlew :engine:test --tests "org.physics.engine.collide.CollisionsTest"
```

## What to take away from this chapter

- Collisions are resolved as constraints, after movement, not as gentle forces.
- Particles have a radius and an inverse mass; an immovable object has inverse mass zero.
- Walls reflect the velocity into them, scaled by the restitution.
- A collision separates the balls, then applies equal and opposite impulses.
- Momentum is always conserved; kinetic energy is conserved only in a perfectly elastic hit.

Next, ch07, we scale this up. Fill the box with hundreds of tiny particles and it becomes a
gas, and out of all that random bouncing comes temperature, pressure, and a famous bell-shaped
curve of speeds.
