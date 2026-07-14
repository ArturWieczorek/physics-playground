# Chapter 22: The double-slit experiment

This is the flagship experiment of wave optics, and the one that first proved light is a wave.
Send a wave at a barrier with two narrow openings and, instead of two bright patches on the
screen beyond, you get a whole row of bright and dark stripes. In places, light plus light adds
up to darkness. Nothing but waves can do that.

## Waves add up

The key rule is superposition: where two waves overlap, their heights simply add. Two crests
together make a taller crest (constructive interference); a crest meeting a trough cancels to
nothing (destructive interference). Each open slit acts as a fresh source of circular waves, so
past the barrier two sets of ripples overlap and lock into a fixed pattern.

`Interference` (in `org.physics.engine.wave`) does two jobs. `amplitudeAt` gives the live wave
height by adding a circular wave `sin(k r - omega t)` from each slit, which is the rippling field
you see. `intensityAt` gives the steady brightness a screen records, by adding the waves as
phasors and taking the squared length of the total. The tests confirm the centre line is fully
bright (the waves arrive in step there), that a single slit makes no stripes, and that along a
screen the pattern genuinely swings between bright (intensity 4) and dark (near 0).

## Where the stripes come from

A point on the screen is a little farther from one slit than the other. That extra distance, the
path difference, decides everything:

- a whole number of wavelengths: the waves arrive in step and add: a **bright** fringe.
- a half-wavelength (or any half-odd number): they arrive exactly opposed and cancel: a **dark**
  fringe.

Because the path difference grows steadily as you move up the screen, the pattern repeats in
even stripes. Widen the slit gap and the stripes crowd together; lengthen the wavelength and they
spread apart.

## The scene

`InterferenceScene` sends a wave from the left through a barrier and onto a screen at the right.
The coloured ripples are the live field, red crests and blue troughs. The warm bars on the right
are the brightness the screen records. Press space to open one slit or two: with one there are no
stripes, and the moment you open the second the fringes snap into place. Up and down change the
wavelength, left and right the slit gap, and you can watch the stripes stretch and squeeze.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.wave.InterferenceTest"
```

## What to take away from this chapter

- Waves superpose: overlapping waves add, reinforcing in some places and cancelling in others.
- Two slits make a striped interference pattern; one slit makes none.
- A screen point is bright when the path difference is a whole wavelength, dark at a half.
- Wider slits crowd the stripes; longer wavelengths spread them. That light can cancel light is
  the proof it travels as a wave.
