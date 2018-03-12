package edu.stanford.cs108.bunnyworld;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;

/**
 * Created by emohelw on 2/24/2018.
 */

public class Document extends LinearLayout {

    private String gameName = "";
    private String iconName = "";
    private String gameType = "";

 //   public Possessions possessions;
    private LinearLayout.LayoutParams lpPossessions;
    private LinearLayout.LayoutParams lpPages;

    public Document(Context context) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setWeightSum(1.0f);
 //       this.setGravity(Gravity.BOTTOM);
        //this.setBaselineAligned(false);
        lpPages = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0, 0.8f);
        lpPossessions = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.2f);
        this.setLayoutTransition(new LayoutTransition());  //regular animation enforced by the layout.
 //       possessions = new Possessions(context);

  //      possessions.setLayoutParams(lpPossessions);
        //possessions.setVisibility(VISIBLE);
 //       this.addView(possessions);
    }
    public Document(Context context, String name) {
        this(context);
        this.gameName = name;

    }
    public Document(Context context, String name,String iconName,String gameType) {
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


    //retrieve page and possessions layout
    public LinearLayout.LayoutParams getLpPages() {
        return lpPages;
    }
    public LinearLayout.LayoutParams getLpPossessions() {
        return lpPossessions;
    }


}
