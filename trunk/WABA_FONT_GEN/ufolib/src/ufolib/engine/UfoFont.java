/*
 * ufolib - Full unicode font range for Superwaba
 * Copyright (C) 2002 Oliver Erdmann
 * http://jdict.sf.net 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 2.1 as published by the Free Software Foundation
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * Parts are:
 *  Copyright (C) 2000-2002 Guilherme Campos Hazan <guich@superwaba.org.>
 */
package ufolib.engine;

/** 
 * This class are parts of waba.fx.Font adopted for UfoFont.
 */
public final class UfoFont
{
   private String name;
   private int style;
   private int size;

   /** A plain font style. */
   public static final int PLAIN  = 0;

   /** A bold font style. */
   public static final int BOLD   = 1;

   /** An italic font style. */
   public static final int ITALIC   = 2;

   /**
   * Creates a font of the given name, style and size. Font styles are defined
   * in this class.
   * @see #PLAIN
   * @see #BOLD
   * @see Graphics
   */
   public UfoFont( String name, int style, int size )
   {
	   this.name = name;
	   this.style = style;
	   this.size = size;
	   //NativeMethods.nm.fontCreate(this);
   }

   /** Returns the name of the font. */
   public String getName()
   {
	   return name;
   }

   /** Returns the size of the font. */
   public int getSize()
   {
	   return size;
   }

   /**
   * Returns the style of the font. Font styles are defined in this class.
   * @see #PLAIN
   * @see #BOLD
   */
   public String getStyle()
   {
       if ( style == PLAIN ) {
	   return "n";
	   }
       else {
	   String ret = "";
	   if ( ( style & BOLD ) != 0 ) {
	       ret += "b";
	   }
	   if ( ( style & ITALIC ) != 0 ) {
	       ret += "i";
	   }
	   return ret;
       }
   }

   /** returns this font as Bold */
   public UfoFont asBold()
   {
      return new UfoFont(name,BOLD,size);
   }
}

