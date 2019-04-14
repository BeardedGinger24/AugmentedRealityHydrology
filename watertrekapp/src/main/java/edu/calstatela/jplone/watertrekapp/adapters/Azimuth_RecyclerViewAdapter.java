package edu.calstatela.jplone.watertrekapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import edu.calstatela.jplone.watertrekapp.R;

public class Azimuth_RecyclerViewAdapter extends RecyclerView.Adapter<Azimuth_RecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);


            textView = itemView.findViewById(R.id.vTick);

            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }


    private ArrayList<String> recyclerObjects = new ArrayList<>();


    public Azimuth_RecyclerViewAdapter(ArrayList<String> ticks, Context context) {
        this.recyclerObjects = ticks;

        //originally 400
        //adding 4 units to each side so the center marker matches up with the number displayed

        for(int i=0; i< 360; i++){
            this.recyclerObjects.add("");
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_ticks, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Azimuth_RecyclerViewAdapter.ViewHolder holder, int position) {

        TextView listItem = holder.itemView.findViewById(R.id.vTick);

        Log.d("viewPosition", "onBindViewHolder: " + (holder.getAdapterPosition()));

        int tickColor = android.graphics.Color.rgb(255,255,255);

        int pos = holder.getAdapterPosition();

        //for changing margins
        RelativeLayout.LayoutParams parameter =  (RelativeLayout.LayoutParams) listItem.getLayoutParams();

        //For every 10th value, the value is shown instead of a tick
        if(pos %10 == 0){
            setLayoutText(listItem, parameter);
            listItem.setText(Integer.toString(pos));
        }

        //Resetting layout properties for tick layout
        else{
            listItem.getLayoutParams().width = 5;
            listItem.getLayoutParams().height = 50;
            listItem.setPadding(0,0,0,0);
            parameter.setMargins(40, parameter.topMargin, 14, 5); // left, top, right, bottom
            listItem.setLayoutParams(parameter);
            listItem.setTextSize(0);
            listItem.setText("");
            listItem.setBackgroundColor(tickColor);
        }

        setDirectionTxt(pos, listItem, parameter);


    }

    //Setting layout for number/string values
    public void setLayoutText(TextView txtView, RelativeLayout.LayoutParams parameter){
        int textColor = android.graphics.Color.rgb(255, 255, 255);

        txtView.getLayoutParams().width = 125;
        txtView.getLayoutParams().height = 50;
        txtView.setPadding(28,0,20,0);
        parameter.setMargins(40, parameter.topMargin, 0, 5); // left, top, right, bottom
        txtView.setGravity(1);
        txtView.setGravity(10);
        txtView.setLayoutParams(parameter);
        txtView.setTextSize(15);
        txtView.setTextColor(Color.WHITE);
        txtView.setBackgroundColor(Color.TRANSPARENT);
//        txtView.setText(Integer.toString(pos));
    }

    public void setDirectionTxt(int pos, TextView listItem, RelativeLayout.LayoutParams parameter){
        switch(pos){
            case 0:
                listItem.setText("N");
                break;
            case 45:
                listItem.setText("NE");
                setLayoutText(listItem, parameter);
                break;
            case 90:
                listItem.setText("E");
                break;
            case 135:
                listItem.setText("SE");
                setLayoutText(listItem, parameter);
                break;
            case 180:
                listItem.setText("S");
                break;
            case 225:
                listItem.setText("SW");
                setLayoutText(listItem, parameter);
                break;
            case 270:
                listItem.setText("W");
                break;
            case 315:
                listItem.setText("NW");
                setLayoutText(listItem, parameter);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recyclerObjects.size();
    }





}
