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
import android.widget.Toast;

import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ForgotPasswordFragment extends Fragment {

    private FirebaseServices fbs;
    private EditText etMailForgotP;
    private Button btForgotP;
    private ImageButton tvBackToLoginForgorPasswordFragment;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotPasswordFragment newInstance(String param1, String param2) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
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
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onStart(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {return;}
        super.onStart();
        fbs = FirebaseServices.getInstance();
        etMailForgotP = getView().findViewById(R.id.etMailForgotP);
        btForgotP = getView().findViewById(R.id.btForgotP);
        btForgotP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbs.getAuth().sendPasswordResetEmail(etMailForgotP.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Check your email in google messages.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Failed, check the email address!.2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        tvBackToLoginForgorPasswordFragment = getView().findViewById(R.id.tvBackToLoginForgorPasswordFragment);
        tvBackToLoginForgorPasswordFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoLogInFragment();
            }
        });
    }
}