package fi.budokwai.isoveli.malli.validointi;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = P‰iv‰m‰‰r‰j‰rjestysValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface P‰iv‰m‰‰r‰j‰rjestys
{
   String message() default "";

   Class<?>[] groups() default
   {};

   Class<? extends Payload>[] payload() default
   {};

   String alaraja();

   String yl‰raja();
   
   boolean aika();

   @Target(
   { TYPE, ANNOTATION_TYPE })
   @Retention(RUNTIME)
   @Documented
   @interface List
   {
      P‰iv‰m‰‰r‰j‰rjestys[] value();
   }
}