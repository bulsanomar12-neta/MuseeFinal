package com.example.musee.Data;

import android.Manifest;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musee.Fragments.AllPiecesFragment;
import com.example.musee.Fragments.LogInFragment;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.example.musee.classes.User;
import com.example.musee.classes.UtilsClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 134;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private EditText etEmailSignUp, etPasswordSignUp, etConfirmPasswordSignUp, etPhoneNumSignUp, etFirstNameSignUp, etLastNameSignUp, etUserNameSignUp, etAddressSignUp;
    private ImageView imgUserSignUp;
    private Button btSignUp;
    private ImageButton btnBackFromSignUpToAll;
    private FirebaseServices fbs;
    private UtilsClass util;
    private Uri selectedImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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

        if (savedInstanceState != null) {
            selectedImage = savedInstanceState.getParcelable("selectedImage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // connecting companions
        // R is class that have all the valls
        fbs = FirebaseServices.getInstance();
        etUserNameSignUp = getView().findViewById(R.id.etRealUserNameSignUp);
        etPasswordSignUp = getView().findViewById(R.id.etPasswordSignUp);
        etConfirmPasswordSignUp = getView().findViewById(R.id.etConfirmPasswordSignUp);
        etPhoneNumSignUp = getView().findViewById(R.id.etPhoneNumSignUp);
        etFirstNameSignUp = getView().findViewById(R.id.etFirstNameSignUp);
        etLastNameSignUp = getView().findViewById(R.id.etLastNameSignUp);
        etEmailSignUp = getView().findViewById(R.id.etEmailSignUp);
        etAddressSignUp = getView().findViewById(R.id.etAddressSignUp);

        imgUserSignUp = getView().findViewById(R.id.imgUserSignUp);
        /*
        if (selectedImage != null) {
            imgUserSignUp.setImageURI(selectedImage);
            fbs.setSelectedImageURL(selectedImage);
        }

         */

        imgUserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        util = UtilsClass.getInstance();
        btSignUp = getView().findViewById(R.id.btSignUpSignup);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Data chick
                // trim() cut the space
                String userName = etUserNameSignUp.getText().toString().trim();
                String password = etPasswordSignUp.getText().toString().trim();
                String confirmPassword = etConfirmPasswordSignUp.getText().toString().trim();
                String phoneNum = etPhoneNumSignUp.getText().toString().trim();
                String firstName = etFirstNameSignUp.getText().toString().trim();
                String lastName = etLastNameSignUp.getText().toString().trim();
                String email = etEmailSignUp.getText().toString().trim();
                String address = etAddressSignUp.getText().toString().trim();
                if (userName.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty() || phoneNum.trim().isEmpty() ||
                        firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty() || address.trim().isEmpty()){
                    Toast.makeText(getActivity(), "some fields are empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    util.showMessageDialog(getActivity(), "Please enter a valid email address");
                    return;
                }
                if (firstName.length() < 3) {
                    util.showMessageDialog(getActivity(),
                            "FirstName must be at least 3 letters");
                    return;
                }
                if (lastName.length() < 4) {
                    util.showMessageDialog(getActivity(),
                            "LastName must be at least 4 letters");
                    return;
                }
                if (password.length() < 6) {
                    util.showMessageDialog(getActivity(),
                            "Password must be at least 6 characters");
                    return;
                }
                if (!password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*")) {
                    util.showMessageDialog(getActivity(),
                            "Password must contain at least one number and one capital letter");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    util.showMessageDialog(getActivity(), "Password are not identical!");
                    return;
                }
                if (!phoneNum.matches("\\d{10}")) {
                    util.showMessageDialog(getActivity(), "Phone number must be 10 digits");
                    return;
                }
                // SignUp
                Uri selectedImageUri = fbs.getSelectedImageURL();
                String imageURL = "";
                imgUserSignUp = getView().findViewById(R.id.imgUserSignUp);

                if (selectedImage != null) {
                    imgUserSignUp.setImageURI(selectedImage);
                }

                // فحص اذا اسم المستخدم موجود بالفعل
                fbs.getFire().collection("users")
                        .whereEqualTo("userName", userName)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                // الاسم مستخدم بالفعل
                                util.showMessageDialog(getActivity(), "Username is already taken");
                                return;
                            }
                            // Create a new user
                            fbs.getAuth().createUserWithEmailAndPassword(email , password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // إنشاء كائن المستخدم
                                            User user = new User(firstName, lastName, userName, phoneNum, address, email);
                                            //  إضافة الصورة إذا اختارها المستخدم
                                            String imageURL = "";
                                            if (fbs.getSelectedImageURL() != null) {
                                                imageURL = fbs.getSelectedImageURL().toString();
                                            }
                                            user.setPhoto(imageURL);


                                            //أخذ UID الخاص بالمستخدم الجديد
                                            String uid = authResult.getUser().getUid();
                                            //حفظ بال firebase
                                            fbs.getFire().collection("users")
                                                    .document(uid)/// هنا قمت بشيء فريد وهو اعطاء ال Auth وال  FireStore نفس العنوان لكل شخص
                                                    .set(user)
                                                    .addOnSuccessListener(unused -> {
                                                        //requestLocationPermission(); //  هنا المكان الصحيح
                                                        util.showMessageDialog(getActivity(), "User added successfully");
                                                        gotoAllPieces();
                                                        fbs.setSelectedImageURL(null);
                                                        selectedImage = null;
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Signup failed, please try again..", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Error checking username: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        btnBackFromSignUpToAll = getView().findViewById(R.id.btnBackFromSignUpToAll);
        btnBackFromSignUpToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogIn();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedImage != null) {
            outState.putParcelable("selectedImage", selectedImage);
        }
    }

    public void gotoAllPieces()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain,new AllPiecesFragment());
        ft.commit();
    }

    public void gotoLogIn()
    {
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain,new LogInFragment());
        ft.commit();
    }

    public void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImage = data.getData();

            // 1. عرض الصورة في الـ ImageView
            imgUserSignUp.setImageURI(selectedImage);

            // 2. هذه الخطوة هامة جداً: إزالة اللون البني (tint) برمجياً عند اختيار صورة
            imgUserSignUp.setImageTintList(null);

            // 3. حفظ الصورة في كلاس الخدمات
            fbs.setSelectedImageURL(selectedImage);
        }
    }
}