package edu.stanford.cs108.bunnyworld;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class Possessions extends View {


    private List<Shape> shapes = new ArrayList<>();
    //this "shapeCounter" param should not be called to retrieve the number of shapes in this view
    // since it only used to keep the naming scheme of shapes incremented by 1 everytime
    //the user doesn't bother to enter a shape name. Instead classes should get the accurate
    // number of shapes from the shapes List.
    private int shapeCounter = 0;
    private Shape selectedShape;
    private boolean isDragging = false;


    public Possessions(Context context) {
        super(context);
        init(null, 0);
        LinearLayout.LayoutParams lpPossessions = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        this.setLayoutParams(lpPossessions);
    }

    public Possessions(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Possessions(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }
    public void addShape(Shape shape){
        shapeCounter += 1;
        if(shape.getName().trim().isEmpty()) shape.setName("shape" + shapeCounter);
        shapes.add(shape);
        invalidate();
    }



    /****** View.DragShadowBuilder methods overridden by the our ShapeDragShadowBuilder Class ******/

    /*** All this is done to allow us to use the Drag and Drop frame work to move
     * Shapes between different views like the page view and possessions view and since
     * the Shape class isn't a view class we hav to override onProvideShadowMetrics()
     * and onDrawShadow() methods in the DragShadowBuilder.
     */

    private class ShapeDragShadowBuilder extends View.DragShadowBuilder {
        @Override
        public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
            if(selectedShape != null) {
                outShadowSize.set(selectedShape.getWidth()*2, selectedShape.getHeight()*2);
                outShadowTouchPoint.set(selectedShape.getWidth()/2, selectedShape.getHeight()/2);
            }

        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            if(selectedShape != null) {
                selectedShape.drawSelf(canvas, Possessions.this.getContext());
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = (int)event.getX();
                y = (int)event.getY();
                selectedShape = null;  //nullify the previously selected shape
                if(!shapes.isEmpty()){
                    for (Shape sh : shapes) {
                        if (sh.contains(x,y)) selectedShape = sh;  //select last added shape to the page
                    }
                }

                if(selectedShape != null) {
                    DragShadowBuilder shapeShadowBuilder = ImageDragShadowBuilder.fromResource(getContext(),selectedShape.imageIdentifier);
                    ClipData.Item item1_shapeName = new ClipData.Item(selectedShape.getName());
                    ClipData.Item item2_imageId = new ClipData.Item(selectedShape.imageName);
                    String mimeTypes[] = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData draggedShape = new ClipData(selectedShape.getName(), mimeTypes, item1_shapeName);
                    draggedShape.addItem(item2_imageId);
                    this.startDrag(draggedShape, shapeShadowBuilder, null, 0);
                    selectedShape.setVisible(false);
                    invalidate();
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
/*                if(selectedShape != null) {
                    DragShadowBuilder shapeShadowBuilder = ImageDragShadowBuilder.fromResource(getContext(),selectedShape.imageIdentifier);
                    ClipData.Item item1_shapeName = new ClipData.Item(selectedShape.getName());
                    ClipData.Item item2_imageId = new ClipData.Item(selectedShape.imageName);
                    String mimeTypes[] = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData draggedShape = new ClipData(selectedShape.getName(), mimeTypes, item1_shapeName);
                    draggedShape.addItem(item2_imageId);
                    this.startDrag(draggedShape, shapeShadowBuilder, null, 0);
                    selectedShape.setVisible(false);
                    invalidate();
                }
*/

                break;
            case MotionEvent.ACTION_UP:
                selectedShape = null; //nullify selected shape when the user lift his finger
                invalidate();
                break;

        }

        return true;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        // Handles all the expected events
        switch(action) {

            case DragEvent.ACTION_DRAG_STARTED:
                this.isDragging = true;
                System.out.println("ACTION_DRAG_STARTED In Possessions");
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                this.isDragging = true;
                if(selectedShape != null) selectedShape.setInPossession(true);
                System.out.println("ACTION_DRAG_ENTERED In Possessions!!!!");
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                isDragging = true;
                //System.out.println("ACTION_DRAG_LOCATION In Possessions");
                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                isDragging = true;
                System.out.println("ACTION_DRAG_Exited In Possessions");
                if(selectedShape != null) selectedShape.setInPossession(false);
                //System.out.println("ACTION_DRAG_EXITED In Possessions");
//                invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                isDragging = false;
                int currX, currY;
                currX = (int) event.getX();
                currY = (int) event.getY();
                if(selectedShape == null) {
                    System.out.println("ACTION_DROP NULL In Possessions");
                    selectedShape = new Shape(event.getClipData().getItemAt(1).getText().toString(), this.getContext());
                    selectedShape.setName(event.getClipData().getItemAt(0).getText().toString());
                    selectedShape.setInPossession(true);
                    selectedShape.setX1(currX - (selectedShape.getWidth()/2));
                    selectedShape.setY1(currY -(selectedShape.getHeight()/2));
                    selectedShape.setX2(currX + (selectedShape.getWidth()/2));
                    selectedShape.setY2(currY + (selectedShape.getHeight()/2));
                    this.addShape(selectedShape);
 //                   organizePossessions();
                    selectedShape.setVisible(true);
                    invalidate();
                    selectedShape = null;
                    return true;
                }

                if(selectedShape != null) {
                    System.out.println("ACTION_DROP ! NULL In Possessions");
                    selectedShape.setX1(currX - (selectedShape.getWidth()/2));
                    selectedShape.setY1(currY -(selectedShape.getHeight()/2));
                    selectedShape.setX2(currX + (selectedShape.getWidth()/2));
                    selectedShape.setY2(currY + (selectedShape.getHeight()/2));
 //                   organizePossessions();
                    selectedShape.setVisible(true);
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                return false;

            case DragEvent.ACTION_DRAG_ENDED:
                isDragging = false;
                System.out.println("ACTION_DRAG_ENDED In Possessions");
                if(event.getResult()){
                    System.out.println("Drop Ended In Possessions");
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                if(selectedShape != null ){
                    System.out.println("Drop not Ended In Possessions");
                    selectedShape.setInPossession(true);
                    selectedShape.setVisible(true);
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                return false;


            //  An unknown action type was received.
            default:
                break;
        }


        return false;
    }

    private void organizePossessions() {
        System.out.println("OG Called organizePossessions");
//        int posX = Math.round(this.getX());
//        int posY = Math.round(this.getY());
        int posX = 20;
        int posY = 1700;
        System.out.println("OG PosX " + posX + " PosY " + posY);
        System.out.println(shapes.size());

        for(int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            System.out.println("OG is moveable? " + shape.isMovable());
        //    shape.setMovable(true);
            if (i == 0) {
                shape.setX1(posX);
                shape.setY1(posY);
                System.out.println("OG shapeX1 " + shape.getX1() + " shapeY1 " + shape.getY1());
                shape.setX2(posX + 200);
                shape.setY2(posY + 200);
                System.out.println("OG shapeX2 " + shape.getX2() + " shapeY2 " + shape.getY2());
            } else {
                int shapeX1 = Math.round(shape.getX1());
                int shapeY2 = Math.round(shape.getY1());
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setBackgroundColor(Color.GRAY);
        if(!shapes.isEmpty()){
            Iterator<Shape> it = shapes.iterator();
            while (it.hasNext()) {
                Shape sh = it.next();
                if(!sh.isInPossession())  it.remove();
//                organizePossessions();
                sh.drawSelf(canvas, this.getContext());
            }
        }
        if(isDragging) flicker(canvas, selectedShape);
    }
    void flicker(Canvas canvas, Shape sh) {
        if (sh == null) return;
        System.out.println("FLICKER called while dragging shape: " + sh.getName());

        for (Shape shape2 : shapes) {

            if (shape2.isVisible()) {
                List<String> getOnDropNames = shape2.getOnDropShapes();
                System.out.println("FLICKER " + shape2.getName() + " is visible");

                for (String shape3 : getOnDropNames) {
                    System.out.println("FLICKER " + shape2.getName() + " has onDrop for " + shape3);
                    if (shape3.equals(sh.getName())) {
                        System.out.println("FLICKER DRAW RECTANGLE!!!");

                        int rectX1 = shape2.getX1();
                        int rectY1 = shape2.getY1();
                        int rectX2 = shape2.getX2();
                        int rectY2 = shape2.getY2();

                        Paint boundaryPaint = new Paint();
                        boundaryPaint.setStyle(Paint.Style.STROKE);
                        boundaryPaint.setStrokeWidth(10.0f);
                        boundaryPaint.setColor(Color.rgb(0, 255, 0));
                        canvas.drawRect(rectX1 - 10, rectY1 + 10, rectX2 + 10, rectY2 - 10, boundaryPaint);
                    }
                }

                shape2.onDropShapes.clear();
            }
        }
    }


    public List<Shape> getShapes() {
        return shapes;
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }
}