package edu.calstatela.jplone.watertrekapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.calstatela.jplone.watertrekapp.R;

public class Pitch_RecyclerViewAdapter extends RecyclerView.Adapter<Pitch_RecyclerViewAdapter.ViewHolder>{


    private ArrayList<String> horizontalTicks = new ArrayList<>();

    public Pitch_RecyclerViewAdapter(ArrayList<String> horizontalTicks, Context context) {
        this.horizontalTicks = horizontalTicks;

        //Added 8 more ticks for positioning in the center of the recycler view
        for(int i=0; i< 365; i++){
            this.horizontalTicks.add("");
        }

    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_ticks, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Pitch_RecyclerViewAdapter.ViewHolder holder, int position) {

        TextView listItem = holder.itemView.findViewById(R.id.hTick);
        int tickColor = android.graphics.Color.rgb(255,255,255);
        int pos = holder.getAdapterPosition();

        //for changing margins
        RelativeLayout.LayoutParams parameter =  (RelativeLayout.LayoutParams) listItem.getLayoutParams();

        //For every 10th value, the value is shown instead of a tick
        if(pos %10 == 0){
            setLayoutText(listItem, parameter, pos);
        }


        //Resetting layout properties for ticks
        else{
            listItem.getLayoutParams().width = 75;
            listItem.getLayoutParams().height = 5;
            listItem.setPadding(0,0,0,0);
            parameter.setMargins(13, 50, 0, 5); // left, top, right, bottom
            listItem.setLayoutParams(parameter);
            listItem.setTextSize(0);
            listItem.setTypeface(null, Typeface.BOLD);
            listItem.setText("");
            listItem.setBackgroundColor(tickColor);
        }



    }



    @Override
    public int getItemCount() {
        return horizontalTicks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.hTick);
            parentLayout = itemView.findViewById(R.id.parent_layout_H);
        }
    }

    //Setting layout for number/string values
    public void setLayoutText(TextView listItem, RelativeLayout.LayoutParams parameter, int pos){
        int textColor = android.graphics.Color.rgb(255, 255, 255);

        listItem.getLayoutParams().width = 90;
        listItem.getLayoutParams().height = 75;
        listItem.setPadding(0,0,0,0);
//        if(pos < 100){
//            listItem.setPadding(10,0,0,0);
//        }
        parameter.setMargins(10, 25

                , 0, 0); // left, top, right, bottom
        //1: center_horizontal   , 10: center_vertical
//        listItem.setGravity(1);
        listItem.setGravity(3);
        listItem.setLayoutParams(parameter);
        listItem.setTextSize(13);
        listItem.setTextColor(Color.WHITE);
        listItem.setBackgroundColor(Color.TRANSPARENT);
        listItem.setText(Integer.toString((pos - 270)));
    }
}
