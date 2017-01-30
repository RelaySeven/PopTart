package org.pltw.examples.poptartinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AdditionActivity extends AppCompatActivity {

    EditText flavor;
    CheckBox seasonal;
    EditText number;
    EditText min;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        flavor = (EditText) findViewById(R.id.newFlavor);
        seasonal = (CheckBox) findViewById(R.id.isSeasonal);
        number = (EditText) findViewById(R.id.newCurrent);
        min = (EditText) findViewById(R.id.newMinimum);

        updateButton = (Button) findViewById(R.id.addNewFlavor);
        updateButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (flavor.getText().toString().trim().length() == 0 || number.getText().toString().trim().length() == 0 || min.getText().toString().trim().length() == 0) {
                    Intent intent = new Intent(AdditionActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent(AdditionActivity.this, MainActivity.class);
                    intent.putExtra("flavor",flavor.getText().toString().trim());
                    intent.putExtra("seasonal", seasonal.isChecked());
                    intent.putExtra("current", Integer.valueOf(number.getText().toString()));
                    intent.putExtra("min", Integer.valueOf(min.getText().toString()));

                    startActivity(intent);

                }

            }
        });
    }
}
