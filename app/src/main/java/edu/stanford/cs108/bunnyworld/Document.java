package edu.stanford.cs108.bunnyworld;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.List;

// test test test

/**
 * Created by emohelw on 2/24/2018.
 */

public class Document extends LinearLayout {

    private String gameName = "";
    private int iconName;
    private String gameType = "";

    //public View possessions;
    private LinearLayout.LayoutParams lpPossessions;

    private LinearLayout.LayoutParams lpPages;

    public Document(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setWeightSum(5.0f);
        this.setGravity(Gravity.BOTTOM);
        lpPossessions = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 4.0f);
        lpPages = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0, 1.0f);
        this.setLayoutTransition(new LayoutTransition());  //regular animation enforced by the layout.
    }
    public Document(Context context, String name) {
        this(context);
        this.gameName = name;

    }
    public Document(Context context, String name,int iconName,String gameType) {
        this(context);
        this.gameName = name;
        this.iconName = iconName;
        //this.gameType = gameType;
    }

    public Document(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public Document(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // add getters
    public String getGameName() {
        return gameName;
    }

    public int getIconName() {
        return iconName;
    }

    public String getGameType() {
        return gameType;
    }

    //retrieve page and possessions layout
    public LinearLayout.LayoutParams getLpPages() {
        return lpPages;
    }
    public LinearLayout.LayoutParams getLpPossessions() {
        return lpPossessions;
    }

    public void getChildren() {
        System.out.println("DOCCHILDREN " + this.getChildCount());
    }

    /*
    public void flicker(Canvas canvas, Shape sh) {
        //    if(sh == null) sh =
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
        */


}