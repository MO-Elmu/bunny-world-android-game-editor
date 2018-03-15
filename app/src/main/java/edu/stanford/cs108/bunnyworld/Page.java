package edu.stanford.cs108.bunnyworld;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;
import android.view.Gravity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class Page extends View /*implements View.OnClickListener*/ {
    private List<Shape> shapes = new ArrayList<>();
    private int shapeCounter = 0;
    private Shape selectedShape;
    private boolean visibility;
    private boolean playMode = false;
    private boolean starterPage = false;
    private boolean isDragging = false;

    private String pageName = "";

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
        LinearLayout.LayoutParams lpPages = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0, 4.0f);
        this.setLayoutParams(lpPages);
        mDetector = new GestureDetectorCompat(context, new TapGestureListener());
    }

    public Page(Context context, AttributeSet attrs) {
        this(context);
        init(attrs, 0);
    }

    public Page(Context context, boolean visibility) {
        this(context);
        this.visibility = visibility;
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
                selectedShape = null;  //nullify the previously selected shape
                if(!shapes.isEmpty()){
                    for (Shape sh : shapes) {
                        if (sh.contains(x,y)) {
                            if(sh.isVisible())selectedShape = sh;  //select last added shape to the page

                	}                    
                    }
                    //if the clicked shape has an on click action scripts execute it
                    if(selectedShape != null) {
                        selectedShape.execOnClickScript(getContext(),(ViewGroup)this.getParent(),this);
                        if (selectedShape.imageIdentifier != 0) {
                            DragShadowBuilder shapeShadowBuilder = ImageDragShadowBuilder.fromResource(getContext(), selectedShape.imageIdentifier);
                            ClipData.Item item1_shapeName = new ClipData.Item(selectedShape.getName());
                            ClipData.Item item2_imageId = new ClipData.Item(selectedShape.imageName);
                            String mimeTypes[] = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                            ClipData draggedShape = new ClipData(selectedShape.getName(), mimeTypes, item1_shapeName);
                            draggedShape.addItem(item2_imageId);
                            this.startDrag(draggedShape, shapeShadowBuilder, null, 0);
                            selectedShape.setVisible(false);
                            invalidate();
                        }

                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                selectedShape = null; //nullify selected shape when the user lift his finger
                invalidate();
                break;

        }
        mDetector.onTouchEvent(event);

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
            }
        }
        if(isDragging) flicker(canvas, selectedShape);
    }

    //overridden to count for onEnter Scripts if it exists for any of the page's shapes
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(this.visibility == true){
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
                this.isDragging = true;
                System.out.println("ACTION_DRAG_STARTED In page");
                //Check for onDrag events on the page
//                invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                this.isDragging = true;
                System.out.println("ACTION_DRAG_ENTERED In page");
                if(selectedShape != null) selectedShape.setInPossession(false);
     //           invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                isDragging = true;
                // Ignore the event
     //           invalidate();
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                isDragging = true;
                System.out.println("ACTION_DRAG_EXITED In page");
                if(selectedShape != null) selectedShape.setInPossession(true);
    //            invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                isDragging = false;
                System.out.println("ACTION_DROP In page");
                int currX, currY;
                currX = (int) event.getX();
                currY = (int) event.getY();
                if(selectedShape == null) {
                    System.out.println("ACTION_DROP In page, null");
                    if (!playMode) {
                        System.out.println("ACTION_DROP In page, null !playmode");
                        selectedShape = new Shape(event.getClipData().getItemAt(1).getText().toString(), this.getContext());
                        selectedShape.setName(event.getClipData().getItemAt(0).getText().toString());
                        this.addShape(selectedShape);
                        selectedShape.setX1(currX - (selectedShape.getWidth() / 2));
                        selectedShape.setY1(currY - (selectedShape.getHeight() / 2));
                        selectedShape.setX2(currX + (selectedShape.getWidth() / 2));
                        selectedShape.setY2(currY + (selectedShape.getHeight() / 2));
                        selectedShape.setVisible(true);
                        invalidate();
                        selectedShape = null;
                        return true;
                    }
                    if (playMode) {
                        System.out.println("ACTION_DROP In page, null playmode");
                        //Play Mode while user is playing
                        selectedShape = new Shape(event.getClipData().getItemAt(1).getText().toString(), this.getContext());
                        selectedShape.setName(event.getClipData().getItemAt(0).getText().toString());
                        if (!shapes.isEmpty()) {
                            for (Shape sh : shapes) {
                                if (sh.contains(currX, currY)) {
                                    if (sh.wouldRespondToDrop(selectedShape.getName())) {
                                        this.addShape(selectedShape);
                                        selectedShape.setX1(currX - (selectedShape.getWidth() / 2));
                                        selectedShape.setY1(currY - (selectedShape.getHeight() / 2));
                                        selectedShape.setX2(currX + (selectedShape.getWidth() / 2));
                                        selectedShape.setY2(currY + (selectedShape.getHeight() / 2));
                                        selectedShape.setVisible(true);
                                        invalidate();
                                        sh.execOnDropScript(getContext(), (ViewGroup) this.getParent(), this, selectedShape.getName(),selectedShape.getX1(), selectedShape.getX2(),selectedShape.getY1(),selectedShape.getY2());
                                        selectedShape = null;
                                        return true;
                                    }
                                }

                            }
                        }
                        selectedShape = null;
                        invalidate();
                        System.out.println("ACTION_DROP Returning false");
                        return false;
                    }

                }

                if(selectedShape != null) {
                    System.out.println("ACTION_DROP In page, !null");
                    if (!playMode) {
                        System.out.println("ACTION_DROP In page, !null !playmode");
                        selectedShape.setX1(currX - (selectedShape.getWidth() / 2));
                        selectedShape.setY1(currY - (selectedShape.getHeight() / 2));
                        selectedShape.setX2(currX + (selectedShape.getWidth() / 2));
                        selectedShape.setY2(currY + (selectedShape.getHeight() / 2));
                        selectedShape.setVisible(true);
                        invalidate();
                        selectedShape = null;
                        return true;
                    }
                    if (playMode) {
                        System.out.println("ACTION_DROP In page, !null playmode");
                        if (!shapes.isEmpty()) {
                            for (Shape sh : shapes) {
                                if (sh.contains(currX, currY)) {
                                    if (sh.wouldRespondToDrop(selectedShape.getName())) {
                                        selectedShape.setX1(currX - (selectedShape.getWidth() / 2));
                                        selectedShape.setY1(currY - (selectedShape.getHeight() / 2));
                                        selectedShape.setX2(currX + (selectedShape.getWidth() / 2));
                                        selectedShape.setY2(currY + (selectedShape.getHeight() / 2));
                                        selectedShape.setVisible(true);
                                        invalidate();
                                        sh.execOnDropScript(getContext(), (ViewGroup) this.getParent(), this, selectedShape.getName(),selectedShape.getX1(), selectedShape.getX2(),selectedShape.getY1(),selectedShape.getY2());
                                        selectedShape = null;
                                        return true;
                                    }
                                }

                            }
                        }
                        selectedShape.setVisible(true);
                        invalidate();
                        selectedShape = null;
                        System.out.println("ACTION_DROP Returning false");
                        return false;
                    }
                }
                return false;

            case DragEvent.ACTION_DRAG_ENDED:
                isDragging = false;
                System.out.println("ACTION_DRAG_ENDED In page");
                if(event.getResult()){
                    System.out.println("Drop Ended In Page");
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                System.out.println("Drop not Ended In Page");
                invalidate();
                selectedShape = null;
                return true;

            // An unknown action type was received.
            default:
                break;
        }
        return false;
    }
    

    void flicker(Canvas canvas, Shape sh) {
        if(sh == null) return;
        System.out.println("FLICKER called while dragging shape: " + sh.getName());

        for (Shape shape2 : shapes) {

            if(shape2.isVisible()) {
                List<String> getOnDropNames = shape2.getOnDropShapes();
                System.out.println("FLICKER "  + shape2.getName() + " is visible");

                for(String shape3 : getOnDropNames) {
                    System.out.println("FLICKER "  + shape2.getName() + " has onDrop for " + shape3);
                    if(shape3.equals(sh.getName())) {
                        System.out.println("FLICKER DRAW RECTANGLE!!!");

                        int rectX1 = shape2.getX1();
                        int rectY1 = shape2.getY1();
                        int rectX2 = shape2.getX2();
                        int rectY2 = shape2.getY2();

                        Paint boundaryPaint =  new Paint();
                        boundaryPaint.setStyle(Paint.Style.STROKE);
                        boundaryPaint.setStrokeWidth(10.0f);
                        boundaryPaint.setColor(Color.rgb(0,255,0));
                        canvas.drawRect(rectX1-10, rectY1+10, rectX2+10, rectY2-10, boundaryPaint);
                    }
                }

                shape2.onDropShapes.clear();
            }
        }
    }



    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public boolean getPageVisibility() {
        return visibility;
    }

    public void setPageVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getFirstPageFlag() {
        return (starterPage) ? 1 : 0 ;
    }

    public void setPFirstPageFlag(int firstPageFlag) {
        this.starterPage = (firstPageFlag == 0) ? false : true;
    }
    /***** Setters and Getters *******/

    public List<Shape> getShapes() {
        return shapes;
    }

    public boolean isPlayMode() {
        return playMode;
    }

    public void setPlayMode(boolean playMode) {
        this.playMode = playMode;
    }

    public boolean isStarterPage() {
        return starterPage;
    }

    public void setStarterPage(boolean starterPage) {
        this.starterPage = starterPage;
    }

}