package edu.stanford.cs108.bunnyworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tammerbarkouki on 3/14/18.
 */

public class Flicker {
    private Page page;
    private Shape draggingShape;
    private List<Shape> pageShapes;
    private Document document;
    private LinearLayout linearLayout;

    public Flicker(Shape draggingShape, ViewParent viewParent) {
        this.draggingShape = draggingShape;
        if(viewParent.toString().contains("Document")) {

            this.document = (Document) viewParent;

            int childCount = document.getChildCount();
            for(int i = 0; i < childCount; i++) {
                View child = document.getChildAt(i);
                if(child.isShown() && child.toString().contains("Page")) {
                    this.page = (Page) child;
                    this.pageShapes = page.getShapes();
                }
            }
        } else if(viewParent.toString().contains("LinearLayout")){

            this.linearLayout = (LinearLayout) viewParent;

            int childCount = linearLayout.getChildCount();
            for(int i = 0; i < childCount; i++) {
                View child = linearLayout.getChildAt(i);
                if(child.isShown() && child.toString().contains("Page")) {
                    this.page = (Page) child;
                    this.pageShapes = page.getShapes();
                }
            }
        }



        setDrawRectShapeNames();
    }

    public Flicker(Shape draggingShape, Page page) {
        this.draggingShape = draggingShape;
        this.page = page;
        this.pageShapes = page.getShapes();
        setDrawRectShapeNames();
    }

    void setDrawRectShapeNames() {
        List<Shape> drawRectShapes = new ArrayList<>();
        for (Shape pageShape : pageShapes) {

            if (pageShape.isVisible()) {
                List<String> getOnDropNames = pageShape.getOnDropShapes();
                System.out.println("FLICKER class " + pageShape.getName() + " is visible");

                for (String onDroppableShape : getOnDropNames) {
                    System.out.println("FLICKER class " + pageShape.getName() + " has onDrop for " + onDroppableShape);

                    if (onDroppableShape.equals(draggingShape.getName())) {
                        System.out.println("FLICKER class DRAW RECTANGLE!!!");

                        drawRectShapes.add(pageShape);

 /*                       this.rectX1 = pageShape.getX1();
                        this.rectY1 = pageShape.getY1();
                        this.rectX2 = pageShape.getX2();
                        this.rectY2 = pageShape.getY2();
                        System.out.println("FLICKER shape's xy " + pageShape.getX1() + " " + pageShape.getY1()
                                + " " + pageShape.getX2() + " " + pageShape.getY2());
                                */
                    }
                }
                pageShape.onDropShapes.clear();
            }
        }
        System.out.println("FLICKER drawRectShapes in flicker class size " + drawRectShapes.size());
        page.setDrawRectShapes(drawRectShapes);
    }

    public Page getPage() {
        return page;
    }

}
