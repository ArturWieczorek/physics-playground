# Chapter 7: A box of gas, and where temperature comes from

We do not add a single new line of physics in this chapter. We just take the collisions from
ch06 and pour a few hundred particles into a box. Out of nothing but random bouncing come
three of the biggest ideas in thermodynamics: temperature, the spread of molecular speeds, and
the reason a gas has any temperature at all. This is one of the most satisfying results in the
whole course, because it is entirely emergent. Nobody tells the particles how to behave; the
behaviour appears on its own.

## Temperature is just average energy of motion

Ask what temperature really is and the honest answer is surprising: it is the average kinetic
energy of the particles. A hot gas is one whose particles are, on average, moving fast; a cold
gas is one whose particles are moving slowly. There is nothing else to it.

Working in two dimensions and in units where Boltzmann's constant is 1, the relationship is as
clean as it gets:

```
temperature = average kinetic energy per particle
```

That is `MaxwellBoltzmann.temperature` in the engine: add up every particle's one-half m v
squared and divide by how many there are. Heat the gas (speed the particles up) and the number
rises; cool it and it falls. The test `temperatureIsAverageKineticEnergy` pins this down on a
tiny two-particle example.

## The distribution of speeds

Here is the part that feels like magic. Start every particle at the *same* speed, pointing in
random directions, and let them collide. Within a second or two the single speed has smeared
out into a whole range: a few particles nearly stopped, a few racing, and a broad hump of
ordinary speeds in between. And it is not just any spread. It always relaxes to the same
shape, the Maxwell-Boltzmann distribution.

In two dimensions that shape is

```
f(v) = (m v / T) * exp(-m v^2 / (2 T))
```

It starts at zero (nothing is perfectly still for long), climbs to a most-likely speed, then
tails away (very fast particles are rare). `MaxwellBoltzmann.speedProbabilityDensity` returns
this, and two tests hold it to account: one integrates it and confirms it sums to 1 like any
proper probability, and one integrates `v` times it and confirms the average speed matches the
formula `sqrt(pi T / 2m)`.

Why does random bouncing always land on this exact curve? Because collisions swap energy
around, and this is the single most likely way for a fixed total energy to be shared among many
particles. It is the shape with the most ways of happening, so it is where the gas ends up. You
are watching statistics win.

## The scene: watch it emerge

`GasScene` fills the upper box with 260 particles, all starting at the same speed. Along the
bottom it draws two things on the same axes:

- **The bars**: a live histogram of the particles' actual speeds right now.
- **The smooth curve**: the Maxwell-Boltzmann prediction for the current temperature.

At the very first instant the bars are a single tall spike (everyone at one speed) and they do
not match the curve at all. Within moments the collisions spread the speeds out and the bars
settle onto the curve and stay there, jiggling around it. The particles are also coloured by
speed, cool blue to hot red, so you can see the fast and slow ones mingling.

Press the up and down arrows to heat and cool the gas. As the temperature changes, the whole
curve stretches or shrinks and the bars follow it. Press R for a fresh gas to watch the
spreading-out happen again from the start.

Because every collision and wall bounce is perfectly elastic, the total energy never changes
on its own. The test `gasConservesTotalEnergy` runs a full gas for three thousand steps and
confirms the total kinetic energy is the same at the end as at the start, to one part in a
million. That is what lets the gas hold a steady temperature.

## Running it

```
./gradlew desktop:run
```

Press 3 for the gas. Up and down arrows heat and cool it; R shuffles a new one. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.gas.GasTest"
```

## What to take away from this chapter

- Temperature is the average kinetic energy of the particles, nothing more.
- Left to collide, a gas always settles into the Maxwell-Boltzmann spread of speeds, whatever
  it started from.
- That curve wins because it is the most likely way to share a fixed energy among many
  particles.
- Elastic collisions conserve the total energy, so the temperature holds steady. The tests
  prove both the statistics and the conservation.

Next, ch08, we swap the walls for open space and turn on gravity between bodies. A few
particles pulling on each other trace out the ellipses that planets and moons have followed for
billions of years.
