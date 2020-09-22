package com.example.ucochat.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ucochat.Adapter.UserAdapter;
import com.example.ucochat.Main.MainActivityAdm;
import com.example.ucochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupManagementActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    Bundle data;
    private ArrayList<String> usersList;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ImageButton button;
    boolean clear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_group);

        data = getIntent().getExtras();
        final String nombre = data.getString("nombreGrupo");
        button = (ImageButton)findViewById(R.id.id_deleteGroup);
        usersList = new ArrayList<String>();
        recyclerView = findViewById(R.id.containerUserGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        firestore.collection("Grupos").document(nombre).collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        if (!user.getUid().equals(documentSnapshot.getId()) && !documentSnapshot.get("Status").toString().equals("Elim")){
                            usersList.add(documentSnapshot.get("nombre").toString());
                        }
                    }
                    UserAdapter adapter = new UserAdapter(usersList);

                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Toast.makeText(GroupManagementActivity.this, "....",Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder alert = new AlertDialog.Builder(GroupManagementActivity.this);
                            alert.setMessage("¿Desea eliminar el user seleccionado?").setCancelable(true).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firestore.collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for (QueryDocumentSnapshot doc : task.getResult()){
                                                    if(doc.exists()) {
                                                        if (doc.get("Nombre").toString().equals(usersList.get(recyclerView.getChildAdapterPosition(v)))) {
                                                            firestore.collection("Usuarios").document(doc.getId()).collection("Grupos").document(nombre).delete();
                                                            Map<String, String> datos = new HashMap<>();
                                                            datos.put("Status", "Elim");
                                                            firestore.collection("Grupos").document(nombre).collection("Usuarios").document(doc.getId()).set(datos);
                                                        }
                                                    }else{
                                                        Toast.makeText(getApplicationContext(),"NOOO",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                                    });


                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog titulo = alert.create();
                            titulo.setTitle("Eliminar Usuario");
                            titulo.show();
                        }
                    });

                    recyclerView.setAdapter(adapter);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GroupManagementActivity.this);
                alert.setMessage("¿Desea eliminar el grupo?").setCancelable(true).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    final ArrayList<String> listaUID = new ArrayList<String>();

                    firestore.collection("Grupos").document(nombre).collection("Usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (DocumentSnapshot doc : task.getResult()){
                                    firestore.collection("Usuarios").document(doc.getId()).collection("Grupos").document(nombre).delete();
                                    firestore.collection("Grupos").document(nombre).collection("Usuarios").document(doc.getId()).delete();

                                }
                            }

                       }
                   });
                    firestore.collection("Grupos").document(nombre).collection("Mensajes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (DocumentSnapshot doc : task.getResult()){
                                    firestore.collection("Grupos").document(nombre).collection("Mensajes").document(doc.getId()).delete();
                                    clear= true;
                                }
                                firestore.collection("Grupos").document(nombre).delete();

                                if(clear){
                                    Intent i = new Intent(GroupManagementActivity.this, MainActivityAdm.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                    });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog titulo = alert.create();
                titulo.setTitle("Eliminar Grupo");
                titulo.show();
            }
        });
    }
}
