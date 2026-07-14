# Chapter 13: Fluid you can splash

The grand finale. This is a pool of liquid you can stir and splash, and it is made of nothing
but particles. There is no water surface in the code, no waves, no "liquid" object. There are
only particles that each look at their neighbours and decide how to move, and from that a fluid
appears. The technique is smoothed-particle hydrodynamics, SPH, and it is close to how films and
engineers simulate real water.

## The idea: a fluid made of particles

A single particle is just a dot; it is not wet, not dense, not flowing. Those are properties of
a crowd. SPH's trick is to compute everything as a smooth blur over the particles nearby. "How
dense is the fluid here?" means "how many particles, weighted by closeness, are near this spot?"
That weighting is done with a kernel, a little bump-shaped function that counts close neighbours
fully and fades to nothing at a radius. `Kernels` (in `org.physics.engine.fluid`) provides three
standard ones, each shaped for its job, and the tests confirm they peak at the centre and vanish
at the edge.

## Three passes per step

`Sph` runs each step in three passes, and each one is an idea we have met before:

1. **Density.** For every particle, blur its neighbours' masses with the poly6 kernel. Crowded
   particles come out dense, lonely ones sparse. This is the same "average over neighbours" move
   we used for temperature in ch07. A test confirms a clustered particle reads denser than a far
   one, and a lone particle's density is exactly its own mass times the kernel.
2. **Pressure.** Turn density into pressure with a simple rule: the denser than its preferred
   value the fluid is, the higher the pressure. Where particles are crammed together, pressure is
   high.
3. **Forces.** From those pressures, push each particle away from its crowded neighbours (that is
   what makes the fluid incompressible-ish, resisting being squashed), drag it toward its
   neighbours' velocity (viscosity, the gooeyness), and add gravity. Then move everything with the
   trusty semi-implicit Euler step from ch03.

Both the pressure and viscosity forces are written in their symmetric form, so each pair of
particles pushes on the other equally and oppositely, Newton's third law once more. That is why
the fluid conserves momentum, which the test `momentumIsConserved` checks by running a step with
no gravity and confirming the total is unchanged. For the walls we simply reuse the `BoxBounds`
from ch06.

## Emergence, again

Notice the theme that has run through the last few chapters. We never program the behaviour we
want to see. We program a local rule, what each particle does based on its neighbours, and the
behaviour emerges. The gas found its bell curve, the atoms melted, and now the fluid finds its
own flat surface, sloshes when disturbed, and forms a splash, none of which appears as a concept
anywhere in the code. That is the deep lesson of the whole course: rich behaviour comes from
simple rules applied to many pieces.

## The scene

`FluidScene` drops a block of several hundred fluid particles and lets it fall and settle. It
finds its own level, flat under gravity, and when you drag the mouse through it you stir and
splash it, with the disturbance rippling outward and settling again. Particles are coloured by
speed, deep blue where the water is calm and bright where it splashes. Press R to drop a fresh
block.

Two practical notes, honest about the engineering. SPH is stiff, so the scene takes many small
steps per frame for stability. And there is a cap on how fast any particle may move, a safety net
so a rare numerical spike cannot fling a droplet off the screen; it almost never triggers in
normal sloshing.

## Running it

```
./gradlew desktop:run
```

Press 9 for the fluid, then drag through it to splash. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.fluid.SphTest"
```

## What to take away from this chapter

- In SPH a fluid is just particles; its properties are smooth blurs over the neighbours nearby.
- Each step measures density, turns it into pressure, then pushes crowded particles apart and
  drags neighbours together, plus gravity.
- Symmetric forces mean equal and opposite pairs, so momentum is conserved.
- A flat surface, waves, and splashes emerge from the local rule; none of them is written down.

Next, ch14, the last chapter, we ship it: the browser build that runs on WebGL, the packaged
desktop app, and publishing the playground online so anyone can try it.
