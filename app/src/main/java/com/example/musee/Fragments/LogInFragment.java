package com.example.musee.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

public class LogInFragment extends Fragment {
    private EditText etUserName, etPassword;
    private TextView tvForgotPasswordLogIn,tvSignUpLink;
    private Button btLogIn;
    private ImageButton btnBackToAllFromLoginFragment;

    private FirebaseServices fbs;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
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
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get the MainActivity once to call its public navigation methods
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) { // Also check if the view is null
            return; // Exit if the activity or view is not available
        }

        // connecting companions
        // R is class that have all the valls
        fbs = FirebaseServices.getInstance();
        etUserName = getView().findViewById(R.id.etUserNameLogIn);
        etPassword = getView().findViewById(R.id.etPasswordLogIn);
        tvSignUpLink = getView().findViewById(R.id.tvSignUpLink);
        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoSignUpFragment();//<-------------------------------------------------
            }
        });
        tvForgotPasswordLogIn = getView().findViewById(R.id.tvForgotPasswordLogin);
        tvForgotPasswordLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoForgotPasswordFragment();
            }
        });
        btLogIn = getActivity().findViewById(R.id.btLogInLogIn);
        btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Data chick
                // trim() cut the space
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (username.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "some fields are empty", Toast.LENGTH_LONG).show();
                    return;
                }
                // LodIN
                fbs.getAuth().signInWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {mainActivity.gotoAllPiecesFragment();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "something went wrong shick again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnBackToAllFromLoginFragment = getView().findViewById(R.id.btnBackToAllFromLoginFragment);
        btnBackToAllFromLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoAllPiecesFragment();
            }
        });
    }

}