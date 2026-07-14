# Chapter 23: Fourier series, drawing waves with circles

Here is a fact that should be surprising: you can build almost any repeating shape, even one with
flat tops and sharp vertical jumps like a square wave, by adding together nothing but smooth sine
waves. That is a Fourier series, and it is one of the most useful ideas in all of science and
engineering. This chapter builds it and draws it as a chain of spinning circles.

## Sines add up to anything

A Fourier series writes a repeating function as a sum of sine waves called harmonics. Each
harmonic has a frequency that is a whole-number multiple of the base, and its own size. For a
square wave the recipe is beautifully simple: use only the odd harmonics, and make the size of the
`n`th one `4 / (n pi)`.

`FourierSeries` (in `org.physics.engine.wave`) holds a list of these harmonics and adds them up.
The tests confirm the first square-wave harmonic has size `4/pi`, that a square wave uses only odd
harmonics, and, most importantly, that adding more terms makes the flat top converge to 1: with
one term you are off by a lot, with eighty you are within a fraction of a percent.

## Circles, because a sine is a spinning vector

A sine wave is the height of a point going around a circle at a steady rate. So each harmonic can
be drawn as a rotating vector, and adding the harmonics is the same as laying those vectors tip to
tip. The result is a chain of circles, each spinning at its own whole-number rate, and the end of
the chain traces out the wave. This is the "epicycle" picture, and it is exactly how the scene
draws it.

## The scene

`FourierScene` draws the spinning chain on the left; the tip of the chain feeds the graph on the
right, which scrolls to reveal the wave being drawn. Start with one circle and you get a plain
sine. Press up to add harmonics and watch the corners of a square wave sharpen into place out of
smooth curves; press down to take them away. Space switches between a square wave and a sawtooth,
which uses every harmonic instead of only the odd ones.

Fourier series (and their continuous cousin, the Fourier transform) are how sound is compressed,
how images are stored, and how almost any signal is understood as a spectrum of frequencies. They
are also the near relative of the Laplace transform from ch20.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.wave.FourierSeriesTest"
```

## What to take away from this chapter

- Any repeating shape is a sum of sine waves (harmonics), even a square wave with sharp corners.
- A square wave needs only the odd harmonics, sized `4/(n pi)`; more terms sharpen the corners.
- Each sine is a spinning vector, so the sum is a chain of circles whose tip draws the wave.
- Breaking a signal into its sine components underlies audio, images, and signal processing.
