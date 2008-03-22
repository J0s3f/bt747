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

import waba.sys.*;

/** 
 * This class are parts of waba.sys.Convert adopted for UfoFont.
 */
public class UfoConvert
{

   public static String insertLineBreak(int maxWidth, char separator, UfoFontMetrics ufm, String text) 
   {
      String []words = Convert.tokenizeString(text,' ');
      StringBuffer sb = new StringBuffer(text.length()+20);      
      String s = "";
      for (int i =0; i < words.length; i++)
      {
	  if (ufm.getTextWidth(s+' '+words[i]) > maxWidth)
         {
            if (sb.length() > 0)
               sb.append(separator);
            sb.append(s);
            s = "";
         }
         if (s.length() > 0) s += ' ';
         s += words[i];
      }
      if (s.length() > 0)
      {
	  if (sb.length() > 0)
	      sb.append(separator);
	  sb.append(s);
      }
      return sb.toString();
   }   
   
   public static String insertLineBreakForceWordbreak(int maxWidth, char separator, UfoFontMetrics ufm, String text) 
   {
      String []words = Convert.tokenizeString(text,' ');
      StringBuffer sb = new StringBuffer(text.length()+20);      
      String s = "";
      for (int i =0; i < words.length; i++)
      {
	  if (ufm.getTextWidth(s+' '+words[i]) > maxWidth)
         {
            if (sb.length() > 0)
               sb.append(separator);
            sb.append(s);
            s = "";
         }
         if (s.length() > 0) s += ' ';
         s += words[i];
      }
      if (s.length() > 0)
      {
	  if (sb.length() > 0)
	      sb.append(separator);
          sb.append(s);
      }
      return sb.toString();
   }   
   
}
