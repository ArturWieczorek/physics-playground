# Chapter 2: Vectors

Everything that moves in our world has a position and a velocity, and both of those are
really two numbers travelling together: how far across, and how far up. A pair of numbers
treated as one thing is called a vector, and it is the single most useful idea in the whole
engine. This chapter builds it, test first.

## What a vector is

Write a vector as `(x, y)`. It has two jobs, and it is worth being clear which one you mean
at any moment:

- A **position**: a point in space, measured from the origin `(0, 0)`. "The ball is at
  `(3, 4)`."
- A **direction with a size**: an arrow. "The ball moves `(2, 0)` each second", meaning two
  units to the right and none up. Velocities, accelerations, and forces are all this kind.

The beauty is that the same arithmetic serves both.

## The operations, and what they mean

- **Add** `(1, 2) + (3, 4) = (4, 6)`. Adding a movement to a position gives the new
  position. This is the heart of the simulation loop from ch00.
- **Subtract** `target - source` gives the arrow pointing from one point to another. We use
  this constantly: to find which way one object is from another.
- **Scale** `(2, -3) * 2.5 = (5, -7.5)`. Stretch or shrink an arrow. Multiplying a velocity
  by the time step `dt` turns "per second" into "this frame".
- **Length** is how long the arrow is, by Pythagoras: the `(3, 4)` vector has length
  `sqrt(3*3 + 4*4) = 5`. Speed is the length of a velocity.
- **Normalize** shrinks an arrow to exactly one unit long while keeping its direction. That
  is how we say "this way, please" without caring how far.
- **Dot product** is a single number that is zero when two arrows are at right angles,
  positive when they roughly agree, negative when they oppose. It will let us bounce things
  off walls in ch06.
- **Cross product** in 2D is a single number whose sign says whether one arrow turns left or
  right relative to another.

## A design choice: vectors do not change

Our `Vector2` is immutable. Once made, its numbers never change. Every operation returns a
brand new vector:

```java
Vector2 position = new Vector2(3, 4);
Vector2 moved = position.add(new Vector2(1, 0)); // position is still (3, 4)
```

This feels slightly wasteful, and in a physics engine pushed to its limits you would
sometimes avoid it. But for learning it is a clear win: there is no way for two parts of the
program to secretly share one vector and corrupt each other's data, which is a classic and
painful bug. It also makes the code read like the maths on paper.

We used a Java `record`, which is a compact way to declare a small immutable data holder. It
gives us the `x()` and `y()` accessors, along with sensible equality and printing, for free.

## Writing the test first

Look at `Vector2Test` before `Vector2`. Each test states a fact we expect to be true: a
`(3, 4)` vector is 5 long, perpendicular vectors have a dot product of zero, normalizing the
zero vector is not allowed. Only then did we write the code to make those facts hold.

That order matters. The test is a small, precise description of what the code should do,
written while the goal is fresh, before the temptation to just match whatever the code
happens to produce.

Note the two cases that expect a failure: normalizing a zero-length vector, and (from ch01)
a backwards clamp range. A good test suite pins down what should go wrong as firmly as what
should go right.

## Running it

```
./gradlew :engine:test
```

All the `Vector2` tests should pass.

## What to take away from this chapter

- A vector is two numbers acting as one, used both for positions and for directions.
- Add positions to movements, subtract points to get the arrow between them, scale to
  resize, normalize to keep only direction.
- Our vectors are immutable, which trades a little speed for a lot of safety and clarity.
- Tests describe the intended behaviour first, including what should be rejected.

Next, ch03, we put vectors to work: a particle with a position and a velocity, and the
different ways of stepping it forward through time.
