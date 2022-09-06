package com.shpp.p2p.cs.oholubovskyi.nameSurfer;

/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes
 * or the window is resized.
 */

import acm.graphics.*;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class NameSurferGraph extends GCanvas
        implements NameSurferConstants, ComponentListener {

    /* Stores the NameSurferEntry's for the added names */
    private final ArrayList<NameSurferEntry> nameList = new ArrayList<>();

    /* Stores the GCompounds that represent the added names to the graph */
    private final ArrayList<GCompound> surfers = new ArrayList<>();

    /*step increment of decades*/
    private double stepDecades;

    /*step increment of ranks*/
    private double stepRanks;

    private double coefficientChangeSizeText;

    Font fontBold;

    /**
     * Creates a new NameSurferGraph object that displays the data.
     */

    public NameSurferGraph() {
        addComponentListener(this);
        update();
    }

    /**
     * Clears the list of name surfer entries stored inside this class.
     */
    public void clear() {
        for (GCompound surfer : surfers) {
            remove(surfer);
        }
        nameList.clear();
    }

    /**
     * Adds a new NameSurferEntry to the list of entries on the display.
     * Note that this method does not actually draw the graph, but
     * simply stores the entry; the graph is drawn by calling update.
     */
    public void addEntry(NameSurferEntry entry) {
        nameList.add(entry);
    }

    /**
     * Updates the display image by deleting all the graphical objects
     * from the canvas and then reassembling the display according to
     * the list of entries. Your application must call update after
     * calling either clear or addEntry; update is also called whenever
     * the size of the canvas changes.
     */
    public void update() {
        removeAll();
        drawGrid();
        createCompounds();
    }

    /**
     * Iterates through the list of added names
     * and creates the required graphical objects.
     */
    private void createCompounds() {
        for (int i = 0; i < nameList.size(); i++) {
            add(createSurfGraph(nameList.get(i), i));
        }
    }

    /**
     * creates a GCompound for the given NameSurferEntry.
     * The GCompound consists of a GLines (graph lines)
     * and a GLabels (names + ranks).
     */
    private GCompound createSurfGraph(NameSurferEntry entry, int count) {
        count = count % 4;
        GCompound surfCompound = new GCompound();
        surfCompound.add(createSurfLabels(entry, count));
        surfCompound.add(createSurfLines(entry, count));
        surfers.add(surfCompound);
        return surfCompound;
    }

    /**
     * creates a GCompound containing the GLabels for the given
     * NameSurferEntry.
     */
    private GCompound createSurfLabels(NameSurferEntry entry, int count) {

        GCompound labels = new GCompound();




        Font fontPlain = new Font(Font.SERIF, Font.PLAIN, (int) (SIZE_TEXT * 0.8 * coefficientChangeSizeText));

        /*set step ranks, based on current window height*/
        stepRanks = (getHeight() - (GRAPH_MARGIN_SIZE * 2.0)) / MAX_RANK;

        double x = 0;

        /*create label for each decade*/
        for (int i = 0; i < NDECADES; i++) {

            /*set Y coordinate based on rank*/
            double y = (entry.getRank(i) * stepRanks) + GRAPH_MARGIN_SIZE;

            String name = entry.getName();
            GLabel surfLabel = new GLabel(name);
            /*
             * If the rank is zero, pin the label
             * to the bottom of the graph and display a '*'.
             */
            if (entry.getRank(i) == 0) {
                y = getHeight() - GRAPH_MARGIN_SIZE - surfLabel.getAscent() * count;
                surfLabel = new GLabel(name + "*");
                surfLabel.setFont(fontPlain);

            } else {
                surfLabel = new GLabel(name + " ");
                surfLabel.setFont(fontBold);
            }

            /*daw the surf label upper point or under point depend on
            * moving line (up or down)*/
            if (i != NDECADES - 1 && entry.getRank(i) > entry.getRank(i + 1)) {
                y += surfLabel.getAscent();
            } else {
                y -= surfLabel.getDescent();
            }

            surfLabel.setColor(getColor(count));
            labels.add(surfLabel, x, y);

            x += stepDecades;
        }
        return labels;
    }

    /**
     * creates a GCompound containing the GLines for the given
     * NameSurferEntry.
     */
    private GCompound createSurfLines(NameSurferEntry entry, int count) {

        GCompound lines = new GCompound();

        /*coordinates of the points*/
        double x1 = 0, x2;
        double y1, y2;

        /*create line for decade rank*/
        for (int i = 0; i < NDECADES - 1; i++) {

            x2 = x1 + stepDecades;

            /*calculate y1 and y2, if rank is zero, set Y coordinate to bottom of graph*/
            if (entry.getRank(i) == 0) {
                y1 = getHeight() - GRAPH_MARGIN_SIZE;
            } else {
                y1 = entry.getRank(i) * stepRanks + GRAPH_MARGIN_SIZE;
                lines.add(drawPoint(count, x1, y1));
            }

            if (entry.getRank(i + 1) == 0) {
                y2 = getHeight() - GRAPH_MARGIN_SIZE;
            } else {
                y2 = entry.getRank(i + 1) * stepRanks + GRAPH_MARGIN_SIZE;
                lines.add(drawPoint(count, x2, y2));
            }

            /* create line*/
            GLine surfLine = new GLine(x1, y1, x2, y2);
            surfLine.setColor(getColor(count));

            /*add line to GCompound*/
            lines.add(surfLine);

            x1 += stepDecades;
        }
        return lines;
    }

    /**
     * method return point like an oval, rectangle, triangle, diamond based on "count"
     */
    private GObject drawPoint(int count, double x, double y) {
        GObject object;

        switch (count) {
            case 0 -> object = drawTriangle(x, y, count);
            case 1 -> object = drawRectangle(x, y, count);
            case 2 -> object = drawDiamond(x, y, count);
            default -> object = drawOval(x,y,count);
        }
        add(object);
        return object;
    }

    /**
     * return object like a rectangle
     */
    private GObject drawRectangle(double x, double y, int count) {
        var object = new GRect(x - DIAMETER_POINT / 2., y - DIAMETER_POINT / 2.,
                DIAMETER_POINT, DIAMETER_POINT);
        object.setColor(getColor(count));
        object.setFilled(true);
        return object;
    }

    /**
     * return object like a oval
     * */
    private GObject drawOval(double x, double y, int count) {
        var object = new GOval(x - DIAMETER_POINT / 2., y - DIAMETER_POINT / 2.,
                DIAMETER_POINT, DIAMETER_POINT);
        object.setColor(getColor(count));
        object.setFilled(true);
        return object;
    }

    /**
     * return object like a triangle
     * */
    private GObject drawTriangle(double x, double y, int count) {
        GPolygon object = new GPolygon();
        object.addVertex(x - DIAMETER_POINT * 2 / 3., y);
        object.addVertex(x , y - DIAMETER_POINT * 2 / 3.);
        object.addVertex(x + DIAMETER_POINT * 2 / 3., y);

        object.setColor(getColor(count));
        object.setFilled(true);
        return object;
    }

    /**
     * return object like a diamond
     * */
    private GObject drawDiamond(double x, double y, int count) {
        GPolygon object = new GPolygon();
        object.addVertex(x - DIAMETER_POINT * 2 / 3., y);
        object.addVertex(x , y - DIAMETER_POINT * 2 / 3.);
        object.addVertex(x + DIAMETER_POINT * 2 / 3., y);
        object.addVertex(x, y + DIAMETER_POINT * 2 / 3.);

        object.setColor(getColor(count));
        object.setFilled(true);
        return object;
    }

    /**
     * Returns a color based on the counter.
     */
    private Color getColor(int colorCount) {
        return switch (colorCount) {
            case 0 -> Color.blue;
            case 1 -> Color.red;
            case 2 -> Color.magenta;
            default -> Color.black;
        };
    }

    /**
     * Draws the grid, to which the names will be added.
     */
    private void drawGrid() {

        /*set decade step*/
        stepDecades = getWidth() / (double) NDECADES;
        coefficientChangeSizeText = (getHeight() + getWidth())/ (double)(APPLICATION_HEIGHT + APPLICATION_WIDTH);
        fontBold = new Font(Font.SERIF, Font.BOLD, (int) (SIZE_TEXT * coefficientChangeSizeText));

        /*add upper and lower margin lines*/
        add(new GLine(0, getHeight() - GRAPH_MARGIN_SIZE, getWidth(), getHeight() - GRAPH_MARGIN_SIZE));
        add(new GLine(0, GRAPH_MARGIN_SIZE, getWidth(), GRAPH_MARGIN_SIZE));

        /*draw vertical lines and decade labels X Axis*/
        double verticalStart = 0;
        int startDate = START_DECADE;
        for (int i = 0; i < NDECADES; i++) {
            add(new GLine(verticalStart, 0, verticalStart, getHeight()));

            GLabel label = new GLabel(Integer.toString(startDate), verticalStart + (stepDecades / 4), getHeight() - (GRAPH_MARGIN_SIZE / 4.));
            label.setFont(fontBold);
            add(label);

            /*increment date value*/
            startDate += 10;

            /*increment on the X Axis*/
            verticalStart += stepDecades;
        }
    }

    /* Implementation of the ComponentListener interface */
    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        update();
    }

    public void componentShown(ComponentEvent e) {
    }


}
