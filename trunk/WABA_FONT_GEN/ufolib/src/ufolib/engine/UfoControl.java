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

/** 
 * This class is a midclass for controls with UfoFont
 */
public class UfoControl extends Control {

    protected UfoFont m_font = new UfoFont( "UFFgenXX", UfoFont.PLAIN, 12 );

    protected UfoFontMetrics m_ufm;
    protected int m_ufmH;

    public UfoControl() {
	m_font = UfoManager.getCurrentFont();
	m_ufm = new UfoFontMetrics( m_font, null );
	m_ufmH = m_ufm.getHeight();
    }

    public UfoFontMetrics getUfoFontMetrics() {
	return m_ufm;
    }


    /** sets the font of this conrol. */
    public void setUfoFont( UfoFont font ) {
	m_font = font;
	m_ufm = new UfoFontMetrics( m_font, null );
	m_ufmH = m_ufm.getHeight();
	onFontChanged();
    }

    /** gets the font of this conrol. */
    public UfoFont getUfoFont() {
	return m_font;
    }


}
