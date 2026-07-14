# Chapter 10: Magnetism and the Lorentz force

In ch09 charges felt each other whether they moved or not. Magnetism is different: it acts only
on charges that are moving, and it pushes them sideways. That single sideways push is enough to
send a particle spiralling, and it is the principle behind everything from the aurora to a
particle accelerator.

## The Lorentz force

The complete force on a charge in electric and magnetic fields is the Lorentz force:

```
force = q * (E + v x B)
```

The first part, `q E`, is the electric push from ch09; it acts on any charge, moving or still.
The second part, `q (v x B)`, is the magnetic one, and it has two strange and important
features:

- It only acts on a **moving** charge. A charge sitting still feels nothing magnetic. Our test
  `electricPartActsAtRest` shows a resting charge feels only the electric part.
- It is always **perpendicular** to the velocity. The `v x B` (a cross product) comes out at
  right angles to `v`. The test `magneticForceIsPerpendicular` confirms the force and velocity
  are exactly perpendicular, and `magneticForceMagnitude` checks its size is `|q| v B`.

## Why a sideways push means circles

A force perpendicular to motion can never speed a thing up or slow it down, because it never
pushes along the direction of travel. It can only change the direction. A constant push that is
always sideways bends the path at a steady rate, and a steady bend is a circle. This is
cyclotron motion, and the circle's radius is

```
radius = m v / (q B)
```

Heavier or faster charges make bigger circles; stronger fields make tighter ones. Crucially,
the sign of the charge decides which way it turns, so positive and negative charges circle in
opposite directions. That is precisely how detectors identify particles: a curled track
bending left is one sign, bending right is the other.

Because we are drawing on a flat screen, we treat the magnetic field as pointing straight in or
out of it, so it is a single number. The cross product then turns a velocity `(vx, vy)` into
`(vy*B, -vx*B)`, always at right angles. The test `chargeMovesInACircle` launches a charge in a
pure magnetic field and confirms it stays on a circle of the predicted radius and keeps a
constant speed for a full loop, exactly as a magnetic force (which does no work) requires.

## The scene

`MagnetismScene` fills the screen with a uniform magnetic field coming out of it, and lets you
fire charges into it. Each one curls into a circle and leaves a trail so you can see the loop.
Click to launch a charge, alternating positive (red) and negative (blue), and watch the two
signs curl in opposite directions.

The arrow keys change the fields live:

- **Left and right** weaken and strengthen the magnetic field. The circles grow and shrink, and
  when the field passes through zero and reverses, they change their direction of curl.
- **Up and down** add a sideways electric field. Now the charge is pushed steadily to one side
  while it circles, so the neat circle stretches into a drifting series of loops, a cycloid.
  This combined "E cross B drift" is how charged particles slide along in real devices.

Press R to clear the screen and start again.

## Running it

```
./gradlew desktop:run
```

Press 6 for magnetism. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.force.LorentzForceTest"
```

## What to take away from this chapter

- The Lorentz force is `q(E + v x B)`: an electric push plus a magnetic one.
- The magnetic part acts only on moving charges and always pushes perpendicular to the motion.
- A perpetual sideways push bends the path into a circle of radius `m v / (q B)`.
- Opposite charges circle opposite ways; adding an electric field makes them drift.

Next, ch11, we return to forces between particles, but with a twist that pulls at a distance and
pushes up close. That single rule makes atoms clump into solids, flow as liquids, and fly apart
as gases, all depending on how hot they are.
