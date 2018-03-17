package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddShapeDialogFragment.addShapeDialogFragmentListener} interface
 * to handle interaction events.
 */
public class AddShapeDialogFragment extends DialogFragment implements View.OnClickListener{


    private addShapeDialogFragmentListener mListener;
    private Spinner imagesSpinner;
    private EditText shapeName, shapeText, shapeTxtFont, shapeOnClickScript, shapeOnEnterScript, shapeOnDropScript;
    private CheckBox onClick, onEnter, onDrop, movable, invisible, possessable;
    List<Shape> Shapes;
    Shape selectedShape;
    String imagename;

    /*public AddShapeDialogFragment() {
        // Required empty public constructor
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_shape_dialog, container, false);
        Button save = (Button) view.findViewById(R.id.save_shape);
        Button cancel = (Button)view.findViewById(R.id.cancel_shape);
        shapeName = (EditText)view.findViewById(R.id.shape_name);

        shapeText = (EditText)view.findViewById(R.id.shape_txt);
        shapeTxtFont = (EditText)view.findViewById(R.id.font_size);
        shapeOnClickScript = (EditText)view.findViewById(R.id.on_click_script);
        shapeOnEnterScript = (EditText)view.findViewById(R.id.on_enter_script);
        shapeOnDropScript = (EditText)view.findViewById(R.id.on_drop_script);
        onClick = (CheckBox)view.findViewById(R.id.on_click);
        onEnter = (CheckBox)view.findViewById(R.id.on_enter);
        onDrop = (CheckBox)view.findViewById(R.id.on_drop);
        movable = (CheckBox)view.findViewById(R.id.checkbox_movable);
        invisible = (CheckBox)view.findViewById(R.id.checkbox_invisible);
        possessable = (CheckBox)view.findViewById(R.id.checkbox_possessable);
        
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        ArrayList<SpinnerAdapter.ItemData> list = new ArrayList<>();
        list.add(new SpinnerAdapter.ItemData("carrot",R.drawable.carrot));
        list.add(new SpinnerAdapter.ItemData("carrot2",R.drawable.carrot2));
        list.add(new SpinnerAdapter.ItemData("duck",R.drawable.duck));
        list.add(new SpinnerAdapter.ItemData("death",R.drawable.death));
        list.add(new SpinnerAdapter.ItemData("fire",R.drawable.fire));
        list.add(new SpinnerAdapter.ItemData("mystic",R.drawable.mystic));
        list.add(new SpinnerAdapter.ItemData("easterBunny",R.drawable.easterbunny));
        list.add(new SpinnerAdapter.ItemData("happyBunny",R.drawable.happybunny));
        SpinnerAdapter adapter=new SpinnerAdapter(getActivity(),
                R.layout.spinner_layout,R.id.txt,list);
        imagesSpinner = (Spinner)view.findViewById(R.id.image_spinner);
        imagesSpinner.setAdapter(adapter);

        // Get selectedShape info by ShapeSingleton Class
        selectedShape = ShapeSingleton.getInstance().getSelectedShape();
        Shapes = ShapeSingleton.getInstance().getSelectedShapeContainer();

        if (selectedShape != null ) {

            shapeName.setText(selectedShape.getName());
            shapeText.setText(selectedShape.getText());
            shapeTxtFont.setText( String.valueOf (selectedShape.getTxtFontSize() ) );
            shapeOnClickScript.setText(selectedShape.getOnClickScript());
            shapeOnEnterScript.setText(selectedShape.getOnEnterScript());
            shapeOnDropScript.setText(selectedShape.getOnDropScript());
            onClick.setChecked(selectedShape.isOnClick());
            onEnter.setChecked(selectedShape.isOnEnter());
            onDrop.setChecked(selectedShape.isOnDrop());
            movable.setChecked(selectedShape.isMovable());
            invisible.setChecked(!selectedShape.isVisible());
            possessable.setChecked(selectedShape.getPossessable() == 1);
            imagename = selectedShape.getImageName();
            for (int i = 0; i < list.size(); i++ ){
                if (list.get(i).text.equals(imagename) ) {
                    imagesSpinner.setSelection(i);
                    break;
                }
            }


        }

        return view;
    }

    //this is from google example
    /** The system calls this only when creating the layout in a dialog.*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the OnFragmentInteractionListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the OnFragmentInteractionListener so we can send events to the host
            mListener = (addShapeDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AlertDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_shape:
                boolean checkBoxValues[] = {onClick.isChecked(), onEnter.isChecked(), onDrop.isChecked(), movable.isChecked(), invisible.isChecked(), possessable.isChecked()};
                String[] shapeStringValues = {shapeName.getText().toString(), shapeText.getText().toString(),
                        shapeTxtFont.getText().toString(), imagesSpinner.getSelectedItem().toString(),
                        shapeOnClickScript.getText().toString(), shapeOnEnterScript.getText().toString(),
                        shapeOnDropScript.getText().toString()};
                if (mListener != null) {
                    System.out.println("#7 "+ validateScript(shapeOnClickScript.getText().toString(),"click", checkBoxValues ));
                    System.out.println("#7 "+ validateScript(shapeOnEnterScript.getText().toString(),"enter", checkBoxValues ));
                    System.out.println("#7 "+ validateScript(shapeOnDropScript.getText().toString(),"drop", checkBoxValues ));
                    if(validateScript(shapeOnClickScript.getText().toString(),"click", checkBoxValues ) &&
                        validateScript(shapeOnEnterScript.getText().toString(),"enter", checkBoxValues ) &&
                        validateScript(shapeOnDropScript.getText().toString(),"drop", checkBoxValues )){
                        mListener.onSaveShapeClick(shapeStringValues, checkBoxValues);

                    }
                }
                break;
            case R.id.cancel_shape:
                if (mListener != null) {
                    mListener.onCancelShapeClick(view);
                }
                break;
        }
    }

    private boolean checkActions(String script, String scriptType){
        String[] clauses = script.trim().split(";");

        for (int i = 0; i < clauses.length; i++) {

            System.out.println("CLAUSES" + clauses[i]);

            String triggerRecipient = "";

            String[] words = clauses[i].trim().split(" ");


            System.out.println("WORDS" + words);
            int start;

            if (scriptType.equals("drop")) {
                triggerRecipient = words[0];
                start = 1;
            } else {
                start = 0;
            }

            int actionCounter = 0;
            for (int j = start; j < words.length; j += 2) {
                System.out.println(words[j] + "WORDS");
                System.out.println(words.length + "WORDS length");
                System.out.println(scriptType + "type");
                String actionName = words[j].toUpperCase();
                try {
                    String actionRecipient = words[j + 1];
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(getActivity(),"Your script does not have an action recipient!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!actionName.equals("SHOW") && !actionName.equals("HIDE") && !actionName.equals("GOTO")
                        && !actionName.equals("PLAY")) {
                    Toast.makeText(getActivity(),"Your script does not have the right action word!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                actionCounter++;
            }
        }
        return true;

    }

    private boolean validateScript(String script, String scriptType, boolean[] checkBox) {
        if( scriptType.equals("click")) {
            System.out.println("CCHEC ACTIONS 1");
            if(checkBox[0]){ //if the on click script is checked, check there is a script there
                System.out.println("#7 script "+script);
                if(script.length()<1){
                    Toast.makeText(getActivity(),"Your on click script has too few words!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return checkActions(script, scriptType);
                }
            } else {
                if(script.length()>0){
                    Toast.makeText(getActivity(),"Your on click script is not checked!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else if( scriptType.equals("enter")) {
            if(checkBox[1]){ //if the on enter script is checked, check there is a script there
                System.out.println("#7 script "+script);
                if(script.length()<1){
                    Toast.makeText(getActivity(),"Your on enter script has too few words!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return checkActions(script, scriptType);
                }
            } else {
                if(script.length()>0){
                    Toast.makeText(getActivity(),"Your on enter script is not checked!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else if(scriptType.equals("drop")) {
            if(checkBox[2]){ //if the on drop script is checked, check there is a script there
                System.out.println("#7 script "+script);
                if(script.length()<2){
                    Toast.makeText(getActivity(),"Your on drop script has too few words!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return checkActions(script, scriptType);
                }
            } else { //if box has script, no checkmark
                if(script.length()>0){
                    Toast.makeText(getActivity(),"Your on drop script is not checked!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface addShapeDialogFragmentListener {
        public void onSaveShapeClick(String[] shapeStringValues, boolean[] checkBoxValues);
        public void onCancelShapeClick(View view);
    }
}
