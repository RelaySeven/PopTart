package org.pltw.examples.poptartinventory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mrisk on 11/9/2016.
 */
public class MyAdapter extends ArrayAdapter<PopTart> {
    ArrayList<PopTart> popTartList = new ArrayList<>();
    Button buttonOne;
    View.OnTouchListener mTouchListener;

    public MyAdapter(Context context, int textViewResourceId, ArrayList<PopTart> tarts, View.OnTouchListener listener){
        super(context, textViewResourceId, tarts);
        popTartList = tarts;
        mTouchListener = listener;



    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_view_inventory, null);

        TextView nameView = (TextView) v.findViewById(R.id.name);
        final TextView countView = (TextView) v.findViewById(R.id.count);
        final EditText countUpdate = (EditText) v.findViewById(R.id.edit_text);
        CheckBox seasonal = (CheckBox) v.findViewById(R.id.seasonal);

        String temp = String.valueOf(popTartList.get(position).getCount()) + " / " + String.valueOf(popTartList.get(position).getMinimum());
            nameView.setText(popTartList.get(position).getName());
            countView.setText(temp);
            seasonal.setChecked(popTartList.get(position).getSeasonal());
            seasonal.setEnabled(false);

        buttonOne = (Button) v.findViewById(R.id.increment);

        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (countUpdate.getText().toString().trim().length() == 0)
                 {
                     countUpdate.clearFocus();
                     hideKeyboardFrom(getContext() , v);
                }
                else {

                String temp2 = countUpdate.getText().toString();

                PopTart temp = popTartList.get(position);
                temp.setCount(Integer.valueOf(temp2));
                String temp3 = String.valueOf(popTartList.get(position).getCount()) + " / " + String.valueOf(popTartList.get(position).getMinimum());
                popTartList.set(position, temp);
                countView.setText(temp3);
                countUpdate.setText("");
                countUpdate.clearFocus();
                hideKeyboardFrom(getContext() , v);
                String saveStuff = "";
                    for (int i = 0; i < popTartList.size(); i++) {

                        if (popTartList.get(i).getName().equals("null")) {
                        } else {
                            saveStuff += popTartList.get(i).getName() + "/" + String.valueOf(popTartList.get(i).getCount()) + "/" + String.valueOf(popTartList.get(i).getMinimum()) + "/" + String.valueOf(popTartList.get(i).getSeasonal()) + "/";
                        }
                    }
                    new MainActivity().writeFile(saveStuff);
                    new MainActivity().load();

                }

            }
        });

        v.setOnTouchListener(mTouchListener);

        return v;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
