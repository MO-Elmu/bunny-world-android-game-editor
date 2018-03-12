package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class Page extends View /*implements View.OnClickListener*/ {
    protected List<Shape> shapes = new ArrayList<>();
    private int shapeCounter = 0;
    //private int x, y, x1Offset, y1Offset;  //captures user touch coordinates
    private Shape selectedShape;
    private int prevX,prevY;
    private boolean visibility;
    private boolean dragging;

    private String pageName = "";


    public Page(Context context) {
        super(context);
        init(null, 0);
    }

    public Page(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Page(Context context, Boolean visibility) {
        super(context);
        this.visibility = visibility;
    }

    public Page(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // this.setBackgroundColor(Color.WHITE);  //Page background is white (specs)
    }

    public List<Shape> getShapes() {
        return shapes;
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
                    //if the clicked shape has an on click action scripts execute it
                    if(selectedShape != null) selectedShape.execOnClickScript(getContext(),(ViewGroup)this.getParent(),this);
                }
                prevX = x;
                prevY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int currX, currY, xOffset, yOffset;
                if(selectedShape != null) {
                    currX = (int) event.getX();
                    currY = (int) event.getY();
                    xOffset = currX - prevX;
                    yOffset = currY - prevY;
                    prevX = currX;
                    prevY = currY;
                    selectedShape.setX1(selectedShape.getX1() + xOffset);
                    selectedShape.setY1(selectedShape.getY1() + yOffset);
                    selectedShape.setX2(selectedShape.getX1() + selectedShape.getWidth());
                    selectedShape.setY2(selectedShape.getY1() + selectedShape.getHeight());
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                int relX, relY;
                relX = (int) event.getX();
                relY = (int) event.getY();
                if(selectedShape != null) {
                    if (!shapes.isEmpty()) {
                        for (Shape sh : shapes) {
                            if (sh.contains(relX, relY)) {
                                System.out.println("about to exec drop script");
                                System.out.println(sh.getX1());
                                System.out.println(sh.getX2());
                                sh.execOnDropScript(getContext(), (ViewGroup) this.getParent(), this, selectedShape.getName(),sh.getX1(), sh.getX2(), sh.getY1(), sh.getY2() );
                            }
                        }
                    }
                }
                selectedShape = null; //nullify selected shape when the user lift his finger
                invalidate();
                break;

        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setBackgroundColor(Color.WHITE);

        if(dragging) {
            flicker(canvas, selectedShape);
        }

        if(!shapes.isEmpty()){
            System.out.println("drawing shapes");
            for (Shape sh : shapes) {
                sh.drawSelf(canvas, this.getContext());
                System.out.println("drawing shape " + sh);

            }
        }
    }

    //overridden to count for onEnter Scripts if it exists for any of the page's shapes
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        System.out.println("VIS CHANGED  " + this.getPageName() + "  "+ this.visibility+"  "+View.VISIBLE);
        if(this.visibility == true){
            System.out.println("ITES VIEWABLE");
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

    public void setVis(boolean vis) {
        this.visibility = vis;
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
                System.out.println("#1 ACTION_DRAG_DROP In page");
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
                System.out.println("#1 ACTION_DRAG_ENDED In page");
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
        System.out.println("Called FLICKER");
        for (Shape shape2 : shapes) {
            System.out.println("Called FLICKER");
            if(shape2.getOnDropShapes().contains(sh.getName())) {
                System.out.println("FLICKER this one is dragging" + sh.getName() + " this one is flickering " + shape2.getName());
 /*               int rectX1 = sh.getX1();
                int rectY1 = sh.getY1();
                int rectX2 = sh.getX2();
                int rectY2 = sh.getY2();
                System.out.println("trying to draw a rectangle FLICKER");
                Paint boundaryPaint =  new Paint();
                boundaryPaint.setStyle(Paint.Style.STROKE);
                boundaryPaint.setStrokeWidth(5.0f);
                boundaryPaint.setColor(Color.rgb(0,255,0));
                canvas.drawRect(rectX1-10, rectY1+10, rectX2+10, rectY2-10, boundaryPaint);
                */
            }
        }

    }



    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }



}