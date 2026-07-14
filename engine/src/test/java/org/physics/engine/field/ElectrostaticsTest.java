package org.physics.engine.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.physics.engine.core.Particle;
import org.physics.engine.force.Coulomb;
import org.physics.engine.math.Vector2;

@DisplayName("Electrostatics: Coulomb's law and the electric field")
class ElectrostaticsTest {

  @Test
  @DisplayName("like charges repel and opposite charges attract")
  void likeChargesRepelOppositeAttract() {
    // Two positives, one at the origin and one to the right.
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(1.0);
    Particle b = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0).charge(1.0);
    new Coulomb(1.0, 0.0).apply(List.of(a, b));
    assertTrue(a.force().x() < 0, "a should be pushed left, away from b");
    assertTrue(b.force().x() > 0, "b should be pushed right, away from a");

    // Now make b negative: they should attract instead.
    Particle c = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(1.0);
    Particle d = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0).charge(-1.0);
    new Coulomb(1.0, 0.0).apply(List.of(c, d));
    assertTrue(c.force().x() > 0, "c should be pulled right, toward d");
    assertTrue(d.force().x() < 0, "d should be pulled left, toward c");
  }

  @Test
  @DisplayName("the force follows k*q1*q2/r^2 and is equal and opposite")
  void forceMatchesCoulombsLaw() {
    Particle a = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(2.0);
    Particle b = new Particle(new Vector2(3, 0), Vector2.ZERO, 1.0).charge(3.0);
    new Coulomb(1.0, 0.0).apply(List.of(a, b));
    // k=1, q1*q2=6, r^2=9, so magnitude 6/9 = 0.6667.
    assertEquals(6.0 / 9.0, a.force().length(), 1e-9);
    assertEquals(a.force().x(), -b.force().x(), 1e-9);
  }

  @Test
  @DisplayName("the field points away from a positive charge and toward a negative one")
  void fieldDirectionFollowsSign() {
    Particle positive = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(1.0);
    Vector2 right = new Vector2(2, 0);
    Vector2 fieldFromPositive = ElectricField.fieldAt(right, List.of(positive), 1.0, 0.0);
    assertTrue(fieldFromPositive.x() > 0, "field points away from a positive charge");

    Particle negative = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(-1.0);
    Vector2 fieldFromNegative = ElectricField.fieldAt(right, List.of(negative), 1.0, 0.0);
    assertTrue(fieldFromNegative.x() < 0, "field points toward a negative charge");
  }

  @Test
  @DisplayName("a single charge makes a field of size k*q/r^2")
  void fieldMagnitudeFromSingleCharge() {
    Particle charge = new Particle(new Vector2(0, 0), Vector2.ZERO, 1.0).charge(4.0);
    Vector2 field = ElectricField.fieldAt(new Vector2(2, 0), List.of(charge), 1.0, 0.0);
    assertEquals(4.0 / 4.0, field.length(), 1e-9); // k*q/r^2 = 4/4 = 1
  }

  @Test
  @DisplayName("the potential is zero halfway between equal and opposite charges")
  void potentialCancelsOnTheMidline() {
    Particle plus = new Particle(new Vector2(-2, 0), Vector2.ZERO, 1.0).charge(1.0);
    Particle minus = new Particle(new Vector2(2, 0), Vector2.ZERO, 1.0).charge(-1.0);
    double potential = ElectricField.potentialAt(new Vector2(0, 0), List.of(plus, minus), 1.0, 0.0);
    assertEquals(0.0, potential, 1e-9);
  }
}
