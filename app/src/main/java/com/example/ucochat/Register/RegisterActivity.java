package com.example.ucochat.Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.example.ucochat.Main.MainActivity;
import com.example.ucochat.Main.MainActivityAdm;
import com.example.ucochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText name, etpassword,passwordProf;
    private Button button;
    private Switch aSwitch;
    private FirebaseAuth mAuth;
    private String key;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        name = (EditText)findViewById(R.id.registerName);
        etpassword = (EditText)findViewById(R.id.passwordRegister);
        button = (Button)findViewById(R.id.buttonRegist);
        aSwitch = (Switch)findViewById(R.id.switchProfessor);
        passwordProf = (EditText)findViewById(R.id.passwordProf);



        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final String passwordP = passwordProf.getText().toString();
        final String name = this.name.getText().toString();
        final String password = etpassword.getText().toString();

        if (password.isEmpty() || name.isEmpty()){
            Toast.makeText(this, "Rellena los campos",Toast.LENGTH_SHORT).show();
        }
        else {
            if (aSwitch.isChecked()) {
                DocumentReference docRef = db.collection("Usuarios").document("Profesor");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            key = document.get("Password").toString();
                        }
                    }
                });
                if (passwordP.equals(key)) {
                    mAuth.createUserWithEmailAndPassword(name, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Map<String, String> datos = new HashMap<>();
                                datos.put("Profesor", "Si");
                                datos.put("Nombre", name);

                                db.collection("Usuarios").document(mAuth.getCurrentUser().getUid()).set(datos);
                                SavePreferences(name, password, "Si");
                                goToMainAdm();
                            } else {
                                Toast.makeText(getApplicationContext(), "Parametros incorrectos", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Clave incorrecta", Toast.LENGTH_SHORT).show();
                }
            } else {
                mAuth.createUserWithEmailAndPassword(name, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, String> datos = new HashMap<>();
                            datos.put("Profesor", "No");
                            datos.put("Nombre", name);
                            db.collection("Usuarios").document(mAuth.getCurrentUser().getUid()).set(datos);
                            SavePreferences(name, password.toString(), "No");
                            goToMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "Parametros incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
    private void goToMain (){
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }

    private void SavePreferences (String nombre, String password, String profesor) {
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("name", nombre);
        editor.putString("password", password);
        editor.putString("profesor", profesor);
        editor.commit();    //Para el codigo hasta que se guarde las variables
        editor.apply();
    }
    private void goToMainAdm (){
        Intent i = new Intent(this, MainActivityAdm.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }
}
