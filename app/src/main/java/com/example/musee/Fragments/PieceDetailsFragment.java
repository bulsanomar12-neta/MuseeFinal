package com.example.musee.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;


import com.example.musee.classes.FirebaseServices;
import com.example.musee.classes.PieceClass;
import com.squareup.picasso.Picasso;


public class PieceDetailsFragment extends Fragment {
    private static final int PERMISSION_SEND_SMS = 1;
    private static final int REQUEST_CALL_PERMISSION = 2;
    private FirebaseServices fbs;
    private TextView tvArtNamePieceDetails, tvSizePieceDetails, tvInformationPieceDetails, tvArtistNamePieceDetails,
            tvHoursPieceDetails, tvCategoryPieceDetails, tvPricePieceDetails;
    private ImageView imgPieceDetails;
    private PieceClass myPiece;
    private Button btAddtocartlPieceDetailsFragment, btEmailPieceDetailsFragment;
    private ImageButton btnBackFromDetailsToAll;
    private boolean isEnlarged = false;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PieceDetailsFragment() {
        // Required empty public constructor
    }

    public static PieceDetailsFragment newInstance(String param1, String param2) {
        PieceDetailsFragment fragment = new PieceDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_piece_details, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        init();
        ImageView ivPiecePhoto = getView().findViewById(R.id.imgPieceDetails);

        ivPiecePhoto.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = ivPiecePhoto.getLayoutParams();
                if (isEnlarged) {
                    layoutParams.height = 500;
                } else {
                    layoutParams.height = 2200;
                }
                ivPiecePhoto.setLayoutParams(layoutParams);

                // נשנה את המצב הנוכחי של התמונה
                isEnlarged = !isEnlarged;

            }
        });
    }

    public void init() {
        fbs = FirebaseServices.getInstance();
        tvArtNamePieceDetails = getView().findViewById(R.id.tvArtNamePieceDetails);
        tvSizePieceDetails = getView().findViewById(R.id.tvSizePieceDetails);
        tvInformationPieceDetails = getView().findViewById(R.id.tvInformationPieceDetails);
        tvArtistNamePieceDetails = getView().findViewById(R.id.tvArtistNamePieceDetails);
        tvHoursPieceDetails = getView().findViewById(R.id.tvHoursPieceDetails);
        tvCategoryPieceDetails = getView().findViewById(R.id.tvCategoryPieceDetails);
        tvPricePieceDetails = getView().findViewById(R.id.tvPricePieceDetails);
        imgPieceDetails = getView().findViewById(R.id.imgPieceDetails);

        btAddtocartlPieceDetailsFragment = getView().findViewById(R.id.btAddtocartlPieceDetailsFragment); // زر الـ SMS سابقاً
        btEmailPieceDetailsFragment = getView().findViewById(R.id.btEmailPieceDetailsFragment);
        btnBackFromDetailsToAll = getView().findViewById(R.id.btnBackFromDetailsToAll);

        Bundle args = getArguments();
        if (args != null) {
            myPiece = args.getParcelable("pieces");
            if (myPiece != null) {
                tvArtNamePieceDetails.setText(myPiece.getname());
                tvSizePieceDetails.setText(myPiece.getSize());
                tvInformationPieceDetails.setText(myPiece.getInformation());
                tvArtistNamePieceDetails.setText(myPiece.getArtistName());
                tvHoursPieceDetails.setText(myPiece.getHours());
                tvCategoryPieceDetails.setText(myPiece.getCategory());
                tvPricePieceDetails.setText(myPiece.getPrice() + " $");
                if (myPiece.getPhoto() == null || myPiece.getPhoto().isEmpty()) {
                    //Picasso.get().load(R.drawable.ic_fav).into(imgPieceDetails);
                } else {
                    Picasso.get().load(myPiece.getPhoto()).into(imgPieceDetails);
                }

                //  برمجة زر الإيميل
                btEmailPieceDetailsFragment.setOnClickListener(v -> {
                    String artName = myPiece.getname();
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:gallery@example.com"));

                    intent.putExtra(Intent.EXTRA_SUBJECT,
                            "Inquiry about: " + artName);

                    intent.putExtra(Intent.EXTRA_TEXT,
                            "Hello, I am interested in your artwork: " + artName);

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(),
                                "No email app found",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                //  برمجة زر الشراء - النسخة المصححة لضمان عدم إنشاء مستندات مكررة
                btAddtocartlPieceDetailsFragment.setOnClickListener(v -> {
                    FirebaseUser user = fbs.getAuth().getCurrentUser();
                    if (user == null) {
                        Toast.makeText(getActivity(), "Login first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // نستخدم الـ ID الخاص باللوحة الأصلية لربطها بسلة المستخدم
                    String pieceId = myPiece.getPieceId();
                    if (pieceId == null || pieceId.isEmpty()) {
                        Toast.makeText(getActivity(), "Piece ID is missing!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // إضافة معرف اللوحة إلى سلة المستخدم في قاعدة البيانات
                    fbs.getFire().collection("users")
                            .document(user.getUid())
                            .update("userPiecesCart", FieldValue.arrayUnion(pieceId))
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getActivity(), myPiece.getArtistName() + " added to cart!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                // زر الرجوع
                btnBackFromDetailsToAll.setOnClickListener(v -> {

                    if (getActivity() == null) return;

                    //معرفة من أين جاء المستخدم
                    String from = getArguments() != null
                            ? getArguments().getString("from", "all")
                            : "all"; //اذا لا ناخذ ال all

                    MainActivity mainActivity = (MainActivity) getActivity();

                    if (from.equals("home")) {
                        mainActivity.gotoUserHomeFragment();

                    } else if (from.equals("checkout")) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                        // أو mainActivity.gotoCheckOutFragment()

                    } else {
                        mainActivity.gotoAllPiecesFragment();
                    }
                });            }
        }
    }
}