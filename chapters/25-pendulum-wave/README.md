# Chapter 25: The pendulum wave

This is pure delight: a row of pendulums that, left to swing, drift out of step into rippling
waves and then, as if by magic, snap back into line, over and over. It is a favourite science-
museum toy, and it is built from the single fact we proved in ch25's engine: a longer pendulum
swings more slowly.

## Tuning the lengths

Line up a row of pendulums and give them lengths that make their swing rates step up one by one.
The trick is to choose the lengths so that, over one long cycle, the first pendulum makes some
whole number of swings, the next makes exactly one more, the next one more again, and so on.

Since the period is `2 pi sqrt(length / gravity)`, a target number of swings fixes the length:
faster pendulums (more swings) are shorter, slower ones longer. The scene sets fifteen lengths
this way, so that over one common cycle they make 12, 13, 14, ... 26 swings.

## Why the waves appear

At the start every pendulum is released together, so they swing in unison. But because each is
slightly slower than its neighbour, they immediately begin to fan out of phase. For a while the
row looks like a travelling wave; a little later, a snake weaving back and forth; later still,
scattered chaos; and then, because the rates were tuned to whole numbers, the phases refold and
the pendulums sweep back into a single line, and the whole show repeats.

Nothing coordinates them. There is no wave passing along the row and no coupling between the
pendulums at all. Each swings entirely on its own; the patterns are an illusion of timing,
emerging purely from steadily drifting phases. That is what makes it mesmerising.

## The scene

`PendulumWaveScene` hangs fifteen pendulums from a bar, each a hair shorter than the last,
coloured across the row so the patterns are easy to follow. Watch them start together, ripple
apart, and gather back. Press R to line them up and start again. The swings are kept small so the
bobs do not overlap, which also keeps the simple period formula accurate.

## Running it

```
./gradlew desktop:run
```

Press N to cycle to it. Tests (for the underlying pendulum):

```
./gradlew :engine:test --tests "org.physics.engine.pendulum.SimplePendulumTest"
```

## What to take away from this chapter

- Each pendulum's period depends only on its length, so tuned lengths give tuned swing rates.
- Released together, pendulums with slightly different rates drift out of phase and back again.
- The travelling-wave patterns are emergent: the pendulums never interact, they just drift.
- Choosing the rates as consecutive whole numbers per cycle makes the whole row realign on a
  clock.
