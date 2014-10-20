package fi.budokwai.isoveli.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import fi.budokwai.isoveli.malli.Sukupuoli;

@Converter
public class SukupuoliConverter implements AttributeConverter<Sukupuoli, String>
{

   @Override
   public String convertToDatabaseColumn(Sukupuoli attribute)
   {
      switch (attribute)
      {
      case Mies:
         return "M";
      case Nainen:
         return "N";
      default:
         throw new IllegalArgumentException(String.format("Tuntematon sukupuolityyppi %s", attribute));
      }
   }

   @Override
   public Sukupuoli convertToEntityAttribute(String dbData)
   {
      switch (dbData)
      {
      case "M":
         return Sukupuoli.Mies;
      case "N":
         return Sukupuoli.Nainen;
      default:
         throw new IllegalArgumentException(String.format("Tuntematon tietokantatyyppi %s", dbData));
      }
   }
}