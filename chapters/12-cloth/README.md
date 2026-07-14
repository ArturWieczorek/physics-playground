# Chapter 12: Cloth you can tear

This is the first of two showpieces, the kind of thing that makes people lean in. It is a sheet
of cloth that hangs, sways, folds, and rips when you pull it too hard. And the surprise is how
little new physics it needs. It is built almost entirely from an idea we already have,
constraints, taken to its logical end.

## A different way to move: Verlet

Everywhere else in the engine a particle stores a velocity. Cloth points do not. Instead each
point remembers where it was on the previous step, and its velocity is simply the gap between
where it is now and where it was then. This is Verlet integration, and it seems like a strange
bookkeeping choice until you see what it buys you.

Because velocity is implied by the two positions, you can grab a point and move it wherever you
like, and its velocity updates itself automatically from the new gap. That is the key. It means
we can enforce the cloth's shape purely by moving points around, and the motion takes care of
itself.

## Threads as constraints

A `ClothPoint` is one node; a `Stick` is a thread joining two of them, with a rest length it
wants to keep. A `Cloth` is a grid of points laced together with threads running across and down.

Each step has two phases:

1. **Move.** Carry every free point forward by however far it moved last step (that is its
   Verlet velocity), plus a small nudge from gravity.
2. **Relax.** Walk over all the threads several times. For each one, look at how far apart its
   two ends are, and nudge them back toward the rest length: closer if stretched, apart if
   squashed. A pinned end does not move, so a free end takes the whole correction; two free ends
   split it evenly.

That relax step is the trick that makes stiff cloth without stiff springs. A spring stiff enough
to look like cloth would explode our integrator. Moving the points directly, a few passes per
frame, gives the same look and never blows up. More passes make firmer cloth.

The tests check the solver in isolation: a stretched thread relaxes back to its rest length; two
free ends meet in the middle so their midpoint stays put (equal and opposite, once again); and a
pinned top edge lets the rest hang below it and stay stable for hundreds of steps without any
value running off to infinity.

## Tearing

Real cloth rips when pulled too hard, and ours does too, with almost no extra code. Each thread
has a tear length. During the relax step, if a thread is found stretched past that length, it
simply marks itself broken and stops pulling. A broken thread is skipped from then on. Yank a
fold of the cloth and the local threads pass their limit one by one, and a rip opens and spreads.
The test `overstretchedThreadTears` confirms a thread stretched past its limit snaps.

## The scene

`ClothScene` hangs a thirty-by-twenty sheet from its pinned top edge. It has two modes, switched
with the space bar:

- **Grab mode** (the default): drag to grab the nearest bit of cloth and pull it around. Swing
  it, bunch it up, or haul a corner across the screen. Pull hard enough and it tears.
- **Cut mode**: drag like a pair of scissors to slice threads wherever the pointer passes.

The threads are coloured by strain, calm blue where the cloth is relaxed and warming toward red
where it is stretched taut, so you can see the tension gather just before something rips. Press R
to hang a fresh sheet. The scene title shows which mode you are in.

## Running it

```
./gradlew desktop:run
```

Press 8 for the cloth. Space switches grab and cut; drag to interact. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.cloth.ClothTest"
```

## What to take away from this chapter

- Verlet integration stores a point's previous position instead of its velocity, so moving a
  point updates its velocity for free.
- Cloth is a grid of points joined by length constraints; we enforce shape by nudging points, not
  by applying forces.
- Relaxing the constraints a few times per step gives stiff, stable cloth cheaply.
- Threads that stretch past a limit break, so the cloth tears where the strain is highest.

Next, ch13, the second showpiece, and the most ambitious simulation in the course: a pool of
liquid you can splash and stir, where the fluid itself is made of particles.
