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
import android.graphics.Rect;
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

import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;
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

    //this "shapeCounter" param should not be called to retrieve the number of shapes in this view
    // since it only used to keep the naming scheme of shapes incremented by 1 everytime
    //the user doesn't bother to enter a shape name. Instead classes should get the accurate
    // number of shapes from the shapes List.
    private int shapeCounter = 0;
    private Shape selectedShape;
    private boolean visibility;
    private boolean playMode = false;
    private boolean starterPage = false;
    private boolean isDragging = false;
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

        //hideInspector();
        if (ShapeSingleton.getInstance().playMode == false) {
            Button updateBtn = ((Activity) getContext()).findViewById(R.id.update_btn);
            updateBtn.setOnClickListener(new UpdateButtonHandlr());

            Button deleteBtn = ((Activity) getContext()).findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(new DeleteButtonHandlr());

            Button advancedBtn = ((Activity) getContext()).findViewById(R.id.advanced_btn);
            advancedBtn.setOnClickListener(new AdvancedButtonHandlr());
        }
    }

    class AdvancedButtonHandlr implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (selectedShape != null) {
                ShapeSingleton.getInstance().setSelectedShape(selectedShape);
                ShapeSingleton.getInstance().setSelectedShapeContainer(shapes);

                AddPagesActivity a = (AddPagesActivity) getContext();
                a.showAddShapeDialog();


            }
        }
    }

    class DeleteButtonHandlr implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (selectedShape != null) {
                removeShape(selectedShape);
                ChangeText(selectedShape);
                hideInspector();
                invalidate();
            }
        }

    }

    public void removeShape (Shape shape) {
        shapeCounter --;
        shapes.remove(shape);
        selectedShape = null;
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

                selectedShape.setX1_absolute(xValue);
                selectedShape.setY1_absolute(yValue);
                selectedShape.setWidth(wValue);
                selectedShape.setHeight(hValue);

                invalidate();
            }
        }
    }
    // XT Implemented end

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
 //               this.isDragging = true;
                x = (int)event.getX();
                y = (int)event.getY();

                selectedShape = null;  //nullify the previously selected shape
                if(!shapes.isEmpty()){
                    for (Shape sh : shapes) {
                        if (sh.contains(x,y)) {
                            if(sh.isVisible())selectedShape = sh;  //select last added shape to the page


                	    }
                    }

                    if(!playMode) {
                        if (selectedShape != null) {
                            showInspector();
                            if (!selectedShape.getText().equals("")) {
                                hideWH();
                            } else {
                                showWH();
                            }
                            ChangeText(selectedShape);

                            //if the clicked shape has an on click action scripts execute it
                            selectedShape.execOnClickScript(getContext(), (ViewGroup) this.getParent(), this);
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
                        } else {
                            hideInspector();
                        }
                    } else{//if the clicked shape has an on click action scripts execute it
                        if(selectedShape != null) {
                            selectedShape.execOnClickScript(getContext(), (ViewGroup) this.getParent(), this);

                            if (selectedShape.imageIdentifier != 0 && selectedShape.getPossessable() == 1) {
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

                }
                prevX = x;
                prevY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(selectedShape != null) {
                    int currX, currY, xOffset, yOffset;
                    // drag text

                    if(!playMode) {
                        if (!selectedShape.getText().equals("")) {
                            hideWH();
                        } else {
                            showWH();
                        }
                    }

                    if (!selectedShape.getText().equals("") || (selectedShape.getText().trim().isEmpty() && selectedShape.getImageName().trim().isEmpty()) || (selectedShape.getText().trim().isEmpty() && selectedShape.getImageIdentifier() == 0)) {
                        currX = (int) event.getX();
                        currY = (int) event.getY();
                        xOffset = currX - prevX;
                        yOffset = currY - prevY;
                        prevX = currX;
                        prevY = currY;
                        selectedShape.setX1_absolute(selectedShape.getX1() + xOffset);
                        selectedShape.setY1_absolute(selectedShape.getY1() + yOffset);

                        selectedShape.setX2_absolute(selectedShape.getX1()  + selectedShape.getWidth());
                        selectedShape.setY2_absolute(selectedShape.getY1()  + selectedShape.getHeight());

                        if(!playMode) {
                            ChangeText(selectedShape);
                        }
                        invalidate();

                    } else {}
                }

            case MotionEvent.ACTION_UP:

                //selectedShape = null; //nullify selected shape when the user lift his finger
                //invalidate();

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
                }
                sh.drawSelf(canvas, this.getContext());
            }
        }
        if(isDragging && selectedShape != null) {
            pageFlicker(canvas, selectedShape);
        }
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

    //int w = selectedShape.getWidth(), h = selectedShape.getHeight();
    @Override
    public boolean onDragEvent(DragEvent event) {
        int x, y;
        final int action = event.getAction();
        // Handles all the expected events
        switch(action) {
            case DragEvent.ACTION_DRAG_STARTED:
                System.out.println("ACTION_DRAG_STARTED In page");
                if(!playMode) {
                   // hideInspector();
                }
                //Check for onDrag events on the page

                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                System.out.println("ACTION_DRAG_ENTERED In page");

                if(selectedShape != null) {
                    selectedShape.setInPossession(false);

                }
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
     //           invalidate();
                if(selectedShape != null) {
                    int xloc = (int) event.getX() - selectedShape.getWidth() / 2;
                    int yloc = (int) event.getY() - selectedShape.getHeight() / 2;

                    EditText X = ((Activity) getContext()).findViewById(R.id.X_input);
                    EditText Y = ((Activity) getContext()).findViewById(R.id.Y_input);
                    X.setText(String.valueOf(xloc));
                    Y.setText(String.valueOf(yloc));
                } else{
                    //hideInspector();
                }


                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                System.out.println("ACTION_DRAG_EXITED In page");

                if(selectedShape != null) {
                    selectedShape.setInPossession(true);
                    hideInspector();

                }
                return true;

            case DragEvent.ACTION_DROP:
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
                        selectedShape.setX1_absolute(currX - (selectedShape.getWidth() / 2));
                        selectedShape.setY1_absolute(currY - (selectedShape.getHeight() / 2));
                        selectedShape.setX2_absolute(currX + (selectedShape.getWidth() / 2));
                        selectedShape.setY2_absolute(currY + (selectedShape.getHeight() / 2));
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

                        selectedShape.setX1_absolute(currX - (selectedShape.getWidth() / 2));
                        selectedShape.setY1_absolute(currY - (selectedShape.getHeight() / 2));
                        selectedShape.setX2_absolute(currX + (selectedShape.getWidth() / 2));
                        selectedShape.setY2_absolute(currY + (selectedShape.getHeight() / 2));

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

    public void possessionsFlicker (Shape possSelectedShape) {
        selectedShape = possSelectedShape;
//        invalidate();
    }

    public void pageFlicker(Canvas canvas, Shape selectedShape) {
        ViewParent viewParent = this.getParent();
        Flicker flicker = new Flicker(selectedShape, viewParent);
        int rectX1 = flicker.getRectX1();
        int rectY1 = flicker.getRectY1();
        int rectX2 = flicker.getRectX2();
        int rectY2 = flicker.getRectY2();
        System.out.println("FLICKER rect's xy " + rectX1 + " " + rectY1
                + " " + rectX2 + " " + rectY2);
        Paint boundaryPaint = flicker.getBoundaryPaint();

        canvas.drawRect(rectX1, rectY1, rectX2, rectY2, boundaryPaint);
    }
    

 /*   void flicker(Canvas canvas, Shape sh) {
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
    } */



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

    // change the x, y, width and height for shape inspector based on input shape
    private void hideInspector () {
        LinearLayout inspector = ((Activity) getContext()).findViewById(R.id.inspector);
        inspector.setVisibility(View.GONE);

    }

    private void showInspector () {
        LinearLayout inspector = ((Activity) getContext()).findViewById(R.id.inspector);
        inspector.setVisibility(View.VISIBLE);

    }

    // Hide width and height in shape inspector
    private void hideWH () {
        EditText w_edittext = ((Activity) getContext()).findViewById(R.id.Width_input);
        w_edittext.setVisibility(View.GONE);

        TextView w_label = ((Activity) getContext()).findViewById(R.id.Width_label);
        w_label.setVisibility(View.GONE);

        EditText h_edittext = ((Activity) getContext()).findViewById(R.id.Height_input);
        h_edittext.setVisibility(View.GONE);

        TextView h_label = ((Activity) getContext()).findViewById(R.id.Height_label);
        h_label.setVisibility(View.GONE);
    }

    // Show width and height in shape inspector
    private void showWH () {
        EditText w_edittext = ((Activity) getContext()).findViewById(R.id.Width_input);
        w_edittext.setVisibility(View.VISIBLE);

        TextView w_label = ((Activity) getContext()).findViewById(R.id.Width_label);
        w_label.setVisibility(View.VISIBLE);

        EditText h_edittext = ((Activity) getContext()).findViewById(R.id.Height_input);
        h_edittext.setVisibility(View.VISIBLE);

        TextView h_label = ((Activity) getContext()).findViewById(R.id.Height_label);
        h_label.setVisibility(View.VISIBLE);
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