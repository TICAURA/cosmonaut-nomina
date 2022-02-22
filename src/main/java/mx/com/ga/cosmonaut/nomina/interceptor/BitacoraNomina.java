package mx.com.ga.cosmonaut.nomina.interceptor;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Around
@Type(BitacoraNominaInterceptor.class)
public @interface BitacoraNomina {
}