package com.example.ucochat.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ucochat.Adapter.Message;
import com.example.ucochat.Adapter.MessageAdapter;
import com.example.ucochat.Main.MainActivity;
import com.example.ucochat.Main.MainActivityAdm;
import com.example.ucochat.Notifications.APIService;
import com.example.ucochat.Notifications.Client;
import com.example.ucochat.Notifications.Data;
import com.example.ucochat.Notifications.MyResponse;
import com.example.ucochat.Notifications.Sender;
import com.example.ucochat.Notifications.Token;
import com.example.ucochat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private TextView etGroupName;
    private FirebaseAuth auth;
    private String groupName;
    private SharedPreferences preferences;
    private FirebaseFirestore firestore;
    private ImageButton send, sendAdj, backMenu;
    private EditText message;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private final int PICK_IMAGE_REQUEST = 71;

    private RecyclerView recyclerView;

    private APIService apiService;

    boolean notify =false;

    StorageReference reference;

    private String checker="", myURL="";
    private StorageTask uploadTask;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        etGroupName = (TextView)findViewById(R.id.nombreChat);
        groupName = preferences.getString("groupName","");
        etGroupName.setText(groupName);
        send = (ImageButton)findViewById(R.id.btn_envio);
        message = (EditText)findViewById(R.id.mensajeEnviar);
        sendAdj = (ImageButton)findViewById(R.id.btn_envioDoc);
        backMenu = (ImageButton)findViewById(R.id.volverMenu);

        reference = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView)findViewById(R.id.contenedorChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //--------------------------ENVIO DE NOTIFAICION-------------------------------------
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = message.getText().toString();
                if (!msg.equals("")){
                    sendMessage(msg,auth.getCurrentUser().getUid(), groupName);
                } else {
                    Toast.makeText(getApplicationContext(), "Escribe un message",Toast.LENGTH_SHORT).show();
                }
                message.setText("");

                readMessages();
            }
        });

        //--------------------------ACCEDER A LA LISTA DE USUARIOS---------------------------------
        etGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Grupos").document(groupName).collection("Usuarios").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.get("Status").equals("Admin")){
                                Intent i = new Intent(ChatActivity.this, GroupManagementActivity.class);
                                i.putExtra("groupName", groupName);
                                startActivity(i);

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"No eres el creador del grupo",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        //--------------------------ELECCION DE FICHERO A ENVIAR-------------------------------------
        sendAdj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Imagenes",
                        "PDF"
                };
                AlertDialog.Builder builder =  new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Selecciona el tipo de documento: ");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ( which == 0){
                            checker = "imagen";

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent.createChooser(intent,"Imagen Seleccionada"),PICK_IMAGE_REQUEST);
                        }
                        if (which == 1){
                            checker = "PDF";
                            Intent intent = new Intent();
                            intent.setType("application/pdf");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent.createChooser(intent,"PDF Seleccionado"),PICK_IMAGE_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });
        updateChat();

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getString("profesor", "").equals("Si")){
                    goToMainAdm();
                }
                else {
                    goToMain();
                }
            }
        });

    }

    private void updateChat() {

        firestore.collection("Grupos").document(groupName).collection("Mensajes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w("Actualizacion Firestore", "listen:error",e);
                    return;
                }
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                    switch (dc.getType()){
                        case ADDED:
                            readMessages();
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });
    }


    //----------------------------ENVIO DE MENSAJES DE TEXTO-------------------------
    private void sendMessage (final String msg, String emisor, String grupo) {

        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("Message", msg);
        hashMap.put("Emisor", emisor);
        Long tsLong = System.currentTimeMillis();
        hashMap.put("Time", tsLong);
        hashMap.put("Tipo", "texto");

        firestore.collection("Grupos").document(grupo).collection("Mensajes").document().set(hashMap);

        final String msg2 = msg;

        firestore.collection("Usuarios").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if (notify) {
                            sendNotification(groupName, document.get("Nombre").toString(), msg2);
                        }
                        notify = false;
                    }
                }
            }
        });

    }

    //----------------ENVIO DE DOCUMENTOS----------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            fileUri = data.getData();
            if (fileUri != null) {
                if (checker.equals("imagen")){
                    final StorageReference storageReference = reference.child("images/" + UUID.randomUUID().toString());
                    uploadTask = storageReference.putFile(fileUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            return storageReference.getDownloadUrl();
                    }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURL = task.getResult();
                                myURL = downloadURL.toString();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Message", myURL);
                                hashMap.put("Emisor", auth.getCurrentUser().getUid());
                                Long tsLong = System.currentTimeMillis();
                                hashMap.put("Time", tsLong);
                                hashMap.put("Tipo", checker);

                                firestore.collection("Grupos").document(groupName).collection("Mensajes").document().set(hashMap);

                                sendNotification(groupName, preferences.getString("nombre", ""), "Ha enviado una foto");

                            }
                        }
                    });
                }
                else {
                    final StorageReference storageReference = reference.child("PDF/" + UUID.randomUUID().toString());
                    uploadTask = storageReference.putFile(fileUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadURL = task.getResult();
                                myURL = downloadURL.toString();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Message", myURL);
                                hashMap.put("Emisor", auth.getCurrentUser().getUid());
                                Long tsLong = System.currentTimeMillis();
                                hashMap.put("Time", tsLong);
                                hashMap.put("Tipo", checker);

                                firestore.collection("Grupos").document(groupName).collection("Mensajes").document().set(hashMap);

                                sendNotification(groupName, preferences.getString("nombre", ""), "Ha enviado un PDF");

                            }
                        }
                    });
                }
            }
        }
    }

    private void sendNotification(final String grupo, final String usuario, final String msg){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Grupos").document(grupo).collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()){
                     ArrayList<String> usuarios = new ArrayList<>();
                     for (QueryDocumentSnapshot document : task.getResult()) {
                         if (!auth.getCurrentUser().getUid().equals(document.getId()) || !document.get("Status").equals("Elim")){
                             usuarios.add(document.getId());
                         }
                     }
                 }
            }
        });

        db.collection("Tokens").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if (!documentSnapshot.getId().equals(auth.getCurrentUser().getUid())){

                            Data data = new Data(auth.getCurrentUser().getUid(), usuario+": "+msg, grupo, documentSnapshot.getId(),R.drawable.ic_new_mesagge);
                            Sender sender = new Sender(data,documentSnapshot.get("token").toString());

                            apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void readMessages() {
        messages = new ArrayList<>();

        firestore.collection("Grupos").document(groupName).collection("Mensajes").orderBy("Time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    messages.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Message mesagge = new Message(document.get("Emisor").toString(),document.get("Message").toString(),(long) document.get("Time"), document.get("Tipo").toString());
                        messages.add(mesagge);

                        messageAdapter = new MessageAdapter(messages);
                        recyclerView.setAdapter(messageAdapter);

                    }
                }
            }
        });
    }

    private void updateToken (String token){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Token token1 = new Token(token);

        firestore.collection("Tokens").document(auth.getCurrentUser().getUid()).set(token1, SetOptions.merge());
    }
    private void goToMain (){
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }

    private void goToMainAdm (){
        Intent i = new Intent(this, MainActivityAdm.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }

}
