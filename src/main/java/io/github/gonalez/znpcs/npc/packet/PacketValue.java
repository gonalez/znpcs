package io.github.gonalez.znpcs.npc.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PacketValue {
  String keyName();
  
  ValueType valueType() default ValueType.DEFAULT;
}
