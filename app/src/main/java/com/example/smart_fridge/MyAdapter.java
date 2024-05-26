package com.example.smart_fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import model.Tache;

import java.util.LinkedList;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LinkedList<Tache> taches;
    private Context context;

    public MyAdapter(LinkedList<Tache> taches, Context context) {
        this.taches = taches;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_grocery, parent, false);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Tache currentTache = taches.get(position);
        String title = currentTache.getTitle();

        // Bind data to ViewHolder
        holder.title.setText(title);
    }

    @Override
    public int getItemCount() {
        return taches.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox title;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = itemLayoutView.findViewById(R.id.checkBox); // Assuming there's a TextView with id 'titleTextView'
        }
    }
}
