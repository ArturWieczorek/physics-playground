# Chapter 9: Electric charges and fields

Gravity from ch08 only ever pulls, because mass is never negative. This chapter introduces a
force with the very same inverse-square shape but a decisive new twist: it can push as well as
pull. That force is electricity, and following it leads us to one of the most important ideas
in all of physics, the field.

## Coulomb's law

Charged objects attract or repel with a force that looks almost identical to gravity:

```
force = k * q1 * q2 / distance^2
```

Same inverse square, same "along the line between them". The difference is entirely in the
charges `q`, which carry a sign:

- Two like charges (both positive, or both negative) give a positive product, and the force
  pushes them apart. Like charges repel.
- A positive and a negative give a negative product, and the force pulls them together.
  Opposite charges attract.

`Coulomb` (in `org.physics.engine.force`) applies this between every pair, reading each
particle's new `charge`. The tests confirm both behaviours and the exact `k*q1*q2/r^2`
magnitude, and that the two charges feel equal and opposite forces, just like every other force
we have built.

That one extra sign changes the world. Because charge comes in two kinds that cancel, matter
can be electrically neutral overall while still being held together by these forces up close.
Gravity can only add up; electricity can balance.

## The field: how a charge reaches across empty space

Here is a puzzle. How does one charge push another that is not touching it, across empty
space? The answer physicists settled on is the field. A charge does not act on distant objects
directly. Instead it fills the space around itself with an electric field, and any other charge
simply responds to the field it sits in. The field is the middleman, and it is real: it carries
energy and, when it changes, it carries light (a thread we pick up in ch10).

`ElectricField` (in `org.physics.engine.field`) lets us look at the field itself, apart from
any particle feeling it. It gives two things at any point:

- The **field vector**: which way, and how hard, a tiny positive test charge would be pushed
  there. It points away from positive charges and toward negative ones. The test
  `fieldDirectionFollowsSign` checks exactly that, and `fieldMagnitudeFromSingleCharge` checks
  its size is `k*q/r^2`.
- The **potential**: a single number, the electrical "height" of a point. Charges roll downhill
  in potential the way balls roll downhill on a landscape. Halfway between an equal positive and
  negative charge the two contributions cancel exactly, which the test
  `potentialCancelsOnTheMidline` confirms.

## The scene

`ChargesScene` makes the invisible field visible. Two fixed source charges, a positive (red)
and a negative (blue), set up a field drawn as a grid of little arrows: each arrow is the
direction a positive charge would be pushed at that spot, brighter where the field is stronger.
The arrows sweep out of the red charge and curl into the blue one, the classic dipole picture.

Scattered around are free test charges that actually feel the field and drift along it, away
from the positive source and toward the negative. Click anywhere to pin a new source charge;
its sign alternates each click, red then blue, so you can build your own arrangement and watch
the whole field and all the drifting charges rearrange around it. Press R to reset.

## Running it

```
./gradlew desktop:run
```

Press 5 for the charges. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.field.ElectrostaticsTest"
```

## What to take away from this chapter

- Coulomb's law has gravity's inverse-square shape, but charge has a sign, so it repels as well
  as attracts.
- Like charges repel, opposite charges attract; that balance is why neutral matter holds
  together.
- A charge acts through a field it creates in the space around it, not directly at a distance.
- The field points away from positive and toward negative charges; the potential is its height,
  and charges roll downhill.

Next, ch10, we add motion and magnetism. A charge moving through a magnetic field feels a
sideways push, the Lorentz force, and instead of falling in a line it spirals.
