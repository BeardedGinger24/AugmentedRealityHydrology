package edu.calstatela.jplone.watertrekapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import edu.calstatela.jplone.watertrekapp.R;

public class Azimuth_RecyclerViewAdapter extends RecyclerView.Adapter<Azimuth_RecyclerViewAdapter.ViewHolder>{


    private ArrayList<String> verticalTicks = new ArrayList<>();

    public Azimuth_RecyclerViewAdapter(ArrayList<String> ticks, Context context) {
        this.verticalTicks = verticalTicks;

        for(int i=0; i< 12; i++){
            this.verticalTicks.add("");
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_ticks, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d("onBindViewHolder", "onBindViewHolder: called");

    }

    @Override
    public int getItemCount() {
        return verticalTicks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.tick);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
