package com.example.ucochat.Adapter;

import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ucochat.R;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GruposHolder> implements View.OnClickListener{

    List<String> groupList;

    private View.OnClickListener listener;

    public GroupsAdapter(List<String> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GruposHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bd,viewGroup,false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams); //PERMITIR QUE SE MUESTRE UNO GRUPO TRAS OTRO

        view.setOnClickListener(listener);

        return new GruposHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GruposHolder gruposHolder, int i) {
        gruposHolder.name.setText(groupList.get(i));
    }

    @Override
    public int getItemCount() {
        return groupList.size();  //Numero de data que mostrar pantalla
    }

    public void setOnClickListener (View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    public class GruposHolder extends RecyclerView.ViewHolder {

        TextView name;
        public GruposHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.idGroup);
        }
    }
}
