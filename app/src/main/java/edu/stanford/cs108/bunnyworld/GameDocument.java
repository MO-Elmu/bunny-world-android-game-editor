package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class for testing Container that extends View instead of a ViewGroup
 * TODO: document your custom view class.
 */
public class GameDocument extends View {

    private List<Page> pages = new ArrayList<>();
    private int pageCounter = 0;
    private String gameName;
    //private View possessions;
    //private LinearLayout.LayoutParams lpPossessions;
    //private int possessionsTop, possessionsLeft, possessionsRight, possessionsBottom;
    private int pageTop, pageLeft, pageRight, pageBottom;

    public GameDocument(Context context) {
        super(context);
        //lpPossessions = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        //lpPossessions.gravity = Gravity.BOTTOM;
        //possessions = new View(context);
        //possessions.setLayoutParams(lpPossessions);
        //possessions.setVisibility(VISIBLE);
        //final LinearLayout gameLayout = (LinearLayout)findViewById(R.id.gameDocLayout);
        //gameLayout.addView(possessions);
        init(null, 0);
    }

    public GameDocument(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameDocument(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!pages.isEmpty()){
            for (Page pg : pages) {
                if(pg.getVisibility() != GONE){
                    pg.draw(canvas);
                }
            }
        }
        //if(possessions != null) possessions.draw(canvas);

    }

    public void addPage(Page page){
        pageCounter += 1;
        if(page.getPageName().trim().isEmpty()) page.setPageName("page" + pageCounter);
        if(pageCounter == 1) page.setVisibility(VISIBLE);
        else page.setVisibility(GONE);
        pages.add(page);
        invalidate();
    }

}