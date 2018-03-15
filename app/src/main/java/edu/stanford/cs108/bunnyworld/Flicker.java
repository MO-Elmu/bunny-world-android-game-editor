package edu.stanford.cs108.bunnyworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;

import java.util.List;

/**
 * Created by tammerbarkouki on 3/14/18.
 */

public class Flicker {
    private Page page;
    private Shape draggingShape;
    private List<Shape> pageShapes;
    private Document document;
    private float STROKE_WIDTH = 10.0f;
    int rectX1 = 0;
    int rectY1 = 0;
    int rectX2 = 0;
    int rectY2 = 0;

    Paint boundaryPaint;

    public Flicker(Shape draggingShape, ViewParent viewParent) {
        this.draggingShape = draggingShape;
        this.document = (Document) viewParent;

        boundaryPaint = new Paint();
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(STROKE_WIDTH);
        boundaryPaint.setColor(Color.rgb(0, 255, 0));

        int childCount = document.getChildCount();
        for(int i = 0; i < childCount; i++) {
            View child = document.getChildAt(i);
            System.out.println("FLICKER document child: " + child.toString());
            if(child.isShown() && child.toString().contains("Page")) {
                this.page = (Page) child;
                this.pageShapes = page.getShapes();
            }
        }
        drawRectangle();
    }

    void drawRectangle() {
        for (Shape pageShape : pageShapes) {

            if (pageShape.isVisible()) {
                List<String> getOnDropNames = pageShape.getOnDropShapes();
                System.out.println("FLICKER " + pageShape.getName() + " is visible");

                for (String onDroppableShape : getOnDropNames) {
                    System.out.println("FLICKER " + pageShape.getName() + " has onDrop for " + onDroppableShape);

                    if (onDroppableShape.equals(draggingShape.getName())) {
                        System.out.println("FLICKER DRAW RECTANGLE!!!");

                        this.rectX1 = pageShape.getX1();
                        this.rectY1 = pageShape.getY1();
                        this.rectX2 = pageShape.getX2();
                        this.rectY2 = pageShape.getY2();
                        System.out.println("FLICKER shape's xy " + pageShape.getX1() + " " + pageShape.getY1()
                                + " " + pageShape.getX2() + " " + pageShape.getY2());
                    }
                }
                pageShape.onDropShapes.clear();
            }
        }
    }

    public Page getPage() {
        return page;
    }

    public int getRectX1() {
        return rectX1;
    }

    public int getRectY1() {
        return rectY1;
    }

    public int getRectX2() {
        return rectX2;
    }

    public int getRectY2() {
        return rectY2;
    }

    public Paint getBoundaryPaint() {
        return boundaryPaint;
    }
}
