package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
<<<<<<< HEAD
=======
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
>>>>>>> 9402117896e1c8079f2e7c9d022485382c7d2224
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
<<<<<<< HEAD
        switch(type){
=======

        switch (type) {
>>>>>>> 9402117896e1c8079f2e7c9d022485382c7d2224
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
<<<<<<< HEAD

    public static void launchDetailsActivity(Activity currentActivity, String type, String data){
=======
    //-----Added bu fugi that will display different data from watertrek-----
    //Menu item with items (Map,History, etc...)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map) {
            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.somethingelse) {
            //change Activityclass to future activity
            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launchDetailsActivity(Activity currentActivity, String type, String data) {
>>>>>>> 9402117896e1c8079f2e7c9d022485382c7d2224
        Intent intent = new Intent(currentActivity, DetailsActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", data);
        currentActivity.startActivity(intent);
    }
<<<<<<< HEAD
=======

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
>>>>>>> 9402117896e1c8079f2e7c9d022485382c7d2224
}
