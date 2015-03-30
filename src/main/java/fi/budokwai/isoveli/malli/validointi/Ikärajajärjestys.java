package fi.budokwai.isoveli.malli.validointi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = Ik�rajaj�rjestysValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface Ik�rajaj�rjestys
{
   String message() default "";

   Class<?>[] groups() default
   {};

   Class<? extends Payload>[] payload() default
   {};

   String yl�raja();

   String alaraja();
}