# Chapter 24: The Lorenz attractor

The double pendulum in ch16 showed chaos in a mechanical toy. This chapter shows it in its most
famous mathematical form, the Lorenz attractor, and draws the shape that became the emblem of
chaos theory: a pair of wings a point circles forever without ever repeating.

## Three equations, a whole new science

In 1963 the meteorologist Edward Lorenz stripped a weather model down to three equations:

```
dx/dt = sigma (y - x)
dy/dt = x (rho - z) - y
dz/dt = x y - beta z
```

Running them, he found something that changed science: starting from almost exactly the same
numbers led, before long, to completely different outcomes. He called it the butterfly effect,
the idea that a flap of a butterfly's wings could, weeks later, change the path of a tornado. It
is not that the system is random; it follows these fixed equations perfectly. It is that any
error in the starting point, however tiny, grows explosively, so no amount of care lets you
predict far ahead.

`LorenzSystem` (in `org.physics.engine.chaos`) integrates the three equations with the same
Runge-Kutta method used for the double pendulum. The tests confirm all three hallmarks: the
trajectory stays bounded (it never flies off), the system genuinely keeps moving, and two starts
a ten-thousandth apart diverge completely within seconds.

## A strange attractor

Here is the paradox at the heart of the picture. The motion never settles to a point and never
falls into a repeating loop, so it is not orderly. Yet it never wanders off to infinity either:
it is drawn back, again and again, to the same butterfly-shaped region. A shape that traps a
chaotic path like this is called a strange attractor. The trajectory loops around one wing a few
times, flips to the other, loops there, flips back, with the number of loops each time being the
unpredictable part.

## The scene

`LorenzScene` lets the point draw its own path in 3D, as a trail that fades from the bright
leading tip back into the past. Drag to orbit the shape and the two wings reveal themselves.
Press R to restart from the beginning. It is, quite simply, one of the most beautiful objects in
mathematics, and it came out of trying to predict the weather.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.chaos.LorenzSystemTest"
```

## What to take away from this chapter

- Three simple equations can produce endlessly complex, never-repeating motion.
- The butterfly effect: tiny differences in the start grow explosively, ruining long-range
  prediction, even though the system is fully deterministic.
- The path is trapped forever in a bounded butterfly shape, a strange attractor.
- This is the same chaos as the double pendulum, and the reason weather forecasts have limits.
