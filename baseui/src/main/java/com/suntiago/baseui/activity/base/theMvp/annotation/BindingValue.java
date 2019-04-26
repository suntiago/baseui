package com.suntiago.baseui.activity.base.theMvp.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Zaiyu on 2019/4/26.
 */
public class BindingValue {
  private final Object target;
  private final Method method;
  private final int hashCode;
  private boolean valid = true;

  public BindingValue(Object target, Method method) {
    if (target == null) {
      throw new NullPointerException("EventProducer target cannot be null.");
    } else if (method == null) {
      throw new NullPointerException("EventProducer method cannot be null.");
    } else {
      this.target = target;
      this.method = method;
      method.setAccessible(true);
      this.hashCode = (31 + method.hashCode()) * 31 + target.hashCode();
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
      BindingValue other = (BindingValue) obj;
      return this.method.equals(other.method) && this.target == other.target;
    }
  }

  public Object produce(Object o) throws InvocationTargetException {
    if (!this.valid) {
      throw new IllegalStateException(this.toString() + " has been invalidated and can no longer produce events.");
    } else {
      try {
        return this.method.invoke(this.target, o);
      } catch (IllegalAccessException var2) {
        throw new AssertionError(var2);
      } catch (InvocationTargetException var3) {
        if (var3.getCause() instanceof Error) {
          throw (Error) var3.getCause();
        } else {
          throw var3;
        }
      }
    }
  }
}
