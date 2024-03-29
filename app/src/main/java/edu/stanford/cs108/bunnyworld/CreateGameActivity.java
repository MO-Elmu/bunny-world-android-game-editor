package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CreateGameActivity extends AppCompatActivity {

    private EditText gameName, gameType;
    private Spinner iconSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        gameName = (EditText)findViewById(R.id.game_name);
        //gameType = (EditText)findViewById(R.id.g_type);
        ArrayList<SpinnerAdapter.ItemData> list=new ArrayList<>();
        list.add(new SpinnerAdapter.ItemData("running-rabbit",R.drawable.running_rabbit));
        list.add(new SpinnerAdapter.ItemData("running_rabbit-filled",R.drawable.running_rabbit_filled));
        list.add(new SpinnerAdapter.ItemData("happybunny",R.drawable.happybunny));
        SpinnerAdapter adapter=new SpinnerAdapter(this,
                R.layout.spinner_layout,R.id.txt,list);
        iconSpinner = (Spinner)findViewById(R.id.icon_spinner);
        iconSpinner.setAdapter(adapter);
    }

    public void addGame(View view) {
        //Start the Creation of a new Document/Game instance
        //***** Add code to guard against bad user's input *****
        String name = gameName.getText().toString();
        if(!name.trim().isEmpty()) {
//        String type = gameType.getText().toString();
            String iconName = iconSpinner.getSelectedItem().toString().replace('-', '_');
            System.out.println("#5 ICON NAME: " + iconName);
            String mDrawableName = iconName;
            int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());

            Intent intent = new Intent(this, AddPagesActivity.class);
            intent.putExtra("gameName", name);
            intent.putExtra("game_type", "");
            intent.putExtra("game_icon", resID);
            intent.putExtra("mode", "create");
            startActivity(intent);
        } else {
            Toast.makeText(this,"You must enter a game name!", Toast.LENGTH_SHORT).show();
        }
    }
}
