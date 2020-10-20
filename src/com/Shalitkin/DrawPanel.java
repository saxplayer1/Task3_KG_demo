package com.Shalitkin;



import com.Shalitkin.PixelDrawers.BufferedImagePixelDrawer;
import com.Shalitkin.PixelDrawers.PixelDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    public DrawPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    private ArrayList<Line> lines = new ArrayList<>();
    private ScreenConverter sc = new ScreenConverter(-2,2,4,4,800,600);

    private Line xAxis = new Line(0,-1,0,1);
    private Line yAxis = new Line(-1,0,1,0);
    private ScreenPoint prevDrag;
    private Line currentLine = null;

    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        sc.setScreenW(getWidth());
        sc.setScreenH(getHeight());
        Graphics bi_g = bi.createGraphics();
        bi_g.setColor(Color.white);
        bi_g.fillRect(0,0,getWidth(),getHeight());
        bi_g.dispose();
        PixelDrawer pd = new BufferedImagePixelDrawer(bi);
        LineDrawer ld = new BresenhaimLineDrawer(pd);//Порождающий патерн, абстрактная фабрика
        drawLine(ld,xAxis);
        drawLine(ld,yAxis);
        for (Line l : lines) {
            drawLine(ld,l);
        }
        if (currentLine != null) {
            drawLine(ld, currentLine);
        }
        //ld.drawLine(sc.r2s(l.getP1()),sc.r2s(l.getP2()));
        //drawAll(ld);
        g.drawImage(bi, 0, 0, null);
        //bi_g.dispose();
    }

    private void drawLine (LineDrawer ld, Line l){
        ld.drawLine(sc.r2s(l.getP1()),sc.r2s(l.getP2()));
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            prevDrag = new ScreenPoint(e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            currentLine = new Line(
                            sc.s2r(new ScreenPoint(e.getX(), e.getY())),
                            sc.s2r((new ScreenPoint(e.getX(), e.getY())))
            );

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            prevDrag = null;
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            lines.add(currentLine);
            currentLine = null;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ScreenPoint current = new ScreenPoint(e.getX(), e.getY());
            if (prevDrag != null) {
                ScreenPoint delta = new ScreenPoint(
                        current.getX() - prevDrag.getX(),
                        current.getY() - prevDrag.getY());
                RealPoint deltaReal = sc.s2r(delta);
                RealPoint zeroReal = sc.s2r(new ScreenPoint(0, 0));
                RealPoint vector = new RealPoint(
                        deltaReal.getX() - zeroReal.getX(),
                        deltaReal.getY() - zeroReal.getY());
                sc.setX(sc.getX() - vector.getX());
                sc.setY(sc.getY() - vector.getY());

                prevDrag = current;
            }
            if (currentLine != null) {
                currentLine.setP2(sc.s2r(current));
            }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double coef = clicks > 0 ? 0.9 : 1.1;
        for (int i = 0; i < Math.abs(clicks); i++) {
            scale *= coef;
        }
        sc.setW(sc.getW() * scale);
        sc.setH(sc.getH() * scale);
        repaint();
    }
}
