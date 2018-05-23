package edu.calstatela.jplone.watertrekapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        String type = getIntent().getStringExtra("type");

        ImageView imgData = findViewById(R.id.img_data);
        if(type.equals("well"))
            imgData.setImageResource(R.drawable.well_color);
        else if(type.equals("reservoir"))
            imgData.setImageResource(R.drawable.reservoir_res_ico_clr);
        else if(type.equals("soil"))
            imgData.setImageResource(R.drawable.grass_res_ico_clr);

        TextView txtData = findViewById(R.id.txt_data);
        txtData.setText(getIntent().getStringExtra("data"));
    }
}
