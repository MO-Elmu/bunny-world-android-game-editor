package edu.stanford.cs108.bunnyworld;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by emohelw on 2/27/2018.
 *
 * This Class is done by following this tutorial on how to create a custom spinner
 * http://www.worldbestlearningcenter.com/tips/Android-Spinner-customized-to-show-image-and-text.htm
 */

public class SpinnerAdapter extends ArrayAdapter<SpinnerAdapter.ItemData> {

    private int groupid;
    private Activity context;
    private ArrayList<ItemData> list;
    LayoutInflater inflater;
    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData> list){
        super(context,id,list);
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){

        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.img);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView=(TextView)itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;

    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }

    //A simple nested class to represent an Item Data that contains an image and a text
    protected static class ItemData {

        String text;
        Integer imageId;
        public ItemData(String text, Integer imageId){
            this.text=text;
            this.imageId=imageId;
        }

        public String getText(){
            return text;
        }

        public Integer getImageId(){
            return imageId;
        }

        public String toString(){
            return getText();
        }
    }



}

