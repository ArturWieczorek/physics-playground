# Chapter 17: Light, an electromagnetic wave

We met electric fields in ch09 and magnetic fields in ch10. This chapter joins them, and the
result is light. Light is not a substance travelling through space; it is a ripple in the
electric and magnetic fields themselves, one feeding the other, rolling forward at a fixed
speed. Drawing that is the goal here.

## A travelling wave

A wave is a shape that moves without the medium moving along with it. The cleanest example is a
sine that slides forward:

```
field(x, t) = amplitude * sin( k (x - speed * t) )
```

The `x - speed * t` is the whole trick. A crest is wherever `x - speed * t` hits a peak of the
sine; to stay on that crest as time passes, `x` must grow at exactly `speed`. So the crest, and
the entire pattern, glides forward at `speed` without changing shape. `PlaneWave` (in
`org.physics.engine.wave`) is this one line, plus the bookkeeping that ties together three
numbers:

```
speed = frequency * wavelength
```

A test checks that relationship, that the wave repeats every wavelength, and that a point of
fixed phase really does travel at the wave speed.

## Why this is light

Maxwell discovered that a changing electric field creates a magnetic field, and a changing
magnetic field creates an electric field. Once you have both, they can sustain each other with
nothing else present: the electric field's change makes the magnetic field, whose change remakes
the electric field, and the pair travels off together as a self-sustaining wave. That wave is
light. Its speed falls straight out of Maxwell's equations as a fixed constant, the speed of
light, the same for every wavelength.

So a light wave has two fields, not one, and they are always:

- at right angles to each other,
- at right angles to the direction of travel, and
- rising and falling in step (in phase).

The single `PlaneWave` describes both; the scene just draws it twice.

## The scene

`LightScene` draws the wave the way physicists picture it. Down the middle runs the direction of
travel. At each point along it, an upright blue arrow shows the electric field and an orange
arrow, drawn going into the depth, shows the magnetic field at right angles to it. Both grow and
shrink together as the sine, and a smooth curve joins the arrow tips into the familiar wavy line
of light. The whole pattern slides forward.

- **Left and right** change the wavelength. Watch the arrows bunch up or spread out.
- **Up and down** change the amplitude, how strong the fields are (for light, how bright it is).

The readout shows the wavelength, the frequency, and the speed, and the key lesson is there: as
you change the wavelength the frequency changes to match, but the speed never budges. A shorter
wavelength is bluer light, a longer one redder, and all of it travels at exactly the same speed.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.wave.PlaneWaveTest"
```

## What to take away from this chapter

- A wave is a shape that moves; `x - speed t` is what makes a sine glide forward without changing
  shape.
- Speed, frequency, and wavelength are locked together by `speed = frequency * wavelength`.
- Light is an electric and a magnetic field sustaining each other, perpendicular and in phase,
  travelling at a fixed speed.
- Changing a light wave's wavelength changes its frequency but never its speed.
