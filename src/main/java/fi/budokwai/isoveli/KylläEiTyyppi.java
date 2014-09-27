package fi.budokwai.isoveli;

import org.hibernate.type.BooleanType;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;

public class Kyll‰EiTyyppi extends BooleanType
{
   private static final long serialVersionUID = 1L;

   public Kyll‰EiTyyppi()
   {
      super(CharTypeDescriptor.INSTANCE, new BooleanTypeDescriptor("K".charAt(0), "E".charAt(0)));
   }
}
