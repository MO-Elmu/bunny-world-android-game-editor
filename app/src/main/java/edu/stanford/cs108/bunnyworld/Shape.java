package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by emohelw on 2/23/2018.
 */

public class Shape {

    private String name = "";       // Shape name
    private boolean movable = true;
    private boolean visible = true;
    private boolean inPossession = false;
    protected String imageName = "";
    protected int imageIdentifier;
    private String text = "";
    private String onClickScript = "";
    private String onEnterScript = "";
    private String onDropScript ="";
    private boolean onClick = false;
    private boolean onEnter = false;
    private boolean onDrop = false;
    private int x1 = 50;
    private int y1 = 50;
    private int x2 = 250;
    private int y2 = 250;   //(x1,y1) = upper left corner of a shape, (x2,y2) == bottom right corner
    private int width = 200;
    private int height = 200;
    private Rect shapeRect;
    private BitmapDrawable imageDrawable;
    private Paint defaultFillPaint;   //Grey fill paint used if image and text are missing or not set
    private Paint hiddenDefaultPaint; //used if Shape visible is set to false
    private Paint txtPaint;    //Black font for the text
    private Paint hiddenTxtPaint;
    private Paint hiddenImagePaint;
    private int txtFontSize = 50;   //initial value font must be settable according to spec
    private final int fillColor = Color.GRAY;

    //container for the onDrop shapes
    List<String> onDropShapes = new ArrayList<>();



    // initialize all needed Paints
    private void init(){   //create and set all Paints
        defaultFillPaint = new Paint();
        defaultFillPaint.setColor(fillColor);
        hiddenDefaultPaint = new Paint();
        hiddenDefaultPaint.setColor(fillColor);
        hiddenDefaultPaint.setAlpha(0);
        txtPaint = new Paint();
        txtPaint.setColor(Color.BLACK);
        txtPaint.setTextSize(txtFontSize);
        hiddenTxtPaint = new Paint();
        hiddenTxtPaint.setColor(Color.BLACK);
        hiddenTxtPaint.setTextSize(txtFontSize);
        hiddenTxtPaint.setAlpha(0);
        hiddenImagePaint = new Paint();
        hiddenImagePaint.setAlpha(0);
        shapeRect = new Rect(this.x1,this.y1,this.x2,this.y2);  //Shape rectangle

    }

    // Shape Constructors
    public Shape(){

    }
    public Shape(int x1, int y1, int width, int height){
        this.x1 = x1;
        this.y1 = y1;
        this.width = width;
        this.height = height;
        this.x2 = x1+width;
        this.y2 = y1+height;
    }

    public Shape(int x1, int y1, int width, int height, String shapeName){
        this(x1,y1,width,height);
        this.name = shapeName;
    }

    public Shape(int x1, int y1, int width, int height, boolean movable, boolean visible){
        this(x1,y1,width,height);
        this.movable = movable;
        this.visible = visible;
    }

    public Shape(int x1, int y1, int width, int height, boolean movable, boolean visible
            , String imageName, Context context){
        this(x1,y1,width,height, movable, visible);
        this.setImage(imageName, context);
    }
    //Construct a shape given Image name

    public Shape(String imageName, Context context){
        this.setImage(imageName, context);
    }

    //Construct a shape given a string txt
    public Shape(String txt){
        this.setText(txt);
    }


    public void setImage(String imageName, Context context){
        this.imageName = imageName;
        imageIdentifier = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    public void setText(String text){
        this.text = text;
    }
    //In-case the user needed to supply his own Paint for the text shape with font size (specs)
    public void setText(String text, Paint paint){
        this.text = text;
        txtPaint = paint;
    }



    public void drawSelf(Canvas canvas, Context context){
        init();
        shapeRect = new Rect(this.x1,this.y1,this.x2,this.y2);  //Shape rectangle
        if((text.trim().isEmpty() && imageName.trim().isEmpty()) || (text.trim().isEmpty() && imageIdentifier == 0)){
            if(visible) canvas.drawRect(x1,y1,x2,y2,defaultFillPaint);
            //else canvas.drawRect(x1,y1,x2,y2,hiddenDefaultPaint);
            return;
        }
        //Text takes precedence over image
        if(!text.trim().isEmpty()){
            if(visible) canvas.drawText(text,x1,y1,txtPaint);
            //else canvas.drawText(text,x1,y1,hiddenTxtPaint);
            return;
        }

        //if image is provided and no text draw the provided image
        imageDrawable = (BitmapDrawable) context.getResources().getDrawable(imageIdentifier);
        if(visible) {
            canvas.drawBitmap(imageDrawable.getBitmap(), null, shapeRect, null);
        }
        //else canvas.drawBitmap(imageDrawable.getBitmap(),null, shapeRect, defaultFillPaint);



    }

    //Check if the point x,y is within the shape boundaries to identify user touch
    public boolean contains(int x, int y){
        if(x1<=x && x<=x2 && y1<=y && y<=y2) return true;
        return false;
    }



    /*********       Handling Scripts and actions code. All code related to scripts   **********/

    //Helper method to tokenize the the scripts into array of clauses
    private List<String> tokenizeScript (String script){
        List<String> clauses = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(script);
        while(tok.hasMoreTokens()){
            clauses.add(tok.nextToken(";"));
        }
        return clauses;

    }

    /*Even though the below methods (parseScript, parseOnDropScript)  have almost the same
      functionality they are slightly different and this difference is stemmed from the fact
      that onDrop scripts is slightly different in structure than the other 2 actions.
     */

    private Map<String, String> parseScript(String script){
        List<String> clauses = tokenizeScript(script);
        Map<String, String> scriptTokens = new LinkedHashMap<>();
        //Since onClick takes the first clause and ignore the rest(Specs) and onEnter
        //usually has one clause will only handle the first clause from the left.
        StringTokenizer tokenizer = new StringTokenizer(clauses.get(0));
        while(tokenizer.hasMoreTokens()){
            scriptTokens.put(tokenizer.nextToken(), tokenizer.nextToken());
        }
        return scriptTokens;
    }


    /* The two methods below are for on drop action only, first one extract
       the first word in onDropScript which represents the shape name to be dropped
       on top of this shape.
       The second method construct the rest of the script actions and ignores the
       first word (name of the shape).
     */
    private void populateOnDropShapesArray (String onDropScript){
        List<String> clauses = tokenizeScript(onDropScript);
        for (String clause : clauses) {
            if (clause.trim().indexOf(' ') < 0) {
                onDropShapes.add(clause.trim());
            } else {
                onDropShapes.add(clause.trim().substring(0, clause.trim().indexOf(' ')));
            }
        }
    }
    private List<Map<String, String>> parseOnDropScript(String onDropScript) {
        List<Map<String, String>> allOnDropActions = new ArrayList<>();
        List<String> clauses = tokenizeScript(onDropScript);
        for (String clause : clauses) {
            Map<String, String> scriptTokens = new LinkedHashMap<>();
            StringTokenizer tokenizer = new StringTokenizer(clause);
            tokenizer.nextToken();  //Skip the shape name store only the actions and their parameters
            while (tokenizer.hasMoreTokens()) {
                scriptTokens.put(tokenizer.nextToken(), tokenizer.nextToken());
            }
            allOnDropActions.add(scriptTokens);
        }
        return allOnDropActions;
    }

    private void execScripts(Map<String, String> scriptToken, Context context, ViewGroup game, Page parentPage){
        int pageCount = game.getChildCount();
        for(Map.Entry<String, String> entry : scriptToken.entrySet()){
            String action = entry.getKey();
            switch (action.toLowerCase()){
                case "play":
                    String soundFileName = entry.getValue();
                    int soundFileId = context.getResources().getIdentifier(soundFileName, "raw",context.getPackageName());
                    MediaPlayer mp = MediaPlayer.create(context,soundFileId);
                    mp.start();
                    break;
                case "goto":
                    String pageName = entry.getValue();
                    for(int i=0; i<pageCount; i++){
                        final Page page = (Page)game.getChildAt(i);
                        if(pageName.equals(page.getPageName())){
                            //parentPage.animate().translationY(parentPage.getHeight());
                            parentPage.setVisibility(View.GONE);
                            //page.animate().translationY(parentPage.getHeight());
                            page.setVisibility(View.VISIBLE);
                            //page.clearAnimation();
                        }
                    }
                    break;
                case "hide":
                    String shapeName = entry.getValue();
                    for(int i=0; i<pageCount; i++){
                        final Page page = (Page)game.getChildAt(i);
                        for (Shape sh : page.shapes){
                            if(shapeName.equals(sh.getName())){
                                sh.visible = false;
                                page.invalidate();
                            }
                        }
                    }
                    break;
                case "show":
                    String name = entry.getValue();
                    for(int i=0; i<pageCount; i++){
                        final Page page = (Page)game.getChildAt(i);
                        for (Shape sh : page.shapes){
                            if(name.equals(sh.getName())){
                                sh.visible = true;
                                page.invalidate();
                            }
                        }
                    }
                    break;
                default:
                    //handle unknown script commands
                    break;

            }

        }

    }

    private boolean isValidScript(String script){
        return true;
    }

    public void execOnClickScript(Context context, ViewGroup game, Page parentPage){ //account for the shape being only on a Page not Possessions area
        if(!onClick || onClickScript.trim().isEmpty() || !isValidScript(onClickScript)) return;
        execScripts(parseScript(onClickScript), context, game, parentPage);
    }

    public void execOnEnterScript(Context context, ViewGroup game, Page parentPage){
        if(!onEnter || onEnterScript.trim().isEmpty() || !isValidScript(onEnterScript)) return;
        execScripts(parseScript(onEnterScript), context, game, parentPage);
    }

    public void execOnDropScript(Context context, ViewGroup game, Page parentPage, String shapeName){
        if(!onDrop || onDropScript.trim().isEmpty() || !isValidScript(onDropScript)) return;
        populateOnDropShapesArray(onDropScript);
        for(int i=0; i<onDropShapes.size(); i++){
            //System.out.println(name);
            if(onDropShapes.get(i).equals(shapeName)) {

                execScripts(parseOnDropScript(onDropScript).get(i), context, game, parentPage);
            }
        }
        onDropShapes.clear();
    }




    /*** Setters and Getters ***/

    //Shape name setters and getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTxtFontSize() {
        return txtFontSize;
    }

    public void setTxtFontSize(int txtFontSize) {
        this.txtFontSize = txtFontSize;
    }
    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }


    //Scripts setters and getters
    public String getOnClickScript() {
        return onClickScript;
    }

    public void setOnClickScript(String onClickScript) {
        this.onClickScript = onClickScript;
    }

    public String getOnEnterScript() {
        return onEnterScript;
    }

    public void setOnEnterScript(String onEnterScript) {
        this.onEnterScript = onEnterScript;
    }

    public String getOnDropScript() {
        return onDropScript;
    }

    public void setOnDropScript(String onDropScript) {
        this.onDropScript = onDropScript;
    }
    public boolean isOnClick() {
        return onClick;
    }

    public void setOnClick(boolean onClick) {
        this.onClick = onClick;
    }

    public boolean isOnEnter() {
        return onEnter;
    }

    public void setOnEnter(boolean onEnter) {
        this.onEnter = onEnter;
    }

    public boolean isOnDrop() {
        return onDrop;
    }

    public void setOnDrop(boolean onDrop) {
        this.onDrop = onDrop;
    }



    //Setters and getters for the Shape geometry


    public boolean isInPossession() {return inPossession;}

    public void setInPossession(boolean inPossession) {this.inPossession = inPossession;}

    public boolean isVisible() {return visible;}

    public void setVisible(boolean visible) {this.visible = visible;}

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        if(movable) this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        if(movable) this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        if(movable) this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        if(movable) this.y2 = y2;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


/*
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
    */
}
