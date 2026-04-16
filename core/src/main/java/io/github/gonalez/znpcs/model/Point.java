package io.github.gonalez.znpcs.model;

import com.google.common.collect.ComparisonChain;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

/** Point represents an XYZ coordinate. */
public class Point implements Comparable<Point>, Cloneable {
  private double x;
  private double y;
  private double z;

  public Point(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  @CanIgnoreReturnValue
  public Point set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  @Override
  public int compareTo(Point o) {
    return ComparisonChain.start()
        .compare(x, o.x)
        .compare(y, o.y)
        .compare(z, o.z)
        .result();
  }

  @Override
  public Point clone() {
    return new Point(x, y, z);
  }
}
