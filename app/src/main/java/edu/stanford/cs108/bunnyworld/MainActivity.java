package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("GamesDB",MODE_PRIVATE,null);
        resetDB();
        setupDatabase();
        populateDatabase();
        checkDB();
    }


    /** Handles the Create Game Button and moves the user
     * to a new Activity (CreateGameActivity).
     * @param view
     */
    public void createGame(View view){
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    /** Reacts to Play Game Button and takes
     * the view to a new Activity to be able
     * to play Games stored in the database.
     * @param view
     */
    public void playGame(View view){
        Intent intent = new Intent(this, PlayGameActivity.class);
        startActivity(intent);
    }

    /**
     -     * This method is used to add setup the database.
     -     */
    private void setupDatabase() {
        String setupStrGames = "CREATE TABLE games ("
                + "name TEXT,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        String setupStrPages = "CREATE TABLE pages ("
                + "name TEXT, game_id INTEGER, visibility INTEGER,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        String setupStrShapes = "CREATE TABLE shapes ("
                + "name TEXT, x REAL, y REAL, width REAL, height REAL,"
                + "movable INTEGER, visible INTEGER, image_name TEXT, text TEXT,"
                + "page_id INTEGER, in_possession INTEGER, _id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        String setupStrScripts = "CREATE TABLE scripts ("
                + "trigger INTEGER, action INTEGER, to_shape TEXT, to_resource TEXT, to_page INTEGER,"
                + "shape_id INTEGER, _id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        String[] statements = new String[]{setupStrGames, setupStrPages, setupStrScripts,
                setupStrShapes};
        for(String sql : statements){
            System.err.println(sql);
            db.execSQL(sql);
        }
    }

    /**
     * This method is used to populate the database this should be populated at least with
     * the base Bunny World game
     */
    private void populateDatabase() {
        String dataStr1 = "INSERT INTO games VALUES "
                + "('Bunny World',NULL);";
        String dataStr2 = "INSERT INTO pages VALUES "
                + "('page1', 1, 1 ,NULL), ('page2', 1, 0 ,NULL);";
        String dataStr3 = "INSERT INTO shapes VALUES "
                + "('shape1', 100,100,100,100,1,1,'duck','',1, 0 ,NULL);";
        String dataStr4 = "INSERT INTO shapes VALUES "
                + "('shape2', 500,100,100,100,1,1,'carrot','',1, 0 ,NULL);";
        String dataStr5 = "INSERT INTO shapes VALUES "
                + "('shape3', 500,200,100,100,1,0,'fire','',1, 0 ,NULL);";
        String dataStr6 = "INSERT INTO scripts VALUES "
                + "(1, 1, 'shape2', '', '', 1 ,NULL);";
        String dataStr7 = "INSERT INTO scripts VALUES "
                + "(1, 2, 'shape3', '', '', 1 ,NULL), (1, 4, '', '', 'page2', 3 ,NULL), (3, 2, 'shape3', '', '', 1, NULL), (3, 2, 'shape1', '', '', 3, NULL);";
        //on click = 1, on enter = 2, on drop = 3
        //hide = 1, show = 2, play = 3, goto = 4
        String[] statements = new String[]{dataStr1, dataStr2, dataStr3,
                dataStr4, dataStr5, dataStr6, dataStr7};
        for(String sql : statements){
            System.err.println(sql);
            db.execSQL(sql);
        }
    }

    private void checkDB() {
        String dataStr1 = "select * from games;";
        String dataStr2 = "select * from pages;";
        String dataStr3 = "select * from shapes;";
        String dataStr4 = "select * from scripts;";
        String[] statements = new String[]{dataStr1, dataStr2, dataStr3,
                dataStr4};
        for(String sql : statements){
            Cursor cursor = db.rawQuery(sql,null);

            //this is just to check what is in the DB currently
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                System.out.println(cursor.getString(0));
                cursor.moveToNext();
            }
        }


    }

    private void resetDB() {
        String removeG = "drop table games;";
        String removeP = "drop table pages;";
        String removeSc = "drop table scripts;";
        String removeSh = "drop table shapes;";
        String[] statements1 = new String[]{removeG, removeP, removeSc,
                removeSh};
        for(String sql : statements1){
            System.err.println(sql);
            db.execSQL(sql);
        }
    }


}
