# Chapter 5: Springs, and the first thing on screen

This chapter does two things. It adds our first force that reacts to what you do to it, a
spring, and it finally puts something on the screen. From here on, most chapters end with a
scene you can watch and play with.

## Hooke's law

A spring wants to be a certain length, its rest length. Pull it longer and it pulls back;
push it shorter and it pushes back. How hard? In proportion to how far you have moved it from
rest. That is Hooke's law:

```
force = stiffness * (currentLength - restLength)
```

`stiffness` (often written `k`) is how stiff the spring is. A stiff spring fights small
stretches hard; a floppy one barely notices. The sign takes care of itself: stretch it and
the force pulls the ends together, compress it and the force pushes them apart.

Real springs also lose energy, or a plucked one would ring forever. We add a damping term
that resists the ends moving apart or together, in proportion to how fast they do so. A
little damping and the motion calms down; none, and it swings forever.

Our `Spring` (in `org.physics.engine.force`) connects two particles and applies equal and
opposite forces to them, which is Newton's third law: every push has an equal push back the
other way.

## Simple harmonic motion

Attach a mass to a spring, pull it, and let go. It oscillates: out, back, out, back, with a
steady rhythm. The time for one full swing is the period, and for an ideal spring it depends
only on the mass and the stiffness, not on how far you pulled it:

```
period = 2 * pi * sqrt(mass / stiffness)
```

Heavier mass, slower swing. Stiffer spring, faster swing. This is called simple harmonic
motion, and it is everywhere in physics, from a child on a swing to the vibration of atoms in
ch11.

The test `oscillatesWithTheExpectedPeriod` proves our spring obeys this. It anchors a mass to
a fixed point with a rest length of zero (so the pull is exactly proportional to
displacement), runs the world for one full period from the formula above, and checks the mass
has returned to its starting point at rest. Another test confirms damping bleeds the energy
away.

## Pinning things in place

To anchor a spring we need a point that does not move. We taught `Particle` a new trick: it
can be pinned. A pinned particle still feels forces, but the `World` never moves it. We use it
for the anchor here, for holding the bob while you drag it, and later for the corners a sheet
of cloth hangs from (ch12).

## The scene

`SpringScene` (in `org.physics.app.scene`) is our first `Scene`. A `Scene` is one experiment
the app can show; they all share a small shape: set up, update, draw, and handle the pointer.
Notice how little the scene does. It places an anchor and a bob, adds gravity and a spring,
and on each frame it steps the world and draws two dots and a line. Every bit of real physics
lives in the engine we already tested. That division, tested physics and thin drawing, is the
habit for every scene to come.

Two details worth seeing:

- **Fixed time step.** Frames do not arrive at a steady rate, and physics hates uneven steps.
  So the scene collects the real elapsed time and steps the world in fixed small slices of
  1/120 of a second, saving any remainder for next frame. The simulation stays stable whether
  the machine runs fast or slow.
- **Dragging reuses pinning.** When you grab the bob, the scene pins it and moves it to the
  pointer, so the spring cannot fight your hand. Let go and it unpins and springs away. No new
  machinery, just the pin from the engine.

## Running it

```
./gradlew desktop:run
```

A window opens with a weight hanging from a spring. Drag it with the mouse and release to set
it swinging and bobbing. Press R to reset. To run the physics tests for this chapter:

```
./gradlew :engine:test --tests "org.physics.engine.force.SpringTest"
```

## What to take away from this chapter

- Hooke's law: a spring's force grows in proportion to how far it is stretched or squashed.
- A mass on a spring swings with a period set only by the mass and the stiffness.
- Damping removes energy so motion settles.
- A scene is thin: it arranges bodies and forces and draws them; the engine does the physics.
- A fixed time step keeps the simulation stable regardless of frame rate.

Next, ch06, things start bumping into each other. We add collisions with the walls and
between particles, and watch momentum and energy be conserved.
