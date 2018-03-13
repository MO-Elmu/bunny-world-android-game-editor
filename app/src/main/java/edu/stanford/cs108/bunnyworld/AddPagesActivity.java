package edu.stanford.cs108.bunnyworld;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddPagesActivity extends AppCompatActivity implements AlertDialogFragment.AlertDialogListener, AddShapeDialogFragment.addShapeDialogFragmentListener {

    SQLiteDatabase db;

    private Document newGame;
    boolean isPageCreated;  //control active/inactive for add shape menu item
    boolean isCurrPageSaved; //control active/inactive for add page menu item
    private Page newPage;
    private LinearLayout mLayout;
    AddShapeDialogFragment addShapeDialogFragment;
    private SubMenu savedPagesSubMenu;  //user can move between already created pages and Edit them.
    int pageCounter=0; //this for testing only it should be deleted when the code is ready

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pages);
        isPageCreated = false;  //make sure addShape menu starts inactive till user adds a page
        isCurrPageSaved = true;
        Intent intent = getIntent();
        String gameName = intent.getStringExtra("gameName");
        String gameType = intent.getStringExtra("game_type");
        String gameIcon = intent.getStringExtra("game_icon");
        mLayout = (LinearLayout)findViewById(R.id.add_page);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.setWeightSum(1.0f);
        mLayout.setVerticalGravity(Gravity.BOTTOM);
        newGame = new Document(this.getApplicationContext(), gameName, gameType, gameIcon);
        Possessions possessions = new Possessions(this.getApplicationContext());
        mLayout.addView(possessions);

    }

    //Handling Add Page options Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_page_menu,menu);
        MenuItem createdPages = menu.findItem(R.id.created_pages);
        savedPagesSubMenu = createdPages.getSubMenu();
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.created_pages:
                savedPagesSubMenu.clear();
                if(newGame.getChildCount()>0){
                    for(int i=0; i< newGame.getChildCount(); i++){
                        final Page page = (Page)newGame.getChildAt(i);
                        savedPagesSubMenu.add(page.getPageName());
                    }
                }
                break;
            case R.id.create_page:
                this.showAlertDialog();
                break;
            case R.id.add_shape:
                showAddShapeDialog();
                //Toast toast = Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT);
                //toast.show();
                break;
            case R.id.save_page:
                savePage();
                break;
            case R.id.delete_page:
                deletePage();
                break;
            case R.id.play_game:
                playGame();
                break;
            case R.id.exit_game:
                exitGame();
                break;
            case R.id.save_game:
                saveGame();
                break;

            default:
                for(int i=0; i< newGame.getChildCount(); i++){
                    final Page page = (Page)newGame.getChildAt(i);
                    if(item.getTitle().toString().equals(page.getPageName())){
                        page.setVisibility(View.VISIBLE);
                        newPage = page;
                        newGame.removeViewAt(i);
                        if(mLayout.getChildCount()>1) {
                            for (int j = 0; j < mLayout.getChildCount(); j++) {
                                if (mLayout.getChildAt(j) instanceof Page) {
                                    mLayout.getChildAt(j).setVisibility(View.GONE);
                                }
                            }
                        }
                        mLayout.addView(newPage, 0);
                        isPageCreated = true;
                        isCurrPageSaved = false;
                        invalidateOptionsMenu();
                        //mLayout.invalidate();
                        //newPage = null;
                        //newPage = (Page)mLayout.getChildAt(0);

                        // mLayout.bringChildToFront(page);
                        break;
                    }
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem addShapeItem = menu.findItem(R.id.add_shape);
        MenuItem addPagetItem = menu.findItem(R.id.create_page);
        if(!isPageCreated){
            addShapeItem.setEnabled(false);
            addShapeItem.getIcon().setAlpha(70);
        }else {
            addShapeItem.setEnabled(true);
            addShapeItem.getIcon().setAlpha(255);
        }
        if(!isCurrPageSaved) {
            addPagetItem.setEnabled(false);
            addPagetItem.getIcon().setAlpha(70);
        }else{
            addPagetItem.setEnabled(true);
            addPagetItem.getIcon().setAlpha(255);
        }

        return true;
    }


    //Show Alert Dialog to add page when add Page is clicked
    public void showAlertDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "AlertFragment");
    }

    //Show addShape Dialog
    public void showAddShapeDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        addShapeDialogFragment = new AddShapeDialogFragment();
        //addShapeDialogFragment.show(fragmentManager, "addShapeDialog");
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, addShapeDialogFragment)
                .addToBackStack(null).commit();

    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the AlerDialogFragment.AlertDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        isPageCreated = true;
        isCurrPageSaved = false;
        invalidateOptionsMenu();
        Dialog dialogView = dialog.getDialog();
        EditText pageName = (EditText)dialogView.findViewById(R.id.page_name);
        newPage = new Page(this.getApplicationContext()); //add logic if the user leaves pageName blank
        if(pageName.getText().toString().trim().isEmpty()) newPage.setPageName("page " + (newGame.getChildCount()+1));
        else newPage.setPageName(pageName.getText().toString());
        newPage.setVisibility(View.VISIBLE);
        mLayout.addView(newPage, 0);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        isPageCreated = false;
        isCurrPageSaved = true;
        invalidateOptionsMenu();
        dialog.getDialog().cancel();
    }

    /*** This a helper method to make the process of
     constructing a shape easier
     The array parameters are
     allShapeStringParams[0] = shape name
     allShapeStringParams[1] = shape text
     allShapeStringParams[2] = txt font as string call Integer.valueOf to convert this value to int
     allShapeStringParams[3] = image name
     allShapeStringParams[4] = onClick script
     allShapeStringParams[5] = onEnter script
     allShapeStringParams[6] = onDrop script
     for the boolean array it contains the script actions as a true or false
     scriptsActions[0] = on click action true/false
     scriptsActions[1] = on enter action
     scriptsActions[2] = on drop action
     scriptsActions[3] = movable
     ***/
    private void createShape(String[] allShapeStringParams, boolean[] scriptsActions, Shape shape){
        shape.setName(allShapeStringParams[0]);
        shape.setText(allShapeStringParams[1]);
        if(!allShapeStringParams[2].trim().isEmpty()){
            float scaledFontSize = Integer.valueOf(allShapeStringParams[2]) * getResources().getDisplayMetrics().scaledDensity;
            shape.setTxtFontSize((int)scaledFontSize);
        }
        shape.setImage(allShapeStringParams[3],this.getApplicationContext());
        shape.setOnClickScript(allShapeStringParams[4]);
        shape.setOnEnterScript(allShapeStringParams[5]);
        shape.setOnDropScript(allShapeStringParams[6]);
        shape.setOnClick(scriptsActions[0]);
        shape.setOnEnter(scriptsActions[1]);
        shape.setOnDrop(scriptsActions[2]);
        shape.setMovable(scriptsActions[3]);

    }
    //Shape dialog listener interface methods
    @Override
    public void onSaveShapeClick(String[] shapeStringValues, boolean[] checkBoxValues) {
        Shape shape = new Shape();
        createShape(shapeStringValues, checkBoxValues, shape);
        //mLayout.removeView(newPage);
        newPage.addShape(shape);
        //mLayout.addView(newPage);
        addShapeDialogFragment.dismiss();
    }

    private void savePage() {
        //Handle the logic when save page is clicked
        //mLayout.
        isCurrPageSaved = true;
        isPageCreated = false;
        invalidateOptionsMenu();
        if(newPage == null){
            Toast toast = Toast.makeText(getApplicationContext(), "No page is created", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        //pageCounter += 1;
        mLayout.removeView(newPage);
        //if(pageCounter!=1)newPage.setVisibility(View.GONE); // changes here when you add starter page option
        newGame.addView(newPage);
        if(mLayout.getChildCount()>1) {
            for (int j = 0; j < mLayout.getChildCount(); j++) {
                if (mLayout.getChildAt(j) instanceof Page) {
                    mLayout.getChildAt(j).setVisibility(View.VISIBLE);
                    newPage = (Page)mLayout.getChildAt(j);
                    break;
                }
            }
            isCurrPageSaved = false;
            isPageCreated = true;
            invalidateOptionsMenu();
            return;

        }

        newPage = null;

    }

    //Handle the logic when delete page is clicked
    private void deletePage() {
        isCurrPageSaved = true;
        isPageCreated = false;
        invalidateOptionsMenu();
        mLayout.removeView(newPage);
        if(mLayout.getChildCount()>1) {
            for (int j = 0; j < mLayout.getChildCount(); j++) {
                if (mLayout.getChildAt(j) instanceof Page) {
                    mLayout.getChildAt(j).setVisibility(View.VISIBLE);
                    newPage = (Page)mLayout.getChildAt(j);
                    break;
                }
            }
            isCurrPageSaved = false;
            isPageCreated = true;
            invalidateOptionsMenu();
            return;

        }

        newPage = null;
    }

    private void playGame(){
        //My code to help designer play the game he just finished creating
        // before it goes into the database
    }
    private void exitGame(){
        //My code to help designer exit the game he just finished creating
        // can continue editing before it goes into the database
    }
    //saves the current game in the database
    private void saveGame(){
        this.saveGameInDataBase(newGame);
    }


    /*** This method saves the game into the Database. The game
     * is the newGame object instantiated here in this activity
     *
     * Farhad Code goes here, can add private methods as many as
     * needed to decompose the below method.
     */
    private void saveGameInDataBase(Document game){

        db = openOrCreateDatabase("BunnyDB",MODE_PRIVATE,null);

        //if (!checkTableExist("games"))
            setupGameTable();

        //if (!checkTableExist("pages"))
            setupPageTable();

        //if (!checkTableExist("shapes"))
            setupShapeTable();

        //if (!checkTableExist("triggers"))
            setupTriggerTable();

        //if (!checkTableExist("actions"))
            setupActionTable();

        //if (!checkTableExist("scripts"))
            setupScriptTable();

        // save game in db
        String gameName = game.getGameName();
        String gameIcon = game.getIconName();
        String addStr = "INSERT INTO games VALUES "
                + String.format("('%s', '%s', NULL)", gameName, gameIcon)
                + ";";
        db.execSQL(addStr);

        // save page in db
        for (int i = 0; i < game.getChildCount(); i++) {
            if (game.getChildAt(i) instanceof Page) {
                Page page = (Page) game.getChildAt(i);
                String pageName = page.getPageName();
                int starterPageFlag = page.getFirstPageFlag();
                addStr = "INSERT INTO pages VALUES "
                        + String.format("('%s', '%s', %d, NULL)", pageName, gameName, starterPageFlag)
                        + ";";
                db.execSQL(addStr);

                for (Shape shape : page.shapes) {
                    String shapeName = shape.getName();
                    String imageName = shape.getImageName();
                    String caption = shape.getText();
                    int inPossession = shape.isInPossession() ? 1 : 0;
                    int possessable = shape.getPossessable();
                    int visible = shape.isVisible() ? 1: 0;
                    int movable = shape.isMovable() ? 1: 0;
                    int xPos = shape.getX1();
                    int yPos = shape.getY1();
                    int width = shape.getWidth();
                    int height = shape.getHeight();

                    addStr = "INSERT INTO shapes VALUES "
                            + String.format("('%s', '%s', '%s', '%s', '%s', %d, %d, %d, %d, %d, %d, %d, %d, NULL)",
                            shapeName, pageName, gameName, imageName, caption,
                            inPossession, possessable, visible, movable, xPos, yPos, width, height)
                            + ";";
                    db.execSQL(addStr);

                    boolean onClick = shape.isOnClick();
                    boolean onEnter = shape.isOnEnter();
                    boolean onDrop = shape.isOnDrop();

                    if (shape.isOnClick())
                        saveScript(gameName, shapeName,"CLICK", shape.getOnClickScript(), 0);
                    if (shape.isOnEnter())
                        saveScript(gameName, shapeName,"ENTER", shape.getOnEnterScript(), 0);
                    if (shape.isOnDrop())
                        saveScript(gameName, shapeName,"DROP", shape.getOnDropScript(), 1);
                }
            }
        }

        // print tables
        System.out.println(tableToString("games"));
        System.out.println(tableToString("pages"));
        System.out.println(tableToString("shapes"));
        System.out.println(tableToString("scripts"));

    }

    void saveScript(String gameName, String shapeName, String triggerName, String script,
                    int isOnDrop){
        String[] parts = script.trim().split(";");
        String triggerRecipient = "";
        System.out.println(script);

        for (int i = 0; i < parts.length; i++) {
            String[] words = parts[i].trim().split(" ");
            int start;

            if ((isOnDrop == 1) && (i == 0)) {
                triggerRecipient = words[0];
                start = 1;
            } else {
                start = 0;
            }

            String addStr = "";
            for (int j = start; j < words.length; j += 2) {
                String actionName = words[j].toUpperCase();
                String actionRecipient = words[j + 1];

                if (isOnDrop == 1) {
                    if (actionName.equals(Shape.SHOW) || actionName.equals(Shape.HIDE))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', '%s', '%s', '%s', NULL, NULL, NULL)",
                                gameName, shapeName, triggerName, triggerRecipient, actionName, actionRecipient)
                                + ";";
                    if (actionName.equals(Shape.PLAY))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', '%s', '%s', NULL,'%s', NULL, NULL)",
                                gameName, shapeName, triggerName, triggerRecipient, actionName, actionRecipient)
                                + ";";
                    if (actionName.equals(Shape.GOTO))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', '%s', '%s', NULL, NULL, '%s', NULL)",
                                gameName, shapeName, triggerName, triggerRecipient, actionName, actionRecipient)
                                + ";";
                } else {
                    if (actionName.equals(Shape.SHOW) || actionName.equals(Shape.HIDE))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', NULL, '%s', '%s',  NULL, NULL, NULL)",
                                gameName, shapeName, triggerName, actionName, actionRecipient)
                                + ";";
                    if (actionName.equals(Shape.PLAY))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', NULL, '%s', NULL,'%s', NULL, NULL)",
                                gameName, shapeName, triggerName, actionName, actionRecipient)
                                + ";";
                    if (actionName.equals(Shape.GOTO))
                        addStr = "INSERT INTO scripts VALUES "
                                + String.format("('%s', '%s', '%s', NULL, '%s', NULL, NULL, '%s', NULL)",
                                gameName, shapeName, triggerName, actionName, actionRecipient)
                                + ";";
                }
                db.execSQL(addStr);
            }
        }
    }
    // helper function to print table, shoudl be removed after debugging
    // https://stackoverflow.com/questions/27003486/printing-all-rows-of-a-sqlite-database-in-android
    private String tableToString(String tableName) {

        String tableString = String.format("Table %s:\n", tableName);

        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        tableString += cursorToString(cursor);
        return tableString;
    }

    private String cursorToString(Cursor cursor) {
        String cursorString = "";
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            for (String name: columnNames)
                cursorString += String.format("%s ", name);
            cursorString += "\n";
            do {
                for (String name: columnNames) {
                    cursorString += String.format("%s  ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        cursorString += "\n";
        return cursorString;
    }
    // end of helper function to print tables

    private boolean checkTableExist(String tableName) {
        String[] queryParameters = {tableName};
        Cursor tablesCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name LIKE ?;",
                queryParameters);
        return tablesCursor.getCount() != 0;
    }

    private void setupGameTable() {
        String clearStr = "DROP TABLE IF EXISTS games;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE games ("
                + "game_name TEXT,"
                + "game_icon TEXT,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    private void setupPageTable() {
        String clearStr = "DROP TABLE IF EXISTS pages;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE pages ("
                + "page_name TEXT,"
                + "game_name TEXT,"
                + "visible INTEGER,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    private void setupShapeTable() {
        String clearStr = "DROP TABLE IF EXISTS shapes;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE shapes ("
                + "shape_name TEXT,"
                + "page_name TEXT,"
                + "game_name TEXT,"
                + "caption TEXT,"
                + "image_file TEXT,"
                + "in_possession INTEGER,"
                + "possessable INTEGER,"
                + "visible INTEGER,"
                + "movable INTEGER,"
                + "x_position INTEGER,"
                + "y_position INTEGER,"
                + "width INTEGER,"
                + "height INTEGER,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    private void setupTriggerTable() {
        String clearStr = "DROP TABLE IF EXISTS triggers;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE triggers ("
                + "trigger_name TEXT,"
                + "shape_name TEXT,"
                + "game_name TEXT,"
                + "trigger_recipient TEXT,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    private void setupActionTable() {
        String clearStr = "DROP TABLE IF EXISTS actions;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE actions ("
                + "action_name TEXT,"
                + "trigger_name TEXT,"
                + "shape_name TEXT,"
                + "game_name TEXT,"
                + "action_recipient TEXT,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    private void setupScriptTable() {
        String clearStr = "DROP TABLE IF EXISTS scripts;";
        db.execSQL(clearStr);
        String setupStr = "CREATE TABLE scripts ("
                + "game_name TEXT,"
                + "shape_name TEXT,"
                + "trigger_name TEXT," // CLICK, ENTER, DROP
                + "trigger_recipient TEXT," // Drop recipient
                + "action_name TEXT,"
                + "show_hide_recipient TEXT,"
                + "play_recipient TEXT,"
                + "goto_recipient TEXT,"
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupStr);
    }

    @Override
    public void onCancelShapeClick(View view) {
        //Toast toast = Toast.makeText(getApplicationContext(), "Cancel Clicked", Toast.LENGTH_SHORT);
        //toast.show();
        addShapeDialogFragment.dismiss();
    }

}