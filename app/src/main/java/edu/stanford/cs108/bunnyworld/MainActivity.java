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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    ArrayList<Integer> mThumbIds = new ArrayList<Integer>();
    ArrayList<String> text = new ArrayList<String>();
    String chosenGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("BunnyDB",MODE_PRIVATE,null);
        String sql = "select * from games";

        setupDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor.getCount() != 0) {
            resetDB();
            setupDatabase();
        }
        populateDatabase();
        checkDB();

        loadGames();

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this, mThumbIds, text));
        final Button playButton = (Button) findViewById(R.id.playGame);
        final Button createButton = (Button) findViewById(R.id.createGame);
        playButton.setVisibility(View.GONE);
        createButton.setVisibility(View.GONE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();

                if(text.get(i).equals("")){
                    createButton.setText("Create New Game");
                    createButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                } else {
                    playButton.setText("Play " + text.get(i));
                    createButton.setText("Edit " + text.get(i));
                    playButton.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    chosenGame = text.get(i);
                }

            }
        });
    }

    public void loadGames() {

        String sql = "SELECT * FROM games;";
        Cursor cursor = db.rawQuery(sql,null);

        //this is just to check what is in the DB currently
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            System.out.println(cursor.getString(0));
            text.add(cursor.getString(0));
            mThumbIds.add(cursor.getInt(1));
            cursor.moveToNext();
        }
        text.add("");
        mThumbIds.add(R.drawable.plus);
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
        intent.putExtra("game", chosenGame);
        startActivity(intent);
    }

    /**
     -     * This method is used to add setup the database.
     -     */
    private void setupDatabase() {
        System.out.println("setting up db");
        String setupStr1 = "CREATE TABLE if not exists games ("
                + "game_name TEXT,"
                + "game_icon INTEGER,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        System.out.println(setupStr1);
        db.execSQL(setupStr1);
        String setupStr2 = "CREATE TABLE if not exists pages ("
                + "page_name TEXT,"
                + "game_name TEXT,"
                + "visible INTEGER,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr2);

        String setupStr3 = "CREATE TABLE if not exists shapes ("
                + "shape_name TEXT," //0
                + "page_name TEXT,"  //1
                + "game_name TEXT,"  //2
                + "caption TEXT,"    //3
                + "image_file TEXT," //4
                + "in_possession INTEGER," //5
                + "possessable INTEGER," //6
                + "visible INTEGER,"     //7
                + "movable INTEGER,"     //8
                + "x_position INTEGER,"  //9
                + "y_position INTEGER,"  //10
                + "width INTEGER,"       //11
                + "height INTEGER,"      //12
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr3);

        String setupStrScripts = "CREATE TABLE if not exists  scripts ("
                + "trigger INTEGER, action INTEGER, to_shape TEXT, to_resource TEXT, to_page INTEGER,"
                + "drop_shape TEXT, shape_id INTEGER, _id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        String setupStr4 = "CREATE TABLE if not exists scripts ("
                + "game_name TEXT," //0
                + "shape_name TEXT," //1
                + "trigger_name TEXT," //2  CLICK, ENTER, DROP
                + "trigger_recipient TEXT," //3 Drop recipient
                + "action_name TEXT," //4
                + "show_hide_recipient TEXT," //5
                + "play_recipient TEXT," //6
                + "goto_recipient TEXT," //7
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr4);
    }

    /**
     * This method is used to populate the database this should be populated at least with
     * the base Bunny World game
     */
    private void populateDatabase() {
        Integer bunn = R.drawable.happybunny;
        String dataStr1 = "INSERT INTO games VALUES "
                + "('Bunny World'," + bunn + ",NULL);";
        String dataStr2 = "INSERT INTO pages VALUES "
                + "('page1', 'Bunny World', 1 ,NULL), ('page2', 'Bunny World', 0 ,NULL), ('page3', 'Bunny World', 0 ,NULL), ('page4', 'Bunny World', 0 ,NULL);";
        String dataStr3 = "INSERT INTO shapes VALUES "
                + "('shape1', 'page2', 'Bunny World', '','mystic', 0, 0,1,0, 100,100,200,200,NULL);";
        String dataStr4 = "INSERT INTO shapes VALUES "
                + "('shape2', 'page2', 'Bunny World','Mystic Bunny Rub my tummy','',0,0,1,0, 250,200,100,100,NULL);";
        String dataStr5 = "INSERT INTO shapes VALUES "
                + "('shape3', 'page2', 'Bunny World', '','',0,0,1,0,100,500,100,100,NULL),"
                + "('shape4', 'page1', 'Bunny World', '','',0,0,1,0,100,500,100,100,NULL),"
                + "('shape5', 'page1', 'Bunny World','you are in a maze of twisty little passages, all alike.','',0,0,1,0,100,100,100,100,NULL);";
               // + "('shape6', 320,520,100,100,0,0,'','',1, 0 ,NULL), ('shape7', 320,320,100,100,0,1,'fire','',3, 0 ,NULL),"
                //+ "('shape8', 320,520,100,100,0,1,'','Eek fire room. Run away!',3, 0 ,NULL), ('shape9', 600,320,100,100,1,1,'carrot','',3, 0 ,NULL),"
               // + "('shape10', 520,500,100,100,0,1,'','',1, 0 ,NULL), ('shape11', 520,600,100,100,0,1,'','',3, 0 ,NULL),"
               // + "('shape12', 320,200,200,200,0,1,'death','',4, 0 ,NULL), ('shape13', 120,600,100,100,0,1,'','you must appease the bunny of death',4, 0 ,NULL),"
               // + "('shape14', 20,200,100,100,1,1,'carrot','',4, 0 ,NULL), ('shape15', 200,600,100,100,0,0,'','',4, 0 ,NULL);";

        String dataStr7 = "INSERT INTO scripts VALUES "
                + " ('Bunny World', 'shape4', 'CLICK','','GOTO','','','page2',NULL);";//, (1, 1, 'shape9', '', '', '', 1 ,NULL),"
                //+" (1, 4, '', '', 'page1', '', 3 ,NULL), (1, 4, '', '', 'page3', '', 6 ,NULL),"
                //+" (1, 4, '', '', 'page4', '', 10 ,NULL), (1, 4, '', '', 'page2', '', 11 ,NULL),"
                //+" (2, 2, 'shape6', '', '', '', 1 ,NULL), (2, 3, '', 'fire', '', '', 7 ,NULL),"
                //+" (2, 3, '', 'evillaugh', '', '', 12 ,NULL), (1, 3, '', 'munch', '', '', 1 ,NULL),"
                //+" (3, 3, '', 'munching', '', 'shape14', 12 ,NULL), (3, 2, 'shape15', '', '', 'shape14', 12 ,NULL),"
                //+" (3, 1, 'shape12', '', '', 'shape14', 12 ,NULL), (3, 1, 'shape14', '', '', 'shape14', 12 ,NULL);";

        //on click = 1, on enter = 2, on drop = 3
        //hide = 1, show = 2, play = 3, goto = 4
        String[] statements = new String[]{dataStr1, dataStr2, dataStr3,
                dataStr4, dataStr5, dataStr7};
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


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<String> result;
        ArrayList<Integer> imageId;


        private LayoutInflater inflater = null;
        public ImageAdapter(Context c, ArrayList<Integer> ints, ArrayList<String> strings) {
            // TODO Auto-generated constructor stub
            result = strings;
            mContext = c;
            imageId = ints;
            inflater = ( LayoutInflater )mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public int getCount() {
            return imageId.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public class Holder {
            TextView thumb_text;
            ImageView thumb_img;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder = new Holder();
            View rowView = new View(mContext);

            rowView = inflater.inflate(R.layout.sample_gridview, null);
            holder.thumb_text =(TextView) rowView.findViewById(R.id.texts);
            holder.thumb_img =(ImageView) rowView.findViewById(R.id.images);
            holder.thumb_img.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
            holder.thumb_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.thumb_img.setPadding(8, 8, 8, 8);

            holder.thumb_text.setText(result.get(position));
            holder.thumb_img.setImageResource(imageId.get(position));


            return rowView;
        }

    }


}