# Chapter 26: Coupled springs and standing waves

One mass on a spring (ch05) just bobs. Connect many masses with many springs and something far
richer appears: the beads move together in patterns, the disturbances travel and reflect, and the
whole row behaves like a vibrating string. This is the bridge from a single oscillator to a
continuous wave, and it is built entirely from springs we have already tested.

## Many oscillators, coupled

The scene strings eighteen beads together with springs and pins the two ends. Each spring is the
same Hooke's-law spring from ch05; the only new thing is that there are many of them, and each
bead is pulled by the springs on both sides. Because every bead's motion depends on its
neighbours', they are coupled: push one and the push passes along the line.

Two practical touches make it behave like a real string. The springs are stretched taut (their
rest length is shorter than the gap), so the string carries tension, and each bead is held on its
own vertical line so it moves only up and down. Tension pulling on a sideways-displaced bead is
what provides the restoring force for a transverse wave.

## Normal modes and standing waves

A string fixed at both ends cannot vibrate just any way. It has special shapes, the normal modes,
in which every bead oscillates at the same frequency and the whole string simply scales up and
down in place: one hump, two humps, three humps, and so on. These are standing waves. Higher
modes wobble faster. Any pluck is a mixture of these modes, which is why a plucked string sounds
like a fundamental note plus overtones, and it is the very same harmonic series that Fourier
(ch23) taught us to add up.

## The scene

`CoupledSpringsScene` starts the string in a chosen normal mode so you can see a clean standing
wave rock up and down. Press space to step through the modes, one hump to five. Press up and down
to change the tension, and watch the waves speed up or slow down. Drag any bead to pluck the
string by hand and let go: the kink you make splits, races to the fixed ends, reflects, and
returns, exactly as a wave on a real string does.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. The springs themselves are tested in ch05:

```
./gradlew :engine:test --tests "org.physics.engine.force.SpringTest"
```

## What to take away from this chapter

- Coupling many spring-mass oscillators lets disturbances travel: a wave on a string.
- Tension supplies the restoring force for sideways (transverse) waves.
- A fixed string has normal modes, standing waves with one, two, three, ... humps, each at its
  own frequency.
- Any pluck is a sum of these modes, the fundamental plus overtones, the harmonic series again.
