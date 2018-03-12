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
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class Page extends View {
    protected List<Shape> shapes = new ArrayList<>();
    private int shapeCounter = 0;
    private Shape selectedShape;
    private int prevX,prevY, pX, pY;
    private boolean visibility;
    private String pageName = "";
    private boolean dragging = false;
    private List<RectF> boundingRects;


    private class TapGestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Toast toast = Toast.makeText(getContext(), "You are in "+ getPageName(), Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }

    }

    private GestureDetectorCompat mDetector;



    /****** View.DragShadowBuilder methods overridden by the our ShapeDragShadowBuilder Class ******/

    /*** All this is done to allow us to use the Drag and Drop frame work to move
     * Shapes between different views like the page view and possessions view and since
     * the Shape class isn't a view class we hav to override onProvideShadowMetrics()
     * and onDrawShadow() methods in the DragShadowBuilder.
     */

    private class ShapeDragShadowBuilder extends View.DragShadowBuilder {
        @Override
        public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
            //super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
            if(selectedShape != null) {
                outShadowSize.set(400, 400);
                outShadowTouchPoint.set(selectedShape.getWidth()/2, selectedShape.getHeight()/2);
            }

        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            //super.onDrawShadow(canvas);
            //if(selectedShape != null) {
                selectedShape.drawSelf(canvas, Page.this.getContext());
            //}
        }

    }


    public Page(Context context) {
        super(context);
        init(null, 0);
        LinearLayout.LayoutParams lpPages = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0, 0.7f);
        this.setLayoutParams(lpPages);
        mDetector = new GestureDetectorCompat(context, new TapGestureListener());
    }

    public Page(Context context, Boolean visibility) {
        super(context);
        this.visibility = visibility;
    }

    public Page(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Page(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
       // this.setBackgroundColor(Color.WHITE);  //Page background is white (specs)
    }






    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = (int)event.getX();
                y = (int)event.getY();
                prevX = x;
                prevY = y;
                selectedShape = null;  //nullify the previously selected shape
                if(!shapes.isEmpty()){
                    for (Shape sh : shapes) {
                        if (sh.contains(x,y)) {
                            selectedShape = sh;  //select last added shape to the page
                            //DragShadowBuilder shapeShadowBuilder = new ShapeDragShadowBuilder();
                            //this.startDrag(null, shapeShadowBuilder, null, 0);
                            //selectedShape.setVisible(false);
                            //invalidate();
                        }
                    }
                    //if the clicked shape has an on click action scripts execute it
                    if(selectedShape != null) selectedShape.execOnClickScript(getContext(),(ViewGroup)this.getParent(),this);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(selectedShape != null) {
                    DragShadowBuilder shapeShadowBuilder = ImageDragShadowBuilder.fromResource(getContext(),selectedShape.imageIdentifier);
                    ClipData.Item item1_shapeName = new ClipData.Item(selectedShape.getName());
                    ClipData.Item item2_imageId = new ClipData.Item(selectedShape.imageName);
                    String mimeTypes[] = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData draggedShape = new ClipData(selectedShape.getName(), mimeTypes, item1_shapeName);
                    draggedShape.addItem(item2_imageId);
                    this.startDrag(draggedShape, shapeShadowBuilder, null, 0);
                    //selectedShape.setVisible(false);
                    //invalidate();
                    /*currX = (int) event.getX();
                    currY = (int) event.getY();
                    xOffset = currX - prevX;
                    yOffset = currY - prevY;
                    prevX = currX;
                    prevY = currY;
                    selectedShape.setX1(selectedShape.getX1() + xOffset);
                    selectedShape.setY1(selectedShape.getY1() + yOffset);
                    selectedShape.setX2(selectedShape.getX1() + selectedShape.getWidth());
                    selectedShape.setY2(selectedShape.getY1() + selectedShape.getHeight());
                    invalidate();*/
                }

                break;
            case MotionEvent.ACTION_UP:
                /*int relX, relY;
                relX = (int) event.getX();
                relY = (int) event.getY();
                if(selectedShape != null) {
                    if (!shapes.isEmpty()) {
                        for (Shape sh : shapes) {
                            if (sh.contains(relX, relY)) {
                                sh.execOnDropScript(getContext(), (ViewGroup) this.getParent(), this, selectedShape.getName());
                            }
                        }
                    }
                }*/
                selectedShape = null; //nullify selected shape when the user lift his finger
                System.out.println("ACTION_UP called !!!!!!!!");
                //invalidate();
                break;

        }
//        mDetector.onTouchEvent(event);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setBackgroundColor(Color.WHITE);
        if(!shapes.isEmpty()){
            Iterator<Shape> it = shapes.iterator();
            while (it.hasNext()) {
                Shape sh = it.next();
                if(sh.isInPossession())  {
                    it.remove();
                    shapeCounter -= 1;
                }
                sh.drawSelf(canvas, this.getContext());
                if(sh.isVisible() && sh.isOnDrop() && dragging) {
                    flicker(canvas, sh);
                }
            }
        }
    }

    //overridden to count for onEnter Scripts if it exists for any of the page's shapes
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == View.VISIBLE){
            for (Shape sh : shapes) {
                 sh.execOnEnterScript(getContext(),(ViewGroup)this.getParent(),this);
            }
        }
    }

    public void addShape(Shape shape){
        shapeCounter += 1;
        if(shape.getName().trim().isEmpty()) shape.setName("shape" + shapeCounter);
        shapes.add(shape);
        invalidate();
    }

    /*public void hideShape(int shapeId){

    }
    public void onClick(View v){

    }*/

    @Override
    public boolean onDragEvent(DragEvent event) {
        int x, y;
        final int action = event.getAction();
        // Handles all the expected events
        switch(action) {
            case DragEvent.ACTION_DRAG_STARTED:
                System.out.println("ACTION_DRAG_STARTED In page");
                if(selectedShape != null) {
                    selectedShape.setVisible(false);
                    dragging = true;
                    invalidate();
                }
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                System.out.println("ACTION_DRAG_ENTERED In page");
                if(selectedShape != null) selectedShape.setInPossession(false);
                dragging = true;
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                System.out.println("ACTION_DRAG_EXITED In page");
                if(selectedShape != null) selectedShape.setInPossession(true);
                dragging = true;
                return true;

            case DragEvent.ACTION_DROP:
                System.out.println("ACTION_DRAG_DROP In page");
                dragging = false;
                //if()
                selectedShape = new Shape(event.getClipData().getItemAt(1).getText().toString(), this.getContext());
                selectedShape.setName(event.getClipData().getItemAt(0).getText().toString());
                this.addShape(selectedShape);
                if(selectedShape != null) {
                    int currX, currY;
                    currX = (int) event.getX();
                    currY = (int) event.getY();
                    selectedShape.setX1(currX - (selectedShape.getWidth()/2));
                    selectedShape.setY1(currY -(selectedShape.getHeight()/2));
                    selectedShape.setX2(currX + (selectedShape.getWidth()/2));
                    selectedShape.setY2(currY + (selectedShape.getHeight()/2));
                    selectedShape.setVisible(true);
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                return false;

            case DragEvent.ACTION_DRAG_ENDED:
                System.out.println("ACTION_DRAG_ENDED In page");
                dragging = false;
                if(event.getResult()){
                    invalidate();
                    return true;
                }
                selectedShape.setVisible(true);
                invalidate();
                return true;

            // An unknown action type was received.
            default:
                return true;
        }


        //return false;
    }

    void flicker(Canvas canvas, Shape sh) {
        System.out.println("FLICKER");
        int rectX1 = sh.getX1();
        int rectY1 = sh.getY1();
        int rectX2 = sh.getX2();
        int rectY2 = sh.getY2();
        System.out.println("trying to draw a rectangle FLICKER");
        Paint boundaryPaint =  new Paint();
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(5.0f);
        boundaryPaint.setColor(Color.rgb(0,255,0));
        canvas.drawRect(rectX1-10, rectY1+10, rectX2+10, rectY2-10, boundaryPaint);
    }




    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }



}
