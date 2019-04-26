package com.suntiago.baseui.activity.base.theMvp.annotation;

/**
 * Created by Zaiyu on 2019/4/26.
 */
public class BindingKey {
  private final String tag;
  private final Class<?> clazz;
  private final int hashCode;

  public BindingKey(String tag, Class<?> clazz) {
    if (tag == null) {
      throw new NullPointerException("EventType Tag cannot be null.");
    } else if (clazz == null) {
      throw new NullPointerException("EventType Clazz cannot be null.");
    } else {
      this.tag = tag;
      this.clazz = clazz;
      this.hashCode = (31 + tag.hashCode()) * 31 + clazz.hashCode();
    }
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      BindingKey other = (BindingKey) obj;
      return this.tag.equals(other.tag) && this.clazz == other.clazz;
    }
  }
}
