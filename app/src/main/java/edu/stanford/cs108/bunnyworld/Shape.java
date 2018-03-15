package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by emohelw on 2/23/2018.
 */

public class Shape{

    private String name = "";       // Shape name
    private boolean movable = true;
    private boolean visible = true;
    private boolean inPossession = false;
    private int possessable = 0;
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

    // constants
    public static final String GOTO = "GOTO";
    public static final String PLAY = "PLAY";
    public static final String HIDE = "HIDE";
    public static final String SHOW = "SHOW";

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
        if(visible) canvas.drawBitmap(imageDrawable.getBitmap(),null, shapeRect, null);
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

    private Map<String, List<String>> parseScript(String script){
        List<String> clauses = tokenizeScript(script);
        Map<String, List<String>> scriptTokens = new LinkedHashMap<>();
        //Since onClick takes the first clause and ignore the rest(Specs) and onEnter
        //usually has one clause will only handle the first clause from the left.
        StringTokenizer tokenizer = new StringTokenizer(clauses.get(0));
        String key, value;
        List<String> holder = new ArrayList<String>();
        while(tokenizer.hasMoreTokens()){
            key = tokenizer.nextToken();
            value = tokenizer.nextToken();
            holder.clear();
            holder.add(value);
            scriptTokens.put(key, holder);
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
        System.out.println(clauses);
        for (String clause : clauses) {
            if (clause.trim().indexOf(' ') < 0) {
                onDropShapes.add(clause.trim());
            } else {
                onDropShapes.add(clause.trim().substring(0, clause.trim().indexOf(' ')));
            }
            System.out.println("The shapes for ondrop "+onDropShapes);
        }
    }
    private List<Map<String, List<String>>> parseOnDropScript(String onDropScript) {
        List<Map<String, List<String>>> allOnDropActions = new ArrayList<>();
        List<String> clauses = tokenizeScript(onDropScript);
        for (String clause : clauses) {
            System.out.println("#1 clauses: "+clause);
            Map<String, List<String>> scriptTokens = new LinkedHashMap<>();
            StringTokenizer tokenizer = new StringTokenizer(clause);

            System.out.println("#1 tokensizer: "+tokenizer);
            //List<String> actionShapes = new ArrayList<String>();
            String action, actionShape;
            tokenizer.nextToken();  //Skip the shape name store only the actions and their parameters
            while (tokenizer.hasMoreTokens()) {
                List<String> actionShapes = new ArrayList<String>(); //THIS MUST BE DECLARED WITHIN THE WHILE LOOP!!!!
                action = tokenizer.nextToken();
                actionShape = tokenizer.nextToken();
                actionShapes.clear();
                actionShapes.add(actionShape);
                System.out.println("get string tokenss "+scriptTokens.get("show"));
                if(scriptTokens.containsKey(action)){
                    System.out.println("get string tokens "+scriptTokens.get("hide"));
                    for(String st : scriptTokens.get(action)) {
                        System.out.println("string: "+st);
                        actionShapes.add(st);
                    }
                }
                scriptTokens.put(action, actionShapes);
                System.out.println("#1 scriptTokens: "+ scriptTokens);
            }
            allOnDropActions.add(scriptTokens);
        }
        System.out.println("ALL actions   "+allOnDropActions);
        return allOnDropActions;
    }

    private void execScripts(Map<String, List<String>> scriptToken, Context context, ViewGroup game, Page parentPage){
        System.out.println("Executing scripts");
        int pageCount = game.getChildCount();

        System.out.println("#1 action: "+scriptToken);
        for(Map.Entry<String, List<String>> entry : scriptToken.entrySet()){

            String action = entry.getKey();
            System.out.println("#1 action: "+action);
            for(String name : entry.getValue()) {
                switch (action.toLowerCase()) {
                    case "play":
                        System.out.println("PLAYING SOUND");
                        String soundFileName = name;
                        int soundFileId = context.getResources().getIdentifier(soundFileName, "raw", context.getPackageName());
                        MediaPlayer mp = MediaPlayer.create(context, soundFileId);
                        mp.start();
                        break;
                    case "goto":
                        try {
                            String pageName = name;
                            System.out.println("GOTO SCRIPT!!! " + pageName + " c: " + pageCount);
                            for (int i = 0; i < pageCount; i++) {
                                final Page page = (Page) game.getChildAt(i);
                                System.out.println("pagename " + pageName + " pageItr: " + page.getPageName());
                                if (pageName.equals(page.getPageName())) {

                                    System.out.println("pn " + page.getPageName());
                                    //parentPage.animate().translationY(parentPage.getHeight());

                                    page.setVis(true);
                                    parentPage.setVis(false);
                                    parentPage.setVisibility(View.GONE);
                                    //page.animate().translationY(parentPage.getHeight());
                                    page.setVisibility(View.VISIBLE);
                                    System.out.println("goto PAGE shapes:  " + page.getShapes());
                                    //page.clearAnimation();
                                } else {
                                    page.setVis(false);
                                    page.setVisibility(View.GONE);
                                }
                            }
                        } catch (ClassCastException e) {

                        }
                        break;
                    case "hide":
                        try {
                            String shapeName = name;
                            for (int i = 0; i < pageCount; i++) {
                                final Page page = (Page) game.getChildAt(i);

                                System.out.println("#1 pageItr: " + page.getPageName());
                                for (Shape sh : page.getShapes()) {
                                    if (shapeName.equals(sh.getName())) {
                                        sh.visible = false;
                                        page.invalidate();
                                    }
                                }
                            }
                        } catch (ClassCastException e) {

                        }
                        break;
                    case "show":

                        System.out.println("SHOWING! " + name);
                        for (int i = 0; i < pageCount; i++) {
                            try {
                                final Page page = (Page) game.getChildAt(i);
                                System.out.println("showing pageItr on enter : " + page.getPageName());
                                for (Shape sh : page.getShapes()) {
                                    System.out.println("showing shape name : " + sh.getName());
                                    if (name.equals(sh.getName())) {
                                        System.out.println("SHOWING2 " + sh.getName());
                                        sh.visible = true;
                                        page.invalidate();
                                    }
                                }

                            } catch (ClassCastException e) {

                            }


                        }
                        break;
                    default:
                        //handle unknown script commands
                        break;

                }
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

    public void execOnDropScript(Context context, ViewGroup game, Page parentPage, String shapeName, int x1, int x2, int y1, int y2){
        System.out.println("PAGE SHAPE " +parentPage.getPageName()+shapeName);
        System.out.println("drop script " +onDropScript);
        System.out.println("EXEC drop script  "+onDrop + " "+onDropScript.trim().isEmpty()+isValidScript(onDropScript));
        if(!onDrop || onDropScript.trim().isEmpty() || !isValidScript(onDropScript)) return;
        populateOnDropShapesArray(onDropScript);
        System.out.println("about to iter drop shapes");
        for(int i=0; i<onDropShapes.size(); i++){

            System.out.println("iter drop shapes");
            System.out.println(onDropShapes.get(i));

            System.out.println(shapeName);
            System.out.println("parent page "+parentPage.getPageName());
            /**if(onDropShapes.get(i).equals(shapeName)) {

             execScripts(parseOnDropScript(onDropScript).get(i), context, game, parentPage);
             }*/
            System.out.println(parentPage.getShapes());
            for (Shape sh : parentPage.getShapes()){
                System.out.println("finding the drop shape in the page");
                System.out.println("SHAPE " + sh.getName());

                System.out.println("DROP SHAPE " + onDropShapes.get(i));
                if(onDropShapes.get(i).equals(sh.getName())){
                    System.out.println("DROP SHAPE " + sh.getName());
                    if( x1 < sh.getX2() && x2 > sh.getX1() && y1 < sh.getY2() && y2>sh.getY1()) {
                        System.out.println("OVERLAP");
                        execScripts(parseOnDropScript(onDropScript).get(i), context, game, parentPage);
                    }
                }
            }
        }
        onDropShapes.clear();
    }
    
    public boolean wouldRespondToDrop(String droppedShape){
        boolean result = false;
        populateOnDropShapesArray(onDropScript);
        for(int i=0; i<onDropShapes.size(); i++){
            if(onDropShapes.get(i).equals(droppedShape)) {
                result = true;
                break;
            }
        }
        onDropShapes.clear();
        return result;
    }
    
    
    public List<String> getOnDropShapes() {
        populateOnDropShapesArray(getOnDropScript());
        return onDropShapes;
    }
    
    /*** Setters and Getters ***/
    
    
    
    //Shape name setters and getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageIdentifier() {
        return imageIdentifier;
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


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isInPossession() {
        return inPossession;
    }

    public void setInPossession(boolean inPossession) {
        this.inPossession = inPossession;
    }

    public int getPossessable() {
        return possessable;
    }

    public void setPossessable(int possessable) {
        this.possessable = possessable;
    }

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

    public int getHeight() {
        return height;
    }

    public String getText() {
        return text;
    }

    public String getImageName() {
        return imageName;
    }


}