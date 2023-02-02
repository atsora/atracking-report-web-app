// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.util;

/**
 * A couple of generics object
 * 
 * @author Eric
 * @version 1.0
 * 
 * @param <A>
 *          Type of first element of the pair
 * @param <B>
 *          Type of second element of the pair
 */
public class Pair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<Pair<? extends A, ? extends B>> {
  /**
   * First element of couple
   */
  private A first;
  /**
   * Second element of couple
   */
  private B second;

  public Pair(A a, B b) {
    first = a;
    second = b;
  }

  /**
   * Get first element
   * 
   * @return first element
   */
  public A getFirst() {
    return first;
  }

  /**
   * Get second element
   * 
   * @return second element
   */
  public B getSecond() {
    return second;
  }

  /**
   * Set first element
   * 
   * @param first
   *          first element
   */
  public void setFirst(A first) {
    this.first = first;
  }

  /**
   * Set second element
   * 
   * @param second
   *          second element
   */
  public void setSecond(B second) {
    this.second = second;
  }

  public String toString() {
    return "(" + first.toString() + ", " + second.toString() + ")";
  }

  public int hashCode() {
    return first.hashCode() ^ second.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (!(o instanceof Pair))
      return false;
    Pair<?, ?> pairo = (Pair<?, ?>) o;
    return this.first.equals(pairo.getFirst()) && this.second.equals(pairo.getSecond());
  }

  public boolean equals2(Object other) {
    if (other instanceof Pair) {
      Pair<?, ?> otherPair = (Pair<?, ?>) other;
      return ((this.first == otherPair.first || (this.first != null && otherPair.first != null && this.first.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
          && otherPair.second != null && this.second.equals(otherPair.second))));
    }

    return false;
  }

  @Override
  public int compareTo(Pair<? extends A, ? extends B> other) {
    int cmp = first.compareTo(other.first);
    return cmp == 0 ? second.compareTo(other.second) : cmp;
  }

  /*
   * public int compareTo(Pair<? extends A, ? extends B> that) { int cf = compare(first, that.first); return cf == 0 ? compare(second, that.second) : cf; }
   */

  /**
   * Compare two <code>Pair</code> instances
   * 
   * @param first
   *          A <code>Pair</code> Object
   * @param second
   *          A <code>Pair</code> Object
   * @return 1 if first Object is greater than the second, -1 if first object is less than the second or 0 if the two are equals
   */
  /*
   * private static int compare(Object first, Object second) { if (first == null) { return second == null ? 0 : -1; } else { return second == null ? 1 : ((Comparable)
   * (first)).compareTo(second); } }
   */

  /**
   * Create a <code>Pair</code> object using two elements as parameters
   * 
   * @param <A>
   *          type of first element
   * @param <B>
   *          type of second element
   * @param first
   *          first element
   * @param second
   *          second element
   * @return A <code>pair</code> instance using parameters of this methods
   */
  public static <A extends Comparable<A>, B extends Comparable<B>> Pair<A, B> of(A first, B second) {
    return new Pair<A, B>(first, second);
  }

}
