# Chapter 0: Orientation

Welcome. This chapter has no code. Its job is to give you the map and the single most
important idea behind every simulation you are about to build. Read it once, and the rest
of the course will make far more sense.

## What we are actually doing

A physics simulation is a program that answers one question over and over, very fast:

> Given where everything is right now, and how fast it is moving, where will it be a tiny
> moment from now?

That is the whole trick. We never solve physics "all at once". We take a tiny step
forward in time, update every object a little, draw the result, and repeat. Do that sixty
times a second and the eye sees smooth, believable motion.

The tiny step has a name: we call it `dt`, short for "delta time", the small slice of time
between one frame and the next. If the game is running at 60 frames per second, `dt` is
about 1/60 of a second, roughly 0.0167.

## Time in small steps

Imagine a ball moving to the right at 3 metres per second. Where is it after one small
step of time `dt`?

```
new position = old position + velocity * dt
```

If `dt` is 0.0167 seconds, the ball moves 3 * 0.0167 = about 0.05 metres this step. Next
step, we do it again from the new position. Small steps, repeated, add up to motion.

Now add gravity. Gravity does not change position directly, it changes velocity: it pulls
the ball's downward speed up a little every step.

```
new velocity = old velocity + acceleration * dt
new position = old position + new velocity * dt
```

That is genuinely most of it. Every scene in this course, from a single falling dot to a
sheet of tearing cloth, is built from that same loop:

1. Work out the forces on each object (gravity, springs, pushes from other objects).
2. Turn force into acceleration (Newton's second law, which we meet in ch04).
3. Use acceleration to update velocity.
4. Use velocity to update position.
5. Draw everything.
6. Go back to step 1.

This is called numerical integration. "Integration" sounds like scary calculus, but here
it just means "adding up lots of tiny steps". We will look at a few different ways of
doing that adding-up in ch03, because some are more accurate than others.

## Why small steps are only approximately right

Because we jump forward in whole steps instead of moving continuously, a simulation is
always a little bit wrong. Smaller steps are more accurate but cost more computing. A big
part of doing physics well on a computer is choosing methods that stay believable and
stable even when the steps are not tiny. You will see this trade-off with your own eyes in
ch03, where one method slowly gains energy from nowhere and another stays steady.

## The shape of the project

You do not need to understand the code yet, just the layout. The project is split into
parts on purpose:

```
engine/    The physics. Pure Java, no graphics. This is where the maths lives,
           and where almost all of our tests live.
app/       The visible app. It draws each scene and reads your mouse and keyboard.
desktop/   A launcher that opens the app in a normal window on your computer.
web/       A launcher that compiles the app to run inside a web browser.
docs/      The finished web build, ready to publish online.
chapters/  This course. One folder per lesson.
```

The reason the physics is kept separate in `engine` is important. Graphics code is hard
to test automatically, because it is about pixels on a screen. Physics code is easy to
test, because it is about numbers: if a ball starts here with this speed, after one second
it should be there. By keeping the numbers away from the pixels, we can prove the physics
is correct with fast, automatic tests before we ever worry about drawing it.

## How you will run things

By the end of ch01 these commands will work from the project folder:

```
./gradlew desktop:run     Open the app in a window.
./gradlew test            Run every physics test and report pass or fail.
./gradlew spotlessApply    Reformat the code to a consistent style.
```

The browser version and how to put it online is the very last chapter, ch14.

## How the course is committed

Every chapter is a single commit in the git history, with a matching tag. If you ever want
to see the project exactly as it was at the end of a chapter, check out its tag:

```
git checkout ch03
```

To come back to the latest version:

```
git checkout main
```

## What to take away from this chapter

- A simulation is a loop that nudges everything forward by a tiny time step `dt`, over and
  over.
- Force changes velocity, velocity changes position. That order matters.
- Stepping in chunks is only approximately correct, and choosing good methods is part of
  the craft.
- The physics (`engine`) is kept separate from the drawing (`app`) so we can test it.

Next up, ch01, we set up the tools: the build system that compiles and runs everything, an
automatic formatter so the code stays tidy, and the testing framework we will lean on for
the rest of the course.
