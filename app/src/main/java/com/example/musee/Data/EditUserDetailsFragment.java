package com.example.musee.Data;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.example.musee.classes.User;
import com.example.musee.classes.UtilsClass;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditUserDetailsFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 134;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private EditText etFirstNameEditUserDetails, etLastNameEditUserDetails, etUserNameEditUserDetails2,
            etPhoneNumEditUserDetails, etAddressEditUserDetails;
    private Button btUpdateEditUserDetails;
    private ImageButton btnBackEditUserDetails; // تعريف زر العودة

    private ImageView imgUserEditUserDetails;
    private FirebaseServices fbs;
    private UtilsClass util;
    private LocationManager locationManager;
    private Uri selectedImageUri;
    private boolean flagAlreadyFilled = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public EditUserDetailsFragment() { }

    public static EditUserDetailsFragment newInstance(String param1, String param2) {
        EditUserDetailsFragment fragment = new EditUserDetailsFragment();
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

        View view = inflater.inflate(R.layout.fragment_edit_user_details, container, false);


        // ربط العناصر بالـ XML
        etFirstNameEditUserDetails = view.findViewById(R.id.etFirstNameEditUserDetails);
        etLastNameEditUserDetails = view.findViewById(R.id.etLastNameEditUserDetails);
        etUserNameEditUserDetails2 = view.findViewById(R.id.etUserNameEditUserDetails2);
        etPhoneNumEditUserDetails = view.findViewById(R.id.etPhoneNumEditUserDetails);
        etAddressEditUserDetails = view.findViewById(R.id.etAddressEditUserDetails);
        btUpdateEditUserDetails = view.findViewById(R.id.btUpdateEditUserDetails);
        imgUserEditUserDetails = view.findViewById(R.id.imgUserEditUserDetailsFragment);
        btnBackEditUserDetails = view.findViewById(R.id.btnBackEditUserDetails); // ربط زر العودة

        // برمجة زر العودة
        btnBackEditUserDetails.setOnClickListener(v -> {
            if (getActivity() != null) {
                // الحصول على MainActivity لاستخدام navigation
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity == null)
                    return;
                mainActivity.gotoUserHomePgFragment();
            }
        });

        imgUserEditUserDetails.setOnClickListener(v -> openGallery());

        util = new UtilsClass();
        fbs = FirebaseServices.getInstance();

        // Fill current user data
        fillUserData();

        // Update button
        btUpdateEditUserDetails.setOnClickListener(v -> updateUserData());

        return view;
    }

    private void fillUserData() {
        if (flagAlreadyFilled) return;
        String uid = fbs.getAuth().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            etFirstNameEditUserDetails.setText(user.getFirstName());
                            etLastNameEditUserDetails.setText(user.getLastName());
                            etUserNameEditUserDetails2.setText(user.getUserName());
                            etAddressEditUserDetails.setText(user.getAddress());
                            etPhoneNumEditUserDetails.setText(user.getPhoneNum());
                            if (user.getPhoto() != null && !user.getPhoto().isEmpty())
                                Picasso.get().load(user.getPhoto()).into(imgUserEditUserDetails);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("EditUserDetails", "Error loading user: " + e.getMessage()));
        flagAlreadyFilled = true;
    }

    private void updateUserData() {
        String firstName = etFirstNameEditUserDetails.getText().toString().trim();
        String lastName = etLastNameEditUserDetails.getText().toString().trim();
        String userName = etUserNameEditUserDetails2.getText().toString().trim();
        String phoneNum = etPhoneNumEditUserDetails.getText().toString().trim();
        String address = etAddressEditUserDetails.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || phoneNum.isEmpty() || address.isEmpty()) {
            Toast.makeText(getActivity(), "Some fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = fbs.getAuth().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(uid);

        userRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                User user = document.toObject(User.class);

                // تحديث القيم داخل الكائن (كما كان عندك)
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUserName(userName);
                user.setPhoneNum(phoneNum);
                user.setAddress(address);

                if (selectedImageUri != null) {
                    StorageReference ref = FirebaseStorage.getInstance()
                            .getReference("profile_images/" + uid + ".jpg");

                    ref.putFile(selectedImageUri)
                            .continueWithTask(task -> ref.getDownloadUrl())
                            .addOnSuccessListener(uri -> {

                                user.setPhoto(uri.toString());

                                // ✅ تم التغيير هنا: استخدمنا update بدل set (حتى لا تُحذف الـ arrays)
                                userRef.update(
                                        "firstName", user.getFirstName(),
                                        "lastName", user.getLastName(),
                                        "userName", user.getUserName(),
                                        "phoneNum", user.getPhoneNum(),
                                        "address", user.getAddress(),
                                        "photo", user.getPhoto()
                                ).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Data updated successfully!", Toast.LENGTH_SHORT).show();
                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    if (mainActivity != null)
                                        mainActivity.gotoUserHomePgFragment();
                                }).addOnFailureListener(e ->
                                        Toast.makeText(getActivity(), "Failed to update data", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show());

                } else {

                    // ✅ تم التغيير هنا أيضاً: استبدلنا set بـ update
                    userRef.update(
                            "firstName", user.getFirstName(),
                            "lastName", user.getLastName(),
                            "userName", user.getUserName(),
                            "phoneNum", user.getPhoneNum(),
                            "address", user.getAddress()
                    ).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Data updated successfully!", Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity != null)
                            mainActivity.gotoUserHomeFragment();
                    }).addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Failed to update data", Toast.LENGTH_SHORT).show());
                }

            } else {
                Toast.makeText(getActivity(), "Current user not found!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Error fetching user data", Toast.LENGTH_SHORT).show());
    }

    public void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgUserEditUserDetails.setImageURI(selectedImageUri);
        }
    }
}