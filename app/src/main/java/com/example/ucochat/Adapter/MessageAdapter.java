package com.example.ucochat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ucochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    Context context;

    List<Message> messageList;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 1){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.own_message, viewGroup, false);
            return new MessageHolder(view);

        }else if (i == 2){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.own_image, viewGroup, false);
            return new MessageHolder(view);
        } else if (i == 3) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_message, viewGroup, false);
            return new MessageHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_image, viewGroup, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder messageHolder, final int i) {

        final Message message = messageList.get(i);

        DocumentReference docRef = firestore.collection("Usuarios").document(message.getTransmitter());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    messageHolder.user.setText(document.get("Nombre").toString());

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                    Date date = new Date(message.getTime().longValue());

                    messageHolder.date.setText(formatter.format(date));

                    //--------------------------------Incluir date------------------
                }

            }
        });

        if (message.getType().equals("texto")) {
            messageHolder.message.setText(message.getMessage());

        }else if (message.getType().equals("imagen")){
            messageHolder.image.setVisibility(View.VISIBLE);
            //messageHolder.message.setVisibility(View.VISIBLE);
            Picasso.get().load(message.getMessage()).into(messageHolder.image);
            messageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageList.get(i).getMessage()));
                    messageHolder.itemView.getContext().startActivity(intent);
                }
            });
        }
        else {
            messageHolder.image.setVisibility(View.VISIBLE);
            //messageHolder.message.setVisibility(View.VISIBLE);
            messageHolder.image.setBackgroundResource(R.drawable.pdf);
            messageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageList.get(i).getMessage()));
                    messageHolder.itemView.getContext().startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        public TextView message, user, date;
        public ImageView image;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            message = (TextView)itemView.findViewById(R.id.message);
            user = (TextView)itemView.findViewById(R.id.memberGroup);
            image = (ImageView)itemView.findViewById(R.id.photo);
            date = (TextView)itemView.findViewById(R.id.messageDate);
        }
    }
    @Override
    public int getItemViewType (int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (messageList.get(position).getTransmitter().equals(firebaseUser.getUid())){
            if (messageList.get(position).getType().equals("texto")){
                return 1;  //Texto enviado por este user
            }else {
                return 2;   //Documento enviado por el user
            }
        }
        else {
            if (messageList.get(position).getType().equals("texto")){
                return 3;   //Texto recibido
            }else {
                return 4;   //Documento recibido
            }
        }
    }
}
