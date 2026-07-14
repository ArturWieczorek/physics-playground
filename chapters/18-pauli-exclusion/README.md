# Chapter 18: The Pauli exclusion principle, in three dimensions

This chapter does two firsts at once. It is our first step into quantum mechanics, and our first
scene drawn in three dimensions. The reward is a picture of one of the deepest rules in nature,
the one that gives atoms their structure and makes matter solid: the Pauli exclusion principle.

## Identical particles

In the everyday world you can always tell two things apart, if only by watching which is which.
In quantum mechanics you cannot. Two electrons are not just similar, they are genuinely
identical, and swapping them must leave every real prediction unchanged. The predictions come
from the square of the wavefunction, so a swap may only do one of two things to the wavefunction
itself: leave it exactly as it was, or flip its sign. Both square to the same thing.

Nature uses both options, and they name two families of particles:

- **Symmetric** under a swap: bosons (light's photons, for example). They are happy to pile into
  the same state.
- **Antisymmetric** under a swap (sign flips): fermions. Electrons, protons, and neutrons are all
  fermions.

`TwoElectronState` (in `org.physics.engine.quantum`) builds both, by combining two single-particle
states in the two allowed ways. The tests confirm the defining behaviour: the symmetric one is
unchanged when the electrons are swapped, and the antisymmetric one flips sign.

## The exclusion, and why it matters

Now put the two electrons in the same place, `x1 = x2`, in the antisymmetric case. The two terms
of the combination become identical, and because one is subtracted from the other, they cancel
exactly. The wavefunction is zero. There is no chance at all of finding two electrons in the same
state at the same place. That is the Pauli exclusion principle, and a test checks it directly: the
antisymmetric state is exactly zero everywhere on the diagonal.

It is hard to overstate how much this one rule does. It forces the electrons in an atom to stack
into shells instead of all collapsing into the lowest one, which gives every element its distinct
chemistry. It is what makes solid matter take up space and resist being squeezed. Without it,
there would be no chemistry and no ordinary matter at all.

## The scene, in 3D

`PauliScene` plots the wavefunction as a landscape. The two electron positions run along the flat
ground, and the height of the surface is the value of the wavefunction there. It is drawn as a
coloured wireframe under a perspective camera, so this is the first scene you can orbit in three
dimensions: drag to rotate, and it drifts slowly on its own when left alone.

Press space to switch between the two combinations:

- **Symmetric (boson):** a smooth hill, high along the diagonal, where both particles happily sit
  near the same place.
- **Antisymmetric (fermion):** a sharp trench cut clean along the diagonal, pinned to zero. That
  trench is the exclusion principle, made visible. Two electrons simply cannot be there.

The surface is coloured from cool blue at its lowest to hot red at its highest.

Note on the 3D drawing: rather than a full lit 3D engine, this reuses the same shape renderer as
every other scene, just handed a perspective camera and its three-dimensional line calls. That
keeps it light and consistent with the rest of the playground.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.quantum.TwoElectronStateTest"
```

## What to take away from this chapter

- Identical quantum particles must leave predictions unchanged when swapped, so the wavefunction
  either stays the same (bosons) or flips sign (fermions).
- Electrons are fermions, and their antisymmetric wavefunction is forced to zero whenever two of
  them would coincide.
- That is the Pauli exclusion principle, and it is the reason atoms have shells and matter is
  solid.
- The same shape renderer, given a perspective camera, is enough to draw and orbit a surface in
  three dimensions.
