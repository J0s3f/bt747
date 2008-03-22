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

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

/** 
 * This class are parts of waba.ui.Label adopted for UfoFont.
 */
public class UfoLabel extends UfoControl
{
   private String text;
   private int align;
   private static final String[] emptyStringArray = {""};
   private String []lines = emptyStringArray;
   private int []linesW;
   private int linesPerPage,currentLine;
   private boolean invert;
   private boolean is3d;
   private int hh;

   /** Creates a label displaying the given text with left alignment. supports inverted text, multiple lines and is scrollable */
   public UfoLabel(String text)
   {
      this(text, LEFT);
   }

   /**
   * Creates a label displaying the given text with the given alignment.
   * @param text the text displayed
   * @param align the alignment
   * @see #LEFT
   * @see #RIGHT
   * @see #CENTER
   */
   public UfoLabel(String text, int align)
   {
      this.align = align;
      setText(text);

   }

   /** if 3d is true, draws the label with a 3d effect. turns off invert. */
   public void set3d(boolean on)
   {
      is3d = on;
      if (on) invert = false;
   }
   /** if invert is true, the back and fore color are swaped. turns off 3d. */
   public void setInvert(boolean on)
   {
      invert = on;
      if (on) is3d = false;
   }
   /** Sets the text that is displayed in the label. */
   public void setText(String text)
   {
      this.text = text;
      lines = Convert.tokenizeString(text,'|');
      linesW = new int[lines.length];
      for (int i =0; i < lines.length; i++)
         linesW[i] = m_ufm.getTextWidth(lines[i]);
      currentLine = 0;
      repaint();
   }

   /** Gets the text that is displayed in the label. */
   public String getText()
   {
      return text;
   }
      
   /** returns the preffered width of this control. added by guich */
   public int getPreferredWidth()
   {
      int w = 0;
      for (int i =0; i < lines.length; i++)
         w = Math.max(w, m_ufm.getTextWidth(lines[i]));
      return w+(invert?2:0);
   }

   /** returns the preffered width of this control. added by guich */
   public int getPreferredHeight()
   {
      return m_ufmH*lines.length + (invert?1:0); // if inverted, make sure the string is surrounded by the black box
   }
   
   protected void onFontChanged()
   {
      for (int i =0; i < lines.length; i++)
         linesW[i] = m_ufm.getTextWidth(lines[i]);
      hh = m_ufmH*linesPerPage;
   }

   protected void onBoundsChanged()
   {
       UfoUserFont.debugMess( "UfoLabel: onBoundChanged: height=" + height + " m_ufmH=" + m_ufmH ); 
      linesPerPage = height / m_ufmH;
      if (linesPerPage < 1) linesPerPage = 1;
      hh = m_ufmH*linesPerPage;
      UfoUserFont.debugMess( "UfoLabel: onBoundChanged: linesPerPage=" + linesPerPage + " hh=" + hh ); 
   }
   
   /** returns if the label can scroll in the given direction */
   public boolean canScroll(boolean down) // guich@200b4_142
   {
      if (lines.length > linesPerPage)
      {
         if (down)
            return currentLine+linesPerPage < lines.length;
         else
            return currentLine - linesPerPage >= 0;
      }
      return false;
   }

   /** scroll one page. returns true if success, false if no scroll possible */
   public boolean scroll(boolean down)
   {
       
       onBoundsChanged();
       UfoUserFont.debugMess( "UfoLabel: lines=" + lines.length + " linesPerPage=" + linesPerPage ); 
      if (lines.length > linesPerPage)
      {
         int lastLine = currentLine;
	 UfoUserFont.debugMess( "UfoLabel: lastLines=" + lastLine );
         if (down)
         {
            if (currentLine+linesPerPage < lines.length)
               currentLine += linesPerPage;
         }
         else
         {
            currentLine -= linesPerPage;
            if (currentLine < 0) 
               currentLine = 0;
         }
	 UfoUserFont.debugMess( "UfoLabel: currentLine=" + currentLine );
         if (lastLine != currentLine) 
         {
            repaint();
            return true;
         }
      }
      return false;
   }

   /** Called by the system to draw the button. */
   public void onPaint( Graphics g )
   {
      // draw label
      if (invert)
      {
         g.setForeColor(backColor);
         g.setBackColor(foreColor);
      } 
      else
      {
         g.setForeColor(foreColor);
         g.setBackColor(backColor);
      }
      // guich@200b4_126: repaint the background always.
      g.fillRect(0,0,width,height); // guich@200b4_120: make sure the label is painted with the correct color
      if (text.length() > 0)
      {
	  UfoFont f =  m_font;
         int y = (this.height - hh) >> 1; // center on y (if necessary)
         int n = Math.min(currentLine+linesPerPage, lines.length);
         int xx = invert?1:0;
	 int leading = m_ufm.getLeading();
         for (int i =currentLine; i < n; i++,y+=m_ufmH+leading)
         {
            int x = 0;
            if (align != LEFT)
            {
               if (align == CENTER)
                  x = (width - linesW[i]) >> 1;
               else 
               if (align == RIGHT)
                  x = width - linesW[i];
            }
            
            if (is3d) // if 3d, invert = false
            {
               g.setForeColor(backColor.darker());
               g.drawText(lines[i], xx+x+1, y+1);
               g.setForeColor(foreColor);
            }
            //g.drawText(lines[i], xx+x, y);

	    UfoManager.drawText( g, f, lines[i], xx+x, y );
	    //ug.drawText(g, lines[i], xx+x, y );
	    //g.drawLine( xx+x, y, xx+x+30 , y+1 );
	    //g.drawLine( xx+x, y, xx+x+30 , y+4 );
	    //g.drawLine( xx+x, y, xx+x+30 , y+8 );
	    //g.drawLine( xx+x, y, xx+x+30 , y+13 );
         }
      }
   }
}
