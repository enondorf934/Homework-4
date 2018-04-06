//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 29 23:36:04 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160225 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.homework.homework04;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

//******************************************************************************

/**
 * The <CODE>KeyHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view)
	{
		this.view = view;

		Component	component = view.getComponent();

		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{
		Point2D.Double	p = view.getOrigin();
		//double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);

		switch (e.getKeyCode())
		{
			case KeyEvent.VK_LEFT:
				view.setSpeedX(view.getSpeedX() * .9);
				view.setSpeedY(view.getSpeedY() * .9);
				break;
			case KeyEvent.VK_RIGHT:
				view.setSpeedX(view.getSpeedX() * 1.1);
				view.setSpeedY(view.getSpeedY() * 1.1);
				break;
			case KeyEvent.VK_1:
				view.setPolygonType(1);
				break;
			case KeyEvent.VK_2:
				view.setPolygonType(2);
				break;
			case KeyEvent.VK_3:
				view.setPolygonType(3);
				break;
			case KeyEvent.VK_4:
				view.setPolygonType(4);
				break;
			case KeyEvent.VK_DELETE:
				view.clear();
				return;
		}

		view.setOrigin(p);
	}
}

//******************************************************************************
