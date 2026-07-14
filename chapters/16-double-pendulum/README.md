# Chapter 16: The double pendulum, and chaos

One pendulum swings back and forth, steady and predictable. Hang a second pendulum from the end
of the first, and something remarkable happens: the motion becomes chaotic. It never settles,
never repeats, and after a few seconds becomes impossible to predict, even though there is
nothing random in it at all. This is the most famous simple example of chaos, and it is a
fitting note to end on.

## A different kind of model

Every other moving thing in this course has been built from particles and forces. The double
pendulum resists that: the two rigid rods are hard to express as forces without the whole thing
becoming a mess. So we take a different approach and track the two angles directly, advancing
them with the pendulum's known equations of motion.

Those equations are stiff and unforgiving, so we integrate them with the fourth-order
Runge-Kutta method, a more careful relative of the integrators from ch03. It samples the rates
of change four times per step and blends them, which keeps the energy steady over long runs
where a simpler method would drift. A test confirms the total energy barely changes over twenty
seconds of wild swinging.

## Deterministic, yet unpredictable

Here is the heart of chaos, and it is worth stating carefully. The double pendulum is completely
deterministic: its future is fixed entirely by its present, with no chance involved. And yet it
is unpredictable, because it is exquisitely sensitive to its starting point. Two double
pendulums released from almost the same position, differing by a ten-thousandth of a radian,
follow nearly the same path for a moment and then diverge completely. A test starts two
pendulums a hair apart and confirms that ten seconds later their lower bobs are nowhere near
each other.

This is why chaos matters beyond a toy: the weather, and many other systems, are like this.
They obey exact laws, but because we can never know their state perfectly, and tiny errors
explode, long-range prediction is impossible in practice. Determinism does not imply
predictability.

## The scene

`DoublePendulumScene` swings a double pendulum and traces the path of the lower bob as a fading
trail, which draws gorgeous, never-repeating loops. Click anywhere to drop the pendulum pointing
in that direction and watch an entirely different dance unfold from an almost identical start.
Press R to reset.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests:

```
./gradlew :engine:test --tests "org.physics.engine.pendulum.DoublePendulumTest"
```

## What to take away from this chapter

- Some systems are cleaner to model by their equations of motion than by particles and forces.
- Fourth-order Runge-Kutta integrates stiff equations accurately enough to conserve energy over
  long runs.
- The double pendulum is deterministic but chaotic: fixed laws, yet wildly sensitive to where it
  starts.
- That sensitivity, not randomness, is why chaotic systems like the weather cannot be predicted
  far ahead.

That is the end of the course. From a single dot falling under gravity you have reached chaos
itself, and every step of the way is plain, tested Java you wrote and understand.
