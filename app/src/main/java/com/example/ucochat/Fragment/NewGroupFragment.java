package com.example.ucochat.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ucochat.Main.MainActivityAdm;
import com.example.ucochat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText etname, etpassword;
    private Button button;
    private SharedPreferences preferences;
    private View view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;


    private OnFragmentInteractionListener mListener;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewGroupFragment newInstance(String param1, String param2) {
        NewGroupFragment fragment = new NewGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_new_group, container, false);

        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        etname = (EditText) view.findViewById(R.id.nameGroupF);
        etpassword = (EditText) view.findViewById(R.id.passwordGroupF);
        button = (Button) view.findViewById(R.id.botonGroup);
        auth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = preferences.getString("nombre", "");
                final String name = etname.getText().toString();
                final String password = etpassword.getText().toString();
                final String id = name + "_" + user;

                assert user != null;
                if (user.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Rellena los campos", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference docRef = db.collection("Grupos").document(id);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(getContext(), "Ya existe un grupo creado con ese nombre", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("password", password);
                                    map.put("nombre", name);
                                    db.collection("Grupos").document(id).set(map);

                                    Map<String, String> map2 = new HashMap<>();
                                    map2.put("Status", "Admin");
                                    map2.put("nombre", auth.getCurrentUser().getEmail());
                                    db.collection("Grupos").document(id).collection("Usuarios").document(auth.getCurrentUser().getUid()).set(map2);
                                    db.collection("Usuarios").document(auth.getCurrentUser().getUid()).collection("Grupos").document(id).set(map2);

                                    goToMainAdm();
                                }
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void goToMainAdm (){
        Intent i = new Intent(getContext(), MainActivityAdm.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Evita volver a la pantalla de login
        startActivity(i);
    }
}
