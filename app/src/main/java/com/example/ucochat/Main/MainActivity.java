package com.example.ucochat.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ucochat.Fragment.SearchGroupFragment;
import com.example.ucochat.Fragment.MainFragment;
import com.example.ucochat.Login.LoginActivity;
import com.example.ucochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, SearchGroupFragment.OnFragmentInteractionListener{

    SharedPreferences preferences;
    MainFragment fragmentMain;
    SearchGroupFragment fragmentSearchGroup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart () {
        super.onStart();

        DocumentReference docRef = db.collection("Usuarios").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if (document.get("Profesor").equals("Si")){
                            SharedPreferences.Editor editor= preferences.edit();
                            editor.putString("profesor", "Si");
                            editor.commit();    //Para el codigo hasta que se guarde las variables
                            editor.apply();
                            goToMainAdm();
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"No existe el doc2",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        fragmentMain = new MainFragment();
        fragmentSearchGroup = new SearchGroupFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.containerUsers,fragmentMain).commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_logout:
                LogOut();
                removePreferences();
                return true;
            case R.id.menu_start:
                fragmentTransaction.replace(R.id.containerUsers,fragmentMain).commit();
                return true;
            case R.id.find_group:
                fragmentTransaction.replace(R.id.containerUsers, fragmentSearchGroup).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void removePreferences() {
        preferences.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
    }
    private void LogOut() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void goToMainAdm (){
        Intent i = new Intent(this, MainActivityAdm.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }


}
