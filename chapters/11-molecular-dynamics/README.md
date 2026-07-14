# Chapter 11: Molecular dynamics, and where solids, liquids, and gases come from

This chapter has a big payoff. With one force between atoms and a single dial for temperature,
we get solids, liquids, and gases, and the melting between them, without ever writing a line of
code that mentions those words. The states of matter simply emerge. That is molecular dynamics,
and it is close to how real materials are simulated in research today.

## The Lennard-Jones force

Atoms have a curious relationship. Bring two of them a little too far apart and they pull
together; push them a little too close and they shove back, hard. There is one separation they
are content with, and they sit there if left alone. The Lennard-Jones potential captures all of
this in a single expression:

```
V(r) = 4 * eps * ( (sigma/r)^12 - (sigma/r)^6 )
```

Two terms, doing two jobs:

- The `(sigma/r)^6` term is the **attraction**: a gentle pull that reaches out a short way. It
  is why atoms stick together into liquids and solids at all.
- The `(sigma/r)^12` term is the **repulsion**: a ferocious push that switches on when atoms get
  too close, because two atoms cannot occupy the same space. It is why matter has volume and
  does not simply collapse.

They balance at exactly one separation, `r = 2^(1/6) * sigma`, where the pull and the push
cancel and the force is zero. That is the atoms' happy distance. `sigma` sets how far apart that
is, and `eps` sets how strongly they bond. Past a cutoff distance the force is negligible, so we
ignore it, which also keeps the simulation fast.

`LennardJones` (in `org.physics.engine.force`) applies this between every close pair. The tests
nail down each behaviour: the force is zero at `2^(1/6) sigma`, it repels when closer, attracts
when farther (within the cutoff), and vanishes beyond it. One more test releases two atoms near
their happy distance and confirms they stay bound, oscillating around it rather than collapsing
together or drifting apart, which is a molecule in miniature.

There is one small engineering touch: a floor on how strong the repulsion may grow, so a rare
very-close approach cannot produce a near-infinite force and blow the simulation up. It sits
well inside the repulsive wall and does not affect the physics we care about.

## States of matter as emergent behaviour

Here is the beautiful part. Take a few dozen of these atoms and cool them down. Each one settles
at its happy distance from its neighbours, and because that distance is the same for all of
them, they arrange into a regular repeating pattern, a crystal. That is a **solid**: atoms held
in a lattice, jiggling a little but staying put.

Now warm them. The jiggling grows. At some point it is violent enough that atoms start slipping
past their neighbours, the lattice breaks up, and the material flows while still holding
together. That is a **liquid**. Warm them more and the atoms have so much energy that the
attraction can no longer hold them at all; they break free and fly apart. That is a **gas**.

Nothing in the code decides which state we are in. There is only the Lennard-Jones force and the
temperature. Solid, liquid, and gas are just what that one force does at different temperatures.
Melting is not a rule we programmed; it is a thing that happens.

## The scene

`MolecularScene` lays out about a hundred atoms in the hexagonal packing a real crystal favours,
cool enough to hold together as a gently shimmering solid. Press the up arrow to heat it: watch
the shimmer grow, the neat rows start to wander, and eventually the whole thing melt and flow,
then boil into a scattering gas. Press down to cool it back toward a solid. The atoms are
coloured by speed, so hot regions glow. Press R to freeze a fresh crystal.

## Running it

```
./gradlew desktop:run
```

Press 7 for molecular dynamics, then hold the up arrow to melt it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.force.LennardJonesTest"
```

## What to take away from this chapter

- The Lennard-Jones force attracts atoms at a distance and repels them up close, with one happy
  separation where the force is zero.
- That attraction gives matter cohesion; that repulsion gives it volume.
- Cool atoms lock into a crystalline solid; warmer ones flow as a liquid; hot ones scatter as a
  gas.
- These states are emergent: the code has only a force and a temperature, never the states
  themselves.

Next, ch12, the first of two showpieces. We connect particles with constraints instead of
forces and get a sheet of cloth you can grab, swing, and tear.
