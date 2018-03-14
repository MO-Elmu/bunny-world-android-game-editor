package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
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
public class Page extends View implements AddShapeDialogFragment.addShapeDialogFragmentListener{
    protected List<Shape> shapes = new ArrayList<>();
    private int shapeCounter = 0;
    private Shape selectedShape;
    private boolean visibility;
    private boolean playMode = false;
    private boolean starterPage = false;
    private int prevX,prevY;

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

    // XT Implemented start
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Button updateBtn = ((Activity) getContext()).findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new UpdateButtonHandlr());

        Button deleteBtn = ((Activity) getContext()).findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new DeleteButtonHandlr());

        Button advancedBtn = ((Activity) getContext()).findViewById(R.id.advanced_btn);
        advancedBtn.setOnClickListener(new AdvancedButtonHandlr());
    }

    class AdvancedButtonHandlr implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (selectedShape != null) {
                ShapeSingleton.getInstance().setSelectedShape(selectedShape);
                AddPagesActivity a = (AddPagesActivity) getContext();
                a.showAddShapeDialog();
                ShapeSingleton.getInstance().setSelectedShape(null);
            }
        }
    }

    class DeleteButtonHandlr implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (selectedShape != null) {
                shapeCounter --;
                shapes.remove(selectedShape);
                invalidate();
            }
        }

    }



    @Override
    public void onSaveShapeClick(String[] shapeStringValues, boolean[] checkBoxValues) {
        AddPagesActivity a = (AddPagesActivity)getContext();
        a.onSaveShapeClick(shapeStringValues, checkBoxValues);
    }

    @Override
    public void onCancelShapeClick(View view) {
        AddPagesActivity a = (AddPagesActivity)getContext();
        a.onCancelShapeClick(view);
    }

    class UpdateButtonHandlr implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if (selectedShape != null) {
                EditText X = ((Activity) getContext()).findViewById(R.id.X_input);
                EditText Y = ((Activity) getContext()).findViewById(R.id.Y_input);
                EditText width = ((Activity) getContext()).findViewById(R.id.Width_input);
                EditText height = ((Activity) getContext()).findViewById(R.id.Height_input);

                int xValue = Integer.parseInt( X.getText().toString() );
                int yValue = Integer.parseInt( Y.getText().toString() );
                int wValue = Integer.parseInt( width.getText().toString() );
                int hValue = Integer.parseInt( height.getText().toString() );

                selectedShape.setX1(xValue);
                selectedShape.setY1(yValue);
                selectedShape.setWidth(wValue);
                selectedShape.setHeight(hValue);

                invalidate();
            }
        }
    }
    // XT Implemented end

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
                        if (sh.contains(x,y)) {
                            if(sh.isVisible())selectedShape = sh;  //select last added shape to the page

                	    }
                    }

                    ChangeText(selectedShape);

                    //if the clicked shape has an on click action scripts execute it
                    if(selectedShape != null) selectedShape.execOnClickScript(getContext(),(ViewGroup)this.getParent(),this);
                }
                prevX = x;
                prevY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(selectedShape != null) {
                    int currX, currY, xOffset, yOffset;
                    // drag text
                    if (!selectedShape.getText().equals("")) {
                        currX = (int) event.getX();
                        currY = (int) event.getY();
                        xOffset = currX - prevX;
                        yOffset = currY - prevY;
                        prevX = currX;
                        prevY = currY;
                        selectedShape.setX1(selectedShape.getX1() + xOffset);
                        selectedShape.setY1(selectedShape.getY1() + yOffset);

                        selectedShape.setX2(selectedShape.getX1()  + selectedShape.getWidth());
                        selectedShape.setY2(selectedShape.getY1()  + selectedShape.getHeight());

                        ChangeText(selectedShape);
                        invalidate();

                    } else {

                        if (selectedShape.imageIdentifier != 0) {
                            DragShadowBuilder shapeShadowBuilder = ImageDragShadowBuilder.fromResource(getContext(), selectedShape.imageIdentifier);
                            ClipData.Item item1_shapeName = new ClipData.Item(selectedShape.getName());
                            ClipData.Item item2_imageId = new ClipData.Item(selectedShape.imageName);
                            String mimeTypes[] = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                            ClipData draggedShape = new ClipData(selectedShape.getName(), mimeTypes, item1_shapeName);
                            this.startDrag(draggedShape, shapeShadowBuilder, null, 0);
                            selectedShape.setVisible(false);
                            selectedShape.setX2(selectedShape.getX1() + selectedShape.getWidth());
                            selectedShape.setY2(selectedShape.getY1() + selectedShape.getHeight());
                            ChangeText(selectedShape);
                            invalidate();
                        }
                    }
                }

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
                //Check for onDrag events on the page
                    return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                System.out.println("ACTION_DRAG_ENTERED In page");
                if(selectedShape != null) selectedShape.setInPossession(false);
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                System.out.println("ACTION_DRAG_EXITED In page");
                if(selectedShape != null) selectedShape.setInPossession(true);
                return true;

            case DragEvent.ACTION_DROP:
                System.out.println("ACTION_DRAG_DROP In page");
                int currX, currY;
                currX = (int) event.getX();
                currY = (int) event.getY();
                if(selectedShape == null) {
                    if (!playMode) {
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
                        return false;
                    }

                }

                if(selectedShape != null) {
                    if (!playMode) {
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
                        return false;
                    }
                }
                return false;

            case DragEvent.ACTION_DRAG_ENDED:
                System.out.println("ACTION_DRAG_ENDED In page");
                if(event.getResult()){
                    System.out.println("Drop is True In Page");
                    invalidate();
                    selectedShape = null;
                    return true;
                }
                System.out.println("Drop is false In Page");
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
        System.out.println("Called FLICKER method");

        for (Shape shape2 : shapes) {
//            System.out.println("In FLICKER For loop, current shape: " + shape2.getName());
            if(shape2.isVisible()) {
                System.out.println("In FLICKER For loop, current shape is visible: " + shape2.getName());
                System.out.println("FLICKER this shape's getOnDropShapes: ");
                List<String> getOnDropNames = shape2.getOnDropShapes();
                for(String shape3 : getOnDropNames) {
                    System.out.println("FLICKER    " + shape3);
                }
            }

  /*            if(shape2.getOnDropShapes().contains(sh.getName())) {
                System.out.println("FLICKER this one is dragging" + sh.getName() + " this one is flickering " + shape2.getName());
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
                */

        }

    }

    // change the x, y, width and height for shape inspector based on input shape
    private void ChangeText (Shape shape) {


        EditText X = ((Activity) getContext()).findViewById(R.id.X_input);
        EditText Y = ((Activity) getContext()).findViewById(R.id.Y_input);
        EditText width = ((Activity) getContext()).findViewById(R.id.Width_input);
        EditText height = ((Activity) getContext()).findViewById(R.id.Height_input);

        if (shape != null) {
            X.setText( String.valueOf(shape.getX1()));
            Y.setText( String.valueOf(shape.getY1()));
            width.setText( String.valueOf (shape.getWidth()) );
            height.setText( String.valueOf (shape.getHeight()) );
        } else {
            X.setText("");
            Y.setText( "");
            width.setText( "" );
            height.setText( "" );
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