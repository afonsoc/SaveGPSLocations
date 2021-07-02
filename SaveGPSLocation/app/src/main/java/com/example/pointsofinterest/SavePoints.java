package com.example.pointsofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SavePoints extends AppCompatActivity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_point);
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        EditText etN = (EditText) findViewById(R.id.etN);
        EditText etT = (EditText) findViewById(R.id.etT);
        EditText etDesc = (EditText) findViewById(R.id.etDesc);
        String name = etN.getText().toString();
        String type = etT.getText().toString();
        String desc = etDesc.getText().toString();

        Toast.makeText(this, "name - " + name + " type - " + type + " desc - " + desc, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("ptsName", name);
        bundle.putString("ptsType", type);
        bundle.putString("ptsDesc", desc);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}

