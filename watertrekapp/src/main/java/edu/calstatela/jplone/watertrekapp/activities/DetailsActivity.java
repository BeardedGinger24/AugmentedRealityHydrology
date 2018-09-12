package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import edu.calstatela.jplone.watertrekapp.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String type = getIntent().getStringExtra("type");

        ImageView imgData = findViewById(R.id.img_data);
        switch(type){
            case "well":
                imgData.setImageResource(R.drawable.well_res_ico_clr);
                break;
            case "mountain":
                imgData.setImageResource(R.drawable.mtn_res_ico_clr);
                break;
            case "reservoir":
                imgData.setImageResource(R.drawable.reservoir_res_ico_clr);
                break;
            case "river":
                imgData.setImageResource(R.drawable.river_res_ico_clr);
                break;
            case "soil":
                imgData.setImageResource(R.drawable.grass_res_ico_clr);
                break;
        }

        TextView txtData = findViewById(R.id.txt_data);
        txtData.setText(getIntent().getStringExtra("data"));
    }

    public static void launchDetailsActivity(Activity currentActivity, String type, String data){
        Intent intent = new Intent(currentActivity, DetailsActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", data);
        currentActivity.startActivity(intent);
    }
}
