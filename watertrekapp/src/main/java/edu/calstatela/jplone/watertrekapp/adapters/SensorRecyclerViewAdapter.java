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

public class SensorRecyclerViewAdapter extends RecyclerView.Adapter<SensorRecyclerViewAdapter.ViewHolder>{


    private ArrayList<String> ticks = new ArrayList<>();

    public SensorRecyclerViewAdapter(ArrayList<String> ticks, Context context) {
        this.ticks = ticks;

        for(int i=0; i< 180; i++){
            this.ticks.add("");
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ticks, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.d("onBindViewHolder", "onBindViewHolder: called");

    }

    @Override
    public int getItemCount() {
        return ticks.size();
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
