package com.example.musee.Data;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.musee.Fragments.AllPiecesFragment;
import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.example.musee.classes.PieceClass;
import com.example.musee.classes.UtilsClass;
import com.google.api.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddPieceFragment extends Fragment {
    ///////////////////////////////////////////
    private EditText etNameAddPieceFragment, etArtistAddPieceFragment, etHoursAddPieceFragment, etInformationAddPieceFragment,etPriceAddPieceFragment;
    private static final int GALLERY_REQUEST_CODE = 123;

    Spinner etSizeAddPieceFragment,spCategoryAddPiece;
    private Button btAddPieceFragment;
    private ImageButton btnBackAddPieceFragment;
    private FirebaseServices fbs;
    private UtilsClass utils; // ✅ تمت الإضافة (لرفع الصورة)
    private ImageView imgVImageAddPieceFragment;
    private Uri selectedImageUri; // ✅ تمت الإضافة (لتخزين الصورة مؤقتًا)
    private Context context;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()== Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgVImageAddPieceFragment.setImageURI(selectedImageUri);
                    //  تمت الإضافة من كود الأستاذ: رفع الصورة بعد اختيارها
                    //utils.uploadImage(getActivity(), selectedImageUri);
                }
            });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_piece, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        connectComponents();
    }

    private void connectComponents() {
        fbs = FirebaseServices.getInstance();
        utils = UtilsClass.getInstance();

        etNameAddPieceFragment = getView().findViewById(R.id.etNameAddPieceFragment);
        etArtistAddPieceFragment = getView().findViewById(R.id.etArtistAddPieceFragment);
        etHoursAddPieceFragment = getView().findViewById(R.id.etHoursAddPieceFragment);
        etInformationAddPieceFragment = getView().findViewById(R.id.etInformationAddPieceFragment);
        etSizeAddPieceFragment = getView().findViewById(R.id.etSizeAddPieceFragment);
        etPriceAddPieceFragment = getView().findViewById(R.id.etPriceAddPieceFragment);

        btAddPieceFragment = getView().findViewById(R.id.btAddPieceFragment);
        imgVImageAddPieceFragment = getView().findViewById(R.id.imgPieceItem);
        spCategoryAddPiece = getView().findViewById(R.id.spCategoryAddPiece);
        btnBackAddPieceFragment = getView().findViewById(R.id.btnBackAddPieceFragment);

        // ==========================================
        // تم إضافة كود ربط السبنر لـ Category برمجياً لمنع ظهور item 1
        // ==========================================
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.types_array, // يقرأ المصفوفة المعدلة من strings.xml
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoryAddPiece.setAdapter(categoryAdapter);

        // ==========================================
        // تم إضافة كود ربط السبنر لـ Size برمجياً لمنع ظهور item 1
        // ==========================================
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.types_array_size, // يقرأ مصفوفة الأحجام الجاهزة
                android.R.layout.simple_spinner_item
        );
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etSizeAddPieceFragment.setAdapter(sizeAdapter);
        // ==========================================

        btAddPieceFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // تحقق من تسجيل الدخول
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Toast.makeText(getActivity(), "Please log in first", Toast.LENGTH_SHORT).show();
                    return;
                }
                addToFirestore();
            }
        });

        imgVImageAddPieceFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // برمجة زر العودة
        btnBackAddPieceFragment.setOnClickListener(v -> {
            if (getActivity() != null) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity == null)
                    return;
                mainActivity.gotoUserHomePgFragment();
            }
        });
    }

/*
    private void connectComponents() {
        fbs = FirebaseServices.getInstance();
        utils = UtilsClass.getInstance();// ✅ تمت الإضافة من كود الأستاذ

        etNameAddPieceFragment = getView().findViewById(R.id.etNameAddPieceFragment);
        etArtistAddPieceFragment = getView().findViewById(R.id.etArtistAddPieceFragment);
        etHoursAddPieceFragment = getView().findViewById(R.id.etHoursAddPieceFragment);
        etInformationAddPieceFragment = getView().findViewById(R.id.etInformationAddPieceFragment);
        etSizeAddPieceFragment = getView().findViewById(R.id.etSizeAddPieceFragment);
        etPriceAddPieceFragment = getView().findViewById(R.id.etPriceAddPieceFragment);
        //button for add piece
        btAddPieceFragment = getView().findViewById(R.id.btAddPieceFragment);
        imgVImageAddPieceFragment = getView().findViewById(R.id.imgPieceItem);
        spCategoryAddPiece = getView().findViewById(R.id.spCategoryAddPiece);
        btnBackAddPieceFragment = getView().findViewById(R.id.btnBackAddPieceFragment);

        btAddPieceFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // تحقق من تسجيل الدخول
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    Toast.makeText(getActivity(), "Please log in first", Toast.LENGTH_SHORT).show();
                    return;
                }
                addToFirestore();
            }
        });

        imgVImageAddPieceFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // برمجة زر العودة
        btnBackAddPieceFragment.setOnClickListener(v -> {
            if (getActivity() != null) {
                // الحصول على MainActivity لاستخدام navigation
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity == null)
                    return;
                mainActivity.gotoUserHomePgFragment();
            }
        });
    }
 */

    private void addToFirestore() {
        String name,artist,hours,information,category,size,price;
        name = etNameAddPieceFragment.getText().toString();
        category = spCategoryAddPiece.getSelectedItem().toString();
        artist = etArtistAddPieceFragment.getText().toString();
        hours = etHoursAddPieceFragment.getText().toString();
        information = etInformationAddPieceFragment.getText().toString();
        size = etSizeAddPieceFragment.getSelectedItem().toString();
        price = etPriceAddPieceFragment.getText().toString();

        if(selectedImageUri == null){
            Toast.makeText(getActivity(), "Please choose an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.trim().isEmpty() || artist.trim().isEmpty() || hours.trim().isEmpty() || information.trim().isEmpty() || category.trim().isEmpty() || size.trim().isEmpty() || price.trim().isEmpty()){
            Toast.makeText(getActivity(), "Some fields are empty.", Toast.LENGTH_LONG).show();
            return;
        }


        // Check for logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(getActivity(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "Please wait, uploading data...", Toast.LENGTH_SHORT).show();
        btAddPieceFragment.setEnabled(false); // Disable button to prevent multiple clicks

        // Start the image upload to Firebase Storage
        String imagePath = "images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = fbs.getStorage().getReference().child(imagePath);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    // 3. When upload is successful, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        // This is the public URL of your uploaded image
                        String imageURL = uri.toString();

                        // Use the URL to create your PieceClass object
                        PieceClass piece = new PieceClass(name, category, artist, hours, size, information, price, imageURL, user.getEmail());

                        piece.setSold(false); // تأكدي أن اللوحة الجديدة ليست مباعة

                        // Save the PieceClass object to Firestore
                        fbs.getFire().collection("pieces")
                                .add(piece) // اضافة الكائن
                                .addOnSuccessListener(documentReference -> {

                                    String generatedId = documentReference.getId(); // ID الذي أنشأه Firestore
                                    piece.setPieceId(generatedId); // ربط الكائن بالـ ID

                                    // ✅ 1. تحديث المستخدم مباشرة (مهم!)
                                    fbs.getFire().collection("users")
                                            .document(user.getUid())
                                            .update("userPieces", FieldValue.arrayUnion(generatedId))
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to update user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });

                                    // ✅ 2. تحديث القطعة (pieceId + isSold)
                                    fbs.getFire().collection("pieces")
                                            .document(generatedId)
                                            .update(
                                                    "pieceId", generatedId,
                                                    "isSold", false
                                            )
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(requireContext(), "Art piece added successfully", Toast.LENGTH_LONG).show();
                                                btAddPieceFragment.setEnabled(true);
                                                gotoAllPieces();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to update details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                btAddPieceFragment.setEnabled(true);
                                            });

                                });

                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btAddPieceFragment.setEnabled(true);
                    });

                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(getActivity(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btAddPieceFragment.setEnabled(true);
                });
                    }
    public void gotoAllPieces() {

    FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.frameLayOutMain,new AllPiecesFragment());//فقط الاسم مختلف
    ft.commit();
   }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

}