package com.example.ucochat.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ucochat.Main.MainActivity;
import com.example.ucochat.R;
import com.example.ucochat.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText name, password;
    private Button Login, register;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText)findViewById(R.id.nombreLogin);
        password = (EditText)findViewById(R.id.passwordLogin);
        Login = (Button)findViewById(R.id.buttonLogin);
        register = (Button)findViewById(R.id.buttonRegistro);
        preferences = getSharedPreferences("data", Context.MODE_PRIVATE);


        mAuth = FirebaseAuth.getInstance();

        Login.setOnClickListener(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    private void goToMain (){
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }


    @Override
    public void onClick(View v) {

        mAuth.signInWithEmailAndPassword(name.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_LONG).show();
                    SavePreferences(name.getText().toString(),password.getText().toString(),"NS");

                    goToMain();

                }
                else {
                    Toast.makeText(getApplicationContext(),"No existe el user",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SavePreferences (String nombre, String password, String profesor) {
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("name", nombre);
        editor.putString("password", password);
        editor.putString("profesor", profesor);
        editor.commit();    //Para el codigo hasta que se guarde las variables
        editor.apply();
    }
}
