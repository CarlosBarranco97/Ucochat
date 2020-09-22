package com.example.ucochat.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ucochat.Fragment.NewGroupFragment;
import com.example.ucochat.Fragment.SearchGroupFragment;
import com.example.ucochat.Fragment.MainFragment;
import com.example.ucochat.Login.LoginActivity;
import com.example.ucochat.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityAdm extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, NewGroupFragment.OnFragmentInteractionListener, SearchGroupFragment.OnFragmentInteractionListener{

    SharedPreferences preferences;
    NewGroupFragment fragmentNewGroup;
    MainFragment fragmentMain;
    SearchGroupFragment fragmentSearchGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_adm);

        preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        fragmentMain = new MainFragment();
        fragmentSearchGroup = new SearchGroupFragment();
        fragmentNewGroup = new NewGroupFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.containerAdm,fragmentMain).commit();

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu_adm) {
        getMenuInflater().inflate(R.menu.menu_adm, menu_adm);
        return super.onCreateOptionsMenu(menu_adm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.menu_logout:
                LogOut();
                removePreferences();
                return true;
            case R.id.create_group:
                fragmentTransaction.replace(R.id.containerAdm, fragmentNewGroup).commit();
                return true;
            case R.id.menu_start:
                fragmentTransaction.replace(R.id.containerAdm,fragmentMain).commit();
                return true;
            case R.id.find_group:
                fragmentTransaction.replace(R.id.containerAdm, fragmentSearchGroup).commit();
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

}
