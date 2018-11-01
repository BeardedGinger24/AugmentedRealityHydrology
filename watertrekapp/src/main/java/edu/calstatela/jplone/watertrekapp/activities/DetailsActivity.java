package edu.calstatela.jplone.watertrekapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.calstatela.jplone.watertrekapp.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String type = getIntent().getStringExtra("type");

        ImageView imgData = findViewById(R.id.img_data);

        switch (type) {
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

        Button yaButton = findViewById(R.id.cameraYa);
        yaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Toast.makeText(DetailsActivity.this, "Rolling Ya Now", Toast.LENGTH_LONG).show();
            }
        });

    }
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
        Intent intent = new Intent(currentActivity, DetailsActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", data);
        currentActivity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
