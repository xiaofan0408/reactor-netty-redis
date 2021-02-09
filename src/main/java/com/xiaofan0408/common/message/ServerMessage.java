package com.xiaofan0408.common.message;


public interface ServerMessage<T> {

  T getData();

  default boolean ending() {
    return false;
  }

  default boolean resultSetEnd() {
    return false;
  }
}
