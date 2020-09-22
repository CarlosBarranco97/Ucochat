package com.example.ucochat.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ucochat.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsuarioHolder> implements View.OnClickListener{

    List<String> usersList;

    private View.OnClickListener listener;

    public UserAdapter(List<String> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserAdapter.UsuarioHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user,viewGroup,false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams); //PERMITIR QUE SE MUESTRE UNO GRUPO TRAS OTRO

        view.setOnClickListener(listener);

        return new UsuarioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UsuarioHolder usuarioHolder, int i) {

        usuarioHolder.user.setText(usersList.get(i));

    }

    public void setOnClickListener (View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {

        return usersList.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null ){
            listener.onClick(v);
        }
    }


    public class UsuarioHolder extends RecyclerView.ViewHolder {

        TextView user;

        public UsuarioHolder(@NonNull View itemView) {
            super(itemView);

            user =(TextView)itemView.findViewById(R.id.userGroup);

        }
    }
}
