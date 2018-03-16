package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static java.lang.Boolean.TRUE;

/**
 * Created by maikefilmer on 3/14/18.
 */

public class LoadGame {

    SQLiteDatabase db;
    private Document doc;
    //private LinearLayout mLayout;
    private Possessions possessions;
    /**
     * Load the games someone can play
     */
    public void loadGames() {
        String loadGames = "SELECT game_name from games;";

        System.err.println(loadGames);
        Cursor cursor = db.rawQuery(loadGames,null);

        //this is just to check what is in the DB currently
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println(cursor.getString(0));
            cursor.moveToNext();
        }

    }

    public Document getDoc(){
        return doc;
    }

    public Possessions getPossessions(){
        return possessions;
    }

   /* public LinearLayout getmLayout() {
        return mLayout;
    }*/

    private void setupShapeScript(Shape shape, String name, String gameName) {
        String loadScripts = "SELECT * from scripts where shape_name = '"+name+"' and game_name='"+gameName+"';";
        String onDropShape = "";

        System.err.println(loadScripts);
        Cursor cursor = db.rawQuery(loadScripts,null);

        //iterate and add the checkmarks to the shape to the document
        cursor.moveToFirst();
        String trigger, action;
        int dropCount = 0;
        String clickScript = "", enterScript = "", dropScript = "";
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println(cursor.getInt(0));
            trigger = cursor.getString(2);
            action = cursor.getString(4);
            //System.out.println(trigger == 1);

            if (trigger.equals("CLICK")){
                System.out.println("CLICK");
                if (action.equals("HIDE")){
                    clickScript += "hide " + cursor.getString(5) + " ";
                } else if (action.equals("SHOW")){
                    clickScript += "show " + cursor.getString(5) + " ";
                } else if (action.equals("PLAY")){
                    clickScript += "play " + cursor.getString(6) + " ";
                } else if (action.equals("GOTO")){
                    clickScript += "goto " + cursor.getString(7) + " ";
                }
                System.out.println(clickScript);
                shape.setOnClick(TRUE);
            } else if (trigger.equals("ENTER")){
                System.out.println("ENTER");
                if (action.equals("HIDE")){
                    enterScript += "hide " + cursor.getString(5) + " ";
                } else if (action.equals("SHOW")){
                    enterScript += "show " + cursor.getString(5) + " ";
                } else if (action.equals("PLAY")){
                    enterScript += "play " + cursor.getString(6) + " ";
                } else if (action.equals("GOTO")){
                    enterScript += "goto " + cursor.getString(7) + " ";
                }
                System.out.println(enterScript);
                shape.setOnEnter(TRUE);
            } else if (trigger.equals("DROP")){
                System.out.println("DROP");
                if( dropCount == 0) {
                    dropScript += cursor.getString(3) + " ";
                    System.out.println(cursor.getString(3)+ "#5 the on drop trigger");
                    onDropShape = cursor.getString(3);
                }

                if(cursor.getString(3).equals(onDropShape)){
                    System.out.println("#5 on drop equals the cursor position " + onDropShape + ":"+ cursor.getString(3));
                } else {
                    System.out.println("#5 on drop dOES NOT equals the cursor position " + onDropShape +":"+ cursor.getString(3));
                    dropScript += "; " + cursor.getString(3)+ " ";
                    onDropShape = cursor.getString(3);
                    System.out.println("#5 drop script  "+dropScript);

                }
                if (action.equals("HIDE")){
                    dropScript += "hide " + cursor.getString(5) + " ";
                } else if (action.equals("SHOW")){
                    dropScript += "show " + cursor.getString(5) + " ";
                } else if (action.equals("PLAY")){
                    dropScript += "play " + cursor.getString(6) + " ";
                } else if (action.equals("GOTO")){
                    dropScript += "goto " + cursor.getString(7) + " ";
                }
                System.out.println(dropScript);
                shape.setOnDrop(TRUE);
                dropCount++;
            }
            cursor.moveToNext();
        }

        System.out.println("#5 on drop script "+ dropScript);

        shape.setOnClickScript(clickScript);
        shape.setOnEnterScript(enterScript);
        shape.setOnDropScript(dropScript);
    }

    public void setupGame(String name, Context context, SQLiteDatabase dbin) {
        //create the doc
        db = dbin;
        possessions = new Possessions(context);
        //mLayout = ml;
        String loadGame = "SELECT * from games where game_name = '"+name+"';";
        System.err.println(loadGame);
        Cursor cursorG = db.rawQuery(loadGame,null);

        cursorG.moveToFirst();
        for(int i = 0; i < cursorG.getCount(); i++){
            doc = new Document(context, cursorG.getString(0), 0, "");

        }



        //setup Pages
        String loadPages = "SELECT * from pages where game_name = '"+name+"';";

        System.err.println(loadPages);
        Cursor cursor = db.rawQuery(loadPages,null);

        //iterate and add the pages to the document
        Page[] pages = new Page[cursor.getCount()];
        boolean vis;
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println("Fetching pages results");
            System.out.println(cursor.getString(0));
            if( cursor.getInt(2) == 0) {
                vis = false;
            } else {
                vis = true;
            }
            Page p = new Page(context, vis); //might want to add more information about a page to a page setting itself up
            p.setLayoutParams(doc.getLpPages());

            pages[i] = p;
            String pageName = cursor.getString(0);
            p.setPageName(cursor.getString(0));
            p.setPlayMode(true);

            if (cursor.getInt(2) == 0) {
                p.setVisibility(View.GONE);

            } else {
                p.setVisibility(View.VISIBLE);
                p.setStarterPage(true);

            }


            //add the shapes
            String loadShapes = "SELECT * from shapes where page_name = '"+pageName+"' and game_name = '"+name+"';";

            System.err.println(loadShapes);
            Cursor cursorS = db.rawQuery(loadShapes,null);
            Shape[] shapes = new Shape[cursorS.getCount()];
            cursorS.moveToFirst();
            boolean movable, visible;
            for(int j = 0; j < cursorS.getCount(); j++){
                System.out.println(cursorS.getInt(9));//x
                System.out.println(cursorS.getInt(10));//y
                System.out.println(cursorS.getInt(11));//w
                System.out.println(cursorS.getInt(12));//h
                System.out.println(cursorS.getInt(8));//movable
                System.out.println(cursorS.getInt(7));//visible
                System.out.println(cursorS.getString(4));//imagename
                System.out.println("shape ID "+cursorS.getInt(4));//imagename
                System.out.println("finished listing shape stuff");
                if(cursorS.getInt(8) == 0) {
                    movable = false;
                } else {
                    movable = true;
                }
                if(cursorS.getInt(7) == 0) {
                    visible = false;
                } else {
                    visible = true;
                }
                Shape s = new Shape(cursorS.getInt(9), cursorS.getInt(10), cursorS.getInt(11)
                        ,cursorS.getInt(12), movable, visible, cursorS.getString(4), context);
                if (!cursorS.getString(3).equals("")){
                    s.setText(cursorS.getString(3));
                    float scaledFontSize = Integer.valueOf(12) * context.getResources().getDisplayMetrics().scaledDensity;
                    s.setTxtFontSize((int)scaledFontSize);
                }
                s.setName(cursorS.getString(0));
                setupShapeScript(s, cursorS.getString(0), name);
                shapes[j] = s;

                //save shape
                Shape shape = new Shape();
                //mLayout.removeView(p);
                p.addShape(s);
                System.out.println("shape added: " + s);
                System.out.println(p.getShapes());
//                mLayout.addView(p);
                cursorS.moveToNext();
            }

            //load shapes into possessions
            String loadPossShapes = "SELECT * from shapes where page_name = 'POSS' and game_name = '"+name+"';";
            Cursor cursorP = db.rawQuery(loadPossShapes,null);
            cursorP.moveToFirst();
            for(int j = 0; j < cursorP.getCount(); j++){
                if(cursorP.getInt(8) == 0) {
                    movable = false;
                } else {
                    movable = true;
                }
                if(cursorP.getInt(7) == 0) {
                    visible = false;
                } else {
                    visible = true;
                }
                Shape s = new Shape(cursorP.getInt(9), cursorP.getInt(10), cursorP.getInt(11)
                        ,cursorP.getInt(12), movable, visible, cursorP.getString(4),context);
                if (!cursorP.getString(3).equals("")){
                    s.setText(cursorP.getString(3));
                    float scaledFontSize = Integer.valueOf(cursorP.getInt(13)) * context.getResources().getDisplayMetrics().scaledDensity;
                    s.setTxtFontSize((int)scaledFontSize);
                }
                s.setName(cursorP.getString(0));
                s.setPossessable(cursorP.getInt(6));
                setupShapeScript(s, cursorP.getString(0), name);

                if(!cursorP.getString(1).equals("POSS")) {
                    System.out.println("shape does not start in possessions");
                } else {
                    System.out.println("shape starts in possessions");
                    s.setInPossession(true);
                    possessions.addShape(s);
                    s.setVisible(true);
                }
//              mLayout.addView(p);
                cursorP.moveToNext();
            }
            //mLayout.removeView(p);
            p.setLayoutParams(doc.getLpPages());
            doc.addView(p, 0);
            if (doc.getParent() != null) {
                ((ViewGroup)doc.getParent()).removeView(doc);
            }
            //mLayout.addView(doc);
            cursor.moveToNext();
        }
        possessions.setLayoutParams(doc.getLpPossessions());
        doc.addView(possessions);

    }
}
