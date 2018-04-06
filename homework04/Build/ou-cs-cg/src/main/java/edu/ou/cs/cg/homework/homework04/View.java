//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Mar  1 18:52:22 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
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
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;

import main.java.edu.ou.cs.cg.homework.homework04.Vector;

//******************************************************************************

/**
 * The <CODE>Interaction</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class View
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final int				DEFAULT_FRAMES_PER_SECOND = 60;
	private static final DecimalFormat	FORMAT = new DecimalFormat("0.000");

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final GLJPanel			canvas;
	private int						w;				// Canvas width
	private int						h;				// Canvas height

	private final KeyHandler		keyHandler;
	private final MouseHandler		mouseHandler;

	private final FPSAnimator		animator;
	private int						counter = 0;	// Frame display counter

	private TextRenderer			renderer;

	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points

	private Point2D.Double point = new Point2D.Double(0,0);
	private double speedx = .0015;
	private double speedy = .0017;

	private int polygonType = 1;

	private Vector[] outerHexagon = new Vector[7];
	private Vector[] outerCircle = new Vector[33];

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public View(GLJPanel canvas)
	{
		this.canvas = canvas;

		// Initialize model
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;
		points = new ArrayList<Point2D.Double>();

		// Initialize rendering
		canvas.addGLEventListener(this);
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		animator.start();

		// Initialize interaction
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler(this);
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************

	public int	getWidth()
	{
		return w;
	}

	public int	getHeight()
	{
		return h;
	}

	public Point2D.Double	getOrigin()
	{
		return new Point2D.Double(origin.x, origin.y);
	}

	public void		setOrigin(Point2D.Double origin)
	{
		this.origin.x = origin.x;
		this.origin.y = origin.y;
		canvas.repaint();
	}

	public Point2D.Double	getCursor()
	{
		return cursor;
	}

	public void		setCursor(Point2D.Double cursor)
	{
		this.cursor = cursor;
		canvas.repaint();
	}

	public void		clear()
	{
		points.clear();
		canvas.repaint();
	}

	public void		add(Point2D.Double p)
	{
		points.add(p);
		canvas.repaint();
	}

	public void setPoint(Point2D.Double p)
	{
		this.point = p;
		canvas.repaint();
	}
	public void setSpeedX(double x)
	{
		this.speedx = x;
	}

	public void setSpeedY(double y)
	{
		this.speedy = y;
	}

	public double getSpeedX()
	{
		return this.speedx;
	}
	
	public double getSpeedY()
	{
		return this.speedy;
	}

	public void setPolygonType(int n)
	{
		this.polygonType = n;
		canvas.repaint();
	}

	//**********************************************************************
	// Public Methods
	//**********************************************************************

	public Component	getComponent()
	{
		return (Component)canvas;
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Monospaced", Font.PLAIN, 12),
									true, true);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	public void		display(GLAutoDrawable drawable)
	{
		updateProjection(drawable);

		update(drawable);
		render(drawable);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Viewport)
	//**********************************************************************

	private void	updateProjection(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();
		GLU		glu = new GLU();

		float	xmin = (float)(origin.x - 1.0);
		float	xmax = (float)(origin.x + 1.0);
		float	ymin = (float)(origin.y - 1.0);
		float	ymax = (float)(origin.y + 1.0);

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(xmin, xmax, ymin, ymax);		// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update(GLAutoDrawable drawable)
	{
		counter++;								// Counters are useful, right?
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		drawBounds(gl);							// Unit bounding box
		drawAxes(gl);							// X and Y axes
		drawCursor(gl);							// Crosshairs at mouse location
		drawCursorCoordinates(drawable);		// Draw some text
		drawPolyline(gl);						// Draw the user's sketch

		drawBox(gl);
		drawPoint(gl);
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void	drawBounds(GL2 gl)
	{
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glVertex2d(1.0, 1.0);
		gl.glVertex2d(-1.0, 1.0);
		gl.glVertex2d(-1.0, -1.0);
		gl.glVertex2d(1.0, -1.0);

		gl.glEnd();
	}

	private void drawBox(GL2 gl)
	{

		if(polygonType == 1) //square
		{
			gl.glColor3f(1.0f, 0f, 0f);
			gl.glBegin(GL.GL_LINE_LOOP);
	
			gl.glVertex2d(.9, .9);
			gl.glVertex2d(-.9, .9);
			gl.glVertex2d(-.9, -.9);
			gl.glVertex2d(.9, -.9);
	
			gl.glEnd();
		}
		else if(polygonType == 2) //hexagon
		{
			int index = 0;


			for (int i=0; i<420; i+=60)			
			{
				
				outerHexagon[index] = (new Vector(.9 * Math.cos(i*Math.PI/180), .9*Math.sin(i*Math.PI/180)));
				//gl.glVertex2d( .9 * Math.cos(i*Math.PI/180),  .9 * Math.sin(i*Math.PI/180));
				index++;
			}

			gl.glColor3f(1.0f, 0f, 0f);
			gl.glBegin(GL.GL_LINE_LOOP);

			for(int k = 0; k<outerHexagon.length; k++)
			{
				gl.glVertex2d(outerHexagon[k].x, outerHexagon[k].y);
			}
			gl.glEnd();
		}
		else if(polygonType == 3) //32 gon circle
		{
			int index = 0;


			for (double i=0; i<371.25; i+=11.25)			
			{
				outerCircle[index] = (new Vector(.9*Math.cos(i*Math.PI/180), .9*Math.sin(i*Math.PI/180)));
				//gl.glVertex2d( .9 * Math.cos(i*Math.PI/180),  .9 * Math.sin(i*Math.PI/180));
				index++;
			}

			gl.glColor3f(1.0f, 0f, 0f);
			gl.glBegin(GL.GL_LINE_LOOP);

			for(int k = 0; k<outerCircle.length; k++)
			{
				gl.glVertex2d(outerCircle[k].x, outerCircle[k].y);
			}
			gl.glEnd();
		}
		else if(polygonType == 4) //odd shape 
		{

		}
		
	}

	private void drawPoint(GL2 gl)
	{
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glPointSize(5.0f);
		gl.glVertex2d(point.x, point.y);
		gl.glEnd();

		if(polygonType ==1)
		{
			if(point.x + speedx > .9 || point.x + speedx < -.9)
			{
				setSpeedX(speedx * -1);
			}
			if(point.y + speedy > .9 || point.y + speedy < -.9)
			{
				setSpeedY(speedy * -1);
			}
			setPoint(new Point2D.Double(point.x + speedx, point.y + speedy));
		}
		else if(polygonType == 2)
		{
			double nextx = point.x + speedx;
			double nexty = point.y + speedy;

			boolean isReflected = false;

			for(int i = 0; i<outerHexagon.length; i++)
			{
				if(i+1 < outerHexagon.length)
				{
					if(crossproduct(outerHexagon[i], outerHexagon[i+1], nextx, nexty) > 0)
					{
						isReflected = true;
					}
				}
				else
				{
					if(crossproduct(outerHexagon[i], outerHexagon[0], nextx, nexty) > 0)
					{
						isReflected = true;
					}
				}
			}

			if(isReflected)
			{
				setSpeedX(speedx * -1);
				setSpeedY(speedy * -1);
			}

			setPoint(new Point2D.Double(point.x + speedx, point.y + speedy));

		}
		else if(polygonType == 3)
		{
			double nextx = point.x + speedx;
			double nexty = point.y + speedy;

			boolean isReflected = false;

			for(int i = 0; i<outerCircle.length; i++)
			{
				if(i+1 < outerCircle.length)
				{
					if(crossproduct(outerCircle[i], outerCircle[i+1], nextx, nexty) > 0)
					{
						isReflected = true;
					}
				}
				else
				{
					if(crossproduct(outerCircle[i], outerCircle[0], nextx, nexty) > 0)
					{
						isReflected = true;
					}
				}
			}

			if(isReflected)
			{
				setSpeedX(speedx * -1);
				setSpeedY(speedy * -1);
			}

			setPoint(new Point2D.Double(point.x + speedx, point.y + speedy));
		}
		else if(polygonType == 4)
		{
			
		}


	}

	private double crossproduct(Vector line, Vector nextLine,  double speedx, double speedy)
	{
		Vector tipMinusPoint = new Vector(nextLine.x - speedx, nextLine.y - speedy);
		Vector tailMinusPoint = new Vector(line.x - speedx, line.y - speedy);

		return (tipMinusPoint.x * tailMinusPoint.y - tipMinusPoint.y * tailMinusPoint.x);
		//System.out.println((line.x * speedy - line.y * speedx));
		//return (line.x * speedy - line.y * speedx);
	}

	private void	drawAxes(GL2 gl)
	{
		// gl.glBegin(GL.GL_LINES);

		// gl.glColor3f(0.25f, 0.25f, 0.25f);
		// gl.glVertex2d(-10.0, 0.0);
		// gl.glVertex2d(10.0, 0.0);

		// gl.glVertex2d(0.0, -10.0);
		// gl.glVertex2d(0.0, 10.0);

		// gl.glEnd();
	}

	private void	drawCursor(GL2 gl)
	{
		// if (cursor == null)
		// 	return;

		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glColor3f(0.5f, 0.5f, 0.5f);

		// for (int i=0; i<32; i++)
		// {
		// 	double	theta = (2.0 * Math.PI) * (i / 32.0);

		// 	gl.glVertex2d(cursor.x + 0.05 * Math.cos(theta),
		// 				  cursor.y + 0.05 * Math.sin(theta));
		// }

		// gl.glEnd();
	}

	private void	drawCursorCoordinates(GLAutoDrawable drawable)
	{
		// if (cursor == null)
		// 	return;

		// String	sx = FORMAT.format(new Double(cursor.x));
		// String	sy = FORMAT.format(new Double(cursor.y));
		// String	s = "(" + sx + "," + sy + ")";

		// renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		// renderer.setColor(1.0f, 1.0f, 0, 1.0f);
		// renderer.draw(s, 2, 2);
		// renderer.endRendering();
	}

	private void	drawPolyline(GL2 gl)
	{
		// gl.glColor3f(1.0f, 0.0f, 0.0f);

		// for (Point2D.Double p : points)
		// {
		// 	gl.glBegin(GL2.GL_POLYGON);

		// 	gl.glVertex2d(p.x - 0.01, p.y - 0.01);
		// 	gl.glVertex2d(p.x - 0.01, p.y + 0.01);
		// 	gl.glVertex2d(p.x + 0.01, p.y + 0.01);
		// 	gl.glVertex2d(p.x + 0.01, p.y - 0.01);

		// 	gl.glEnd();
		// }

		// gl.glColor3f(1.0f, 1.0f, 0.0f);
		// gl.glBegin(GL.GL_LINE_STRIP);

		// for (Point2D.Double p : points)
		// 	gl.glVertex2d(p.x, p.y);

		// gl.glEnd();
	}
}

//******************************************************************************
