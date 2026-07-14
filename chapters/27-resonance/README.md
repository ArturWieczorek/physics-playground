# Chapter 27: Resonance

Everyone knows you push a swing harder to go higher. But the real secret is not how hard you push,
it is when. Push in time with the swing and even gentle nudges build it up enormously; push out of
time and nothing happens. That is resonance, one of the most far-reaching ideas in physics, and
this chapter shows it with a mass on a spring driven by a steady rhythm.

## The driven oscillator

Take the mass-and-spring from ch05, add a little friction, and push it with a rhythmic force that
oscillates at some driving frequency. The mass obeys

```
mass * acceleration = -spring * position - damping * velocity + force * cos(drive * time)
```

three influences: the spring pulling it home, friction draining its energy, and the driving push
adding energy. After the initial wobble dies away, the mass settles into swinging at the driving
frequency, with a steady size given by

```
swing size = force / sqrt( (spring - mass * drive^2)^2 + (damping * drive)^2 )
```

`DrivenOscillator` (in `org.physics.engine.force`) both plays this out step by step and reports
that steady size. The tests confirm the two ends of the story: a very slow push just gives the
static stretch `force / spring`, and the swing peaks sharply near the mass's own natural
frequency. A third test drives the mass at resonance and off it, and shows the on-resonance swing
grows several times larger.

## Why timing beats force

The mass has a natural frequency it "likes", `sqrt(spring / mass)`. Drive it there and every push
arrives just as the mass is moving the way the push points, so each push adds energy and the swing
grows and grows, checked only by friction. Drive it at the wrong rhythm and the pushes fall out of
step, sometimes helping and sometimes fighting, and little happens. The lighter the damping, the
taller and narrower the resonance peak.

Resonance is everywhere: pushing a child's swing, tuning a radio to pick one station out of many,
a singer shattering a wine glass at its pitch, an MRI machine, and the reason troops break step
when crossing a bridge so their marching does not drive it at its natural frequency.

## The scene

`ResonanceScene` shows the driven mass on the left and its response curve on the right, the swing
size for every driving frequency. Press left and right to sweep the driving frequency; a yellow
marker slides along the curve and the mass on the left responds. Away from the natural frequency
(marked in red) the swing is small; bring the marker onto the peak and the mass suddenly swings
wide. That towering peak is resonance.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.force.DrivenOscillatorTest"
```

## What to take away from this chapter

- A driven oscillator settles into swinging at the frequency it is pushed at.
- The swing size peaks sharply when the driving frequency matches the natural frequency: resonance.
- Timing the pushes matters far more than their strength; in step, small pushes build a big swing.
- Lighter damping makes a taller, narrower peak. Resonance runs from radios to bridges.
