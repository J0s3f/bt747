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

import waba.fx.*;

/** 
 * This class are parts of waba.fx.FontMetrics adopted for UfoFont.
 */
public final class UfoFontMetrics
{
   protected UfoFont font;
   private ISurface surface;
   protected int ascent;
   protected int descent;
   protected int leading;

   /**
   * Constructs a font metrics object referencing the given font and surface.
   * <p>
   * If you are trying to create a font metrics object in a Control subclass,
   * use the getFontMetrics() method in the Control class.
   * @see waba.ui.Control#getFontMetrics(waba.fx.Font font)
   */
   public UfoFontMetrics(UfoFont font, ISurface surface)
   {
	   this.font = font;
	   this.surface = surface;
	   //NativeMethods.nm.fontMetricsCreate(this);
   }

   /**
   * Returns the ascent of the font. This is the distance from the baseline
   * of a character to its top.
   */
   public int getAscent()
   {
       return ( UfoManager.getFont( font ) ).getAscent();
   }

   /**
   * Returns the width of the given character in pixels.
   */
   public int getCharWidth(char c)
   {
       //return NativeMethods.nm.fontMetricsGetCharWidth(this,c);
       return ( UfoManager.getFont( font ) ).charWidth( c );
   }


   /**
   * Returns the descent of a font. This is the distance from the baseline
   * of a character to the bottom of the character.
   */
   public int getDescent()
   {
       return ( UfoManager.getFont( font ) ).getDescent();
   }

   /**
   * Returns the height of the referenced font. This is equal to the font's
   * ascent plus its descent. This does not include leading (the space between lines).
   */
   public int getHeight()
   {
	   return getAscent() + getDescent();
   }

   /**
   * Returns the external leading which is the space between lines.
   */
   public int getLeading()
   {
       return ( UfoManager.getFont( font ) ).getLeading();
   }

   /**
   * Returns the width of the given text string in pixels.
   */
   public int getTextWidth(String s)
   {
       return ( UfoManager.getFont( font ) ).stringWidth( s );
       //return NativeMethods.nm.fontMetricsGetTextWidth(this,s);
   }

   /**
   * Returns the width of the given text in pixels.
   * @param chars the text character array
   * @param start the start position in array
   * @param count the number of characters
   */
   public int getTextWidth(char chars[], int start, int count)
   {
       return ( UfoManager.getFont( font ) ).stringWidth( new String( chars, start, count ) );
       //return NativeMethods.nm.fontMetricsGetTextWidth(this,chars,start,count);
   }
}
