package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static java.lang.Boolean.TRUE;

public class PlayGameActivity extends AppCompatActivity {
    SQLiteDatabase db;
    private Document doc;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        mLayout = (LinearLayout)findViewById(R.id.loadGame);
        db = openOrCreateDatabase("GamesDB",MODE_PRIVATE,null);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        data.get("game");
        System.out.println("GAMEEE: "+ data.get("game"));
        loadGames();
        setupGame(data.get("game").toString());
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
        int trigger, action, dropCount = 0;
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
                System.out.println(enterScript);
                shape.setOnEnter(TRUE);
            } else if (trigger == 3){
                System.out.println("DROP");
                if( dropCount == 0) {
                    dropScript += cursor.getString(5) + " ";
                }
                if (action == 1){
                    dropScript += "hide " + cursor.getString(2) + " ";
                } else if (action == 2){
                    dropScript += "show " + cursor.getString(2) + " ";
                } else if (action == 3){
                    dropScript += "play " + cursor.getString(3) + " ";
                } else if (action == 4){
                    dropScript += "goto " + cursor.getString(4) + " ";
                }
                System.out.println(dropScript);
                shape.setOnDrop(TRUE);
                dropCount++;
            }
            cursor.moveToNext();
        }

        shape.setOnClickScript(clickScript);
        shape.setOnEnterScript(enterScript);
        shape.setOnDropScript(dropScript);
    }

    public void setupGame(String name) {
        //create the doc

        String loadGame = "SELECT * from games where name = '"+name+"';";
        System.err.println(loadGame);
        Cursor cursorG = db.rawQuery(loadGame,null);

        cursorG.moveToFirst();
        for(int i = 0; i < cursorG.getCount(); i++){
            doc = new Document(this.getApplicationContext(), cursorG.getString(0), "", "");
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
            Page p = new Page(this.getApplicationContext(), vis); //might want to add more information about a page to a page setting itself up
            pages[i] = p;
            int pageId = cursor.getInt(3);
            p.setPageName(cursor.getString(0));

            if (cursor.getInt(2) == 0) {
                p.setVisibility(View.GONE);
            } else {
                p.setVisibility(View.VISIBLE);
            }


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
                if (!cursorS.getString(8).equals("")){
                    s.setText(cursorS.getString(8));
                    float scaledFontSize = Integer.valueOf(12) * getResources().getDisplayMetrics().scaledDensity;
                    s.setTxtFontSize((int)scaledFontSize);
                }
                s.setName(cursorS.getString(0));
                setupShapeScript(s, cursorS.getInt(11));
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
            //mLayout.removeView(p);
            doc.addView(p);
            if (doc.getParent() != null) {
                ((ViewGroup)doc.getParent()).removeView(doc);
            }
            //mLayout.addView(doc);
            cursor.moveToNext();
        }
        mLayout.addView(doc);

    }


}