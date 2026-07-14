# Chapter 19: Electron orbitals, the shapes of atoms

We close the course where chemistry begins: the shapes of the electron clouds around an atom. You
have almost certainly seen the pictures, the round s orbital, the dumbbell p, the cloverleaf d.
This chapter builds them honestly, from the wavefunction, and lets you turn them in 3D.

## An orbital is a cloud, not an orbit

The word "orbital" is a leftover from an older, wrong picture in which electrons circled the
nucleus like planets. They do not. An electron has no path and no definite position at all. The
wavefunction gives only the odds: its square, at each point in space, is how likely the electron
is to be found there. An orbital is the shape of that probability.

So the honest way to draw one is not a solid surface but a cloud of dots: scatter many points,
crowding them where the electron is likely to be and thinning them where it is not. Squint at the
cloud and the familiar shape appears. This is exactly how the pictures in chemistry books are
made.

## The orbitals

`HydrogenOrbital` (in `org.physics.engine.quantum`) gives the wavefunction of a hydrogen atom for
a handful of orbitals, as a function of position. We drop the normalising constants, because only
the relative density matters when scattering dots, and keep what gives each its shape:

- **1s**: `exp(-r)`. A single round ball, densest at the nucleus. The tests confirm it depends
  only on distance, so it is perfectly spherical.
- **2s**: `(2 - r) exp(-r/2)`. A ball inside a shell, with a spherical gap between them where the
  wavefunction passes through zero. The test checks it flips sign across that node at `r = 2`.
- **2p**: a dumbbell, two lobes of opposite sign with a flat node through the middle. The test
  checks the 2p_z orbital is zero everywhere in the xy plane and opposite in sign above and below.
- **3d**: the four-lobed clover and the z-axis-plus-ring shapes, checked for their lobe structure.

## Sampling the cloud

`OrbitalScene` turns density into dots by rejection sampling, a beautifully simple idea: propose a
random point, and keep it with a probability equal to the density there divided by the largest
density anywhere. Points in likely regions almost always survive; points in empty regions almost
never do. Do this thousands of times and the survivors are distributed exactly like the electron.
Peaked orbitals such as 1s reject most proposals, so the sampler is given a large budget; it runs
only when you switch orbitals, not every frame.

Each dot is coloured by the sign of the wavefunction (its phase) where it sits, warm for positive
and cool for negative. The phase does not change the probability, but showing it makes the two
lobes of a p orbital, and the alternating lobes of a d, immediately clear.

## The scene

Drag to orbit the cloud in 3D; it drifts slowly on its own. Use the left and right arrows to step
through the orbitals and watch the shape change: the ball of 1s, the shell of 2s, the dumbbell of
2p, the clover of 3d. A small cross marks the nucleus at the centre. As with ch18, this is drawn
with the ordinary shape renderer under a perspective camera, here using its point primitive.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.quantum.HydrogenOrbitalTest"
```

## What to take away from this chapter

- An orbital is not a path; it is the cloud of places an electron might be, the square of the
  wavefunction.
- The s, p, and d shapes come straight out of the hydrogen wavefunctions, nodes and lobes and all.
- Rejection sampling turns any probability density into a representative cloud of points.
- Colouring by the wavefunction's sign reveals the phase, which is why the lobes show up so
  clearly.

And that is the whole journey: from a single dot falling under gravity in ch00, through forces,
collisions, gases, orbits, fields, chaos, and light, to the quantum clouds that give atoms their
shape. Every step is plain, tested Java that you can read and change.
