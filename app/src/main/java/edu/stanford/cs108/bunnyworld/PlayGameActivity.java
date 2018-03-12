package edu.stanford.cs108.bunnyworld;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static java.lang.Boolean.TRUE;

public class PlayGameActivity extends AppCompatActivity {
    SQLiteDatabase db;
    private Document doc;
    private LinearLayout mLayout;
    private LinearLayout pLayout;
    private Possessions possessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        mLayout = (LinearLayout)findViewById(R.id.play_game);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.setWeightSum(1.0f);
        mLayout.setVerticalGravity(Gravity.BOTTOM);

        pLayout = (LinearLayout)findViewById(R.id.possessiions);
        possessions = new Possessions(this.getApplicationContext());
        pLayout.addView(possessions);
        System.out.println("mLayout childCount at start: " + mLayout.getChildCount());
        db = openOrCreateDatabase("GamesDB",MODE_PRIVATE,null);
        loadGames();
        setupGame(1);
    }

    /**
     * Load the games someone can play
     */
    public void loadGames() {
        String loadGames = "SELECT name from games;";

        System.err.println(loadGames);
        Cursor cursor = db.rawQuery(loadGames,null);

        //this is just to check what is in the DB currently
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println(cursor.getString(0));
            cursor.moveToNext();
        }

    }

    private void setupShapeScript(Shape shape, int id) {
        String loadScripts = "SELECT * from scripts where shape_id = "+id+";";

        System.err.println(loadScripts);
        Cursor cursor = db.rawQuery(loadScripts,null);

        //iterate and add the checkmarks to the shape to the document
        cursor.moveToFirst();
        int trigger, action;
        String clickScript = "", enterScript = "", dropScript = "";
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println(cursor.getInt(0));
            trigger = cursor.getInt(0);
            action = cursor.getInt(1);
            System.out.println(trigger == 1);

            if (trigger == 1){
                System.out.println("CLICK");
                if (action == 1){
                    clickScript += "hide " + cursor.getString(2) + " ";
                } else if (action == 2){
                    clickScript += "show " + cursor.getString(2) + " ";
                } else if (action == 3){
                    clickScript += "play " + cursor.getString(3) + " ";
                } else if (action == 4){
                    clickScript += "goto " + cursor.getString(4) + " ";
                }
                System.out.println(clickScript);
                //shape.setOnClickScript("hide shape2");
                shape.setOnClick(TRUE);
            } else if (trigger == 2){
                System.out.println("ENTER");
                if (action == 1){
                    enterScript += "hide " + cursor.getString(2) + " ";
                } else if (action == 2){
                    enterScript += "show " + cursor.getString(2) + " ";
                } else if (action == 3){
                    enterScript += "play " + cursor.getString(3) + " ";
                } else if (action == 4){
                    enterScript += "goto " + cursor.getString(4) + " ";
                }
                //shape.setOnEnterScript("");
                shape.setOnEnter(TRUE);
            } else if (trigger == 3){
                System.out.println("DROP");
                if (action == 1){
                    dropScript += "hide " + cursor.getString(2) + " ";
                } else if (action == 2){
                    dropScript += "show " + cursor.getString(2) + " ";
                } else if (action == 3){
                    dropScript += "play " + cursor.getString(3) + " ";
                } else if (action == 4){
                    dropScript += "goto " + cursor.getString(4) + " ";
                }
                //shape.setOnDropScript("");
                shape.setOnDrop(TRUE);
            }
            cursor.moveToNext();
        }

        shape.setOnClickScript(clickScript);
        shape.setOnEnterScript(enterScript);
        shape.setOnDropScript(dropScript);
    }

    public void setupGame(int id) {
        //create the doc

        String loadGame = "SELECT * from games where _id = "+id+";";
        System.err.println(loadGame);
        Cursor cursorG = db.rawQuery(loadGame,null);

        cursorG.moveToFirst();
        for(int i = 0; i < cursorG.getCount(); i++){
            doc = new Document(this.getApplicationContext(), cursorG.getString(0), "", "");
 //           doc.addView(possessions);
        }


        //setup Pages
        // Page p1 = new Page(this.getApplicationContext());
        //Page p2 = new Page(this.getApplicationContext());
        //Page p3 = new Page(this.getApplicationContext());
        //doc.addPage(p1);
        //doc.addPage(p2);
        //doc.addPage(p3);
        String loadPages = "SELECT * from pages where game_id = "+id+";";

        System.err.println(loadPages);
        Cursor cursor = db.rawQuery(loadPages,null);

        //iterate and add the pages to the document
        Page[] pages = new Page[cursor.getCount()];
        boolean vis;
        cursor.moveToFirst();
 //       for(int i = 0; i < cursor.getCount(); i++){
        for(int i = 0; i < 1; i++){
            System.out.println("Fetching pages results");
            System.out.println(cursor.getString(0));
            if( cursor.getInt(2) == 0) {
                vis = false;
            } else {
                vis = true;
            }
            Page p = new Page(this.getApplicationContext(), vis); //might want to add more information about a page to a page setting itself up
            pages[i] = p;
            int pageId = cursor.getInt(3);
            p.setPageName(cursor.getString(0));

            //add the shapes
            String loadShapes = "SELECT * from shapes where page_id = "+pageId+";";

            System.err.println(loadShapes);
            Cursor cursorS = db.rawQuery(loadShapes,null);
            Shape[] shapes = new Shape[cursorS.getCount()];
            cursorS.moveToFirst();
            boolean movable, visible;
            for(int j = 0; j < cursorS.getCount(); j++){
                System.out.println(cursorS.getInt(1));//x
                System.out.println(cursorS.getInt(2));//y
                System.out.println(cursorS.getInt(3));//w
                System.out.println(cursorS.getInt(4));//h
                System.out.println(cursorS.getInt(5));//movable
                System.out.println(cursorS.getInt(6));//visible
                System.out.println(cursorS.getString(7));//imagename
                System.out.println("shape ID "+cursorS.getInt(11));//imagename
                System.out.println("finished listing shape stuff");
                if(cursorS.getInt(5) == 0) {
                    movable = false;
                } else {
                    movable = true;
                }
                if(cursorS.getInt(6) == 0) {
                    visible = false;
                } else {
                    visible = true;
                }
                Shape s = new Shape(cursorS.getInt(1), cursorS.getInt(2), cursorS.getInt(3)
                        ,cursorS.getInt(4), movable, visible, cursorS.getString(7),this.getApplicationContext());
                setupShapeScript(s, cursorS.getInt(11));
                shapes[i] = s;

                //save shape
                Shape shape = new Shape();
                //createShape(shapeStringValues, checkBoxValues, shape);
                //mLayout.removeView(newPage);
                //newPage.addShape(shape);
                //mLayout.addView(newPage);
                //addShapeDialogFragment.dismiss();
 //               mLayout.removeView(p);
                p.addShape(s);

  //              mLayout.addView(p);
                cursorS.moveToNext();
            }

//            mLayout.addView(p);

            //save page
            //pageCounter += 1;
            //mLayout.removeView(newPage);
            //if(pageCounter!=1)newPage.setVisibility(View.GONE);
            //newGame.addView(newPage);
            //newPage = null;
            //if(pageCounter == 3){
            //    mLayout.addView(newGame);
            //}
//            mLayout.removeView(p);
            p.setLayoutParams(doc.getLpPages());
            doc.addView(p);
            if (p.getParent() != null) {
                // ((ViewGroup)p.getParent()).removeView(p);
            }
            //mLayout.addView(p);
            if (doc.getParent() != null) {
                ((ViewGroup)doc.getParent()).removeView(doc);
            }
//            mLayout.addView(doc);
            cursor.moveToNext();
        }
      //  possessions.setLayoutParams(doc.getLpPossessions());
        mLayout.addView(doc);
        System.out.println("mLayout childCount after adding doc: " + mLayout.getChildCount());

  //      mLayout.addView(possessions);
        System.out.println("mLayout childCount after adding poss: " + mLayout.getChildCount());

    }


}
