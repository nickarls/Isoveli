package fi.budokwai.isoveli.util;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;

public class Format
{
   private Font font = FontFactory.getFont("Helvetica", 12);
   private int verticalAlignment = Element.ALIGN_LEFT;
   private int horizontalAlignment = Element.ALIGN_MIDDLE;
   private int border = Rectangle.NO_BORDER;
   private int borderWidth = 0;
   private int padding = 1;
   private int paddingType = Rectangle.BOX;
   private boolean noWrap = false;

   public static Format create()
   {
      return new Format();
   }

   public Format withNoWrap(boolean noWrap)
   {
      this.noWrap = noWrap;
      return this;
   }

   public Format withFont(String name, int size, int style)
   {
      this.font = FontFactory.getFont(name, size);
      this.font.setStyle(style);
      return this;
   }

   public Format withVerticalAlignment(int verticalAlignment)
   {
      this.verticalAlignment = verticalAlignment;
      return this;
   }

   public Format withHorizontalAlignment(int horizontalAlignment)
   {
      this.horizontalAlignment = horizontalAlignment;
      return this;
   }

   public Format withBorder(int border)
   {
      this.border = border;
      return this;
   }

   public Format withBorderWidth(int borderWidth)
   {
      this.borderWidth = borderWidth;
      return this;
   }

   public Format withPadding(int padding, int paddingType)
   {
      this.padding = padding;
      this.paddingType = paddingType;
      return this;
   }

   public Font getFont()
   {
      return font;
   }

   public int getVerticalAlignment()
   {
      return verticalAlignment;
   }

   public int getHorizontalAlignment()
   {
      return horizontalAlignment;
   }

   public int getBorder()
   {
      return border;
   }

   public int getBorderWidth()
   {
      return borderWidth;
   }

   public int getPadding()
   {
      return padding;
   }

   public boolean isNoWrap()
   {
      return noWrap;
   }

   public int getPaddingType()
   {
      return paddingType;
   }
}
