package fi.budokwai.isoveli.util;

import org.hibernate.type.BooleanType;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;

public class KylläEiTyyppi extends BooleanType
{
   private static final long serialVersionUID = 1L;

   public KylläEiTyyppi()
   {
      super(CharTypeDescriptor.INSTANCE, new BooleanTypeDescriptor("K".charAt(0), "E".charAt(0)));
   }
}
