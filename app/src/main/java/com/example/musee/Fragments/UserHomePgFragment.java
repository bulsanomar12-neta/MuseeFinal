package com.example.musee.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musee.Adapters.AllPiecesAdapter;
import com.example.musee.Data.CheckOutFragment;
import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.PieceClass;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserHomePgFragment extends Fragment {

    private Button btGoToAllUserHomePgFragment;
    private MaterialButton btGoToAddUserHomePgFragmint;
    private View btEditDetailsUserHomePgFragmint, btnGoToCheckout;
    private ImageView imgMenuOptions, imgUserHome;
    private FirebaseAuth mAuth;

    private RecyclerView rvUserPieces;
    private AllPiecesAdapter userPiecesAdapter;
    private ArrayList<PieceClass> userPiecesList;

    // قائمة لتخزين معرفات السلة فقط لنقلها لصفحة الدفع
    private ArrayList<String> cartIdsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_homepg, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        mAuth = FirebaseAuth.getInstance();
        imgMenuOptions = view.findViewById(R.id.imgMenuOptions);
        imgUserHome = view.findViewById(R.id.imgUserHome);
        btGoToAllUserHomePgFragment = view.findViewById(R.id.btGoToAllUserHomePgFragment);
        btGoToAddUserHomePgFragmint = view.findViewById(R.id.btGoToAddUserHomePgFragmint);
        btEditDetailsUserHomePgFragmint = view.findViewById(R.id.btEditDetailsUserHomePgFragmint);
        btnGoToCheckout = view.findViewById(R.id.btnGoToCheckout);

        // إعداد RecyclerView لوحاتي بشكل أفقي
        rvUserPieces = view.findViewById(R.id.rvUserPiecesHomePgFragment);
        LinearLayoutManager horizontalMgr = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvUserPieces.setLayoutManager(horizontalMgr);

        userPiecesList = new ArrayList<>();
        userPiecesAdapter = new AllPiecesAdapter(getContext(), userPiecesList);
        rvUserPieces.setAdapter(userPiecesAdapter);

        userPiecesAdapter.setOnItemClickListener(position -> {

            PieceClass selectedPiece = userPiecesList.get(position);

            Bundle args = new Bundle();
            args.putParcelable("pieces", selectedPiece);
            args.putString("pieceDocId", selectedPiece.getPieceId());

            // 👇 هذا هو المهم
            args.putString("from", "home");

            PieceDetailsFragment fragment = new PieceDetailsFragment();
            fragment.setArguments(args);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayOutMain, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // تحميل بيانات المستخدم من Firestore
        loadDataFromFirestore();

        // تحميل صورة المستخدم
        loadUserImage();

        // زر الانتقال لصفحة الدفع
        btnGoToCheckout.setOnClickListener(v -> {
            if (cartIdsList.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                CheckOutFragment fragment = CheckOutFragment.newInstance(cartIdsList);
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayOutMain, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // فتح القائمة الجانبية
        imgMenuOptions.setOnClickListener(v -> showMyMenu(mainActivity));

        btGoToAllUserHomePgFragment.setOnClickListener(v -> mainActivity.gotoAllPiecesFragment());
        btGoToAddUserHomePgFragmint.setOnClickListener(v -> mainActivity.gotoAddPieceFragment());
        btEditDetailsUserHomePgFragmint.setOnClickListener(v -> mainActivity.gotoEditUserDetailsFragment());
    }

    // تحميل بيانات المستخدم (لوحات + سلة)
    private void loadDataFromFirestore() {
        if (mAuth.getCurrentUser() == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // جلب لوحاتي الشخصية
                        List<String> userPiecesIds = (List<String>) documentSnapshot.get("userPieces");
                        if (userPiecesIds != null) fetchUserPieces(userPiecesIds);

                        // جلب قائمة الـ IDs للسلة
                        List<String> cartIds = (List<String>) documentSnapshot.get("userPiecesCart");
                        if (cartIds != null) {
                            cartIdsList.clear();
                            cartIdsList.addAll(cartIds);
                        }
                    }
                });
    }

    // جلب تفاصيل اللوحات من Firestore
    private void fetchUserPieces(List<String> ids) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String id : ids) {
            db.collection("pieces").document(id).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    PieceClass p = doc.toObject(PieceClass.class);
                    if (p != null) {
                        userPiecesList.add(p);
                        userPiecesAdapter.notifyItemInserted(userPiecesList.size() - 1);
                    }
                }
            });
        }
    }

    private void loadUserImage() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists() && doc.getString("photo") != null) {
                            Picasso.get().load(doc.getString("photo"))
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .fit().centerCrop().into(imgUserHome);
                        }
                    });
        }
    }

    // قائمة الخيارات (تسجيل خروج / حذف حساب)
    private void showMyMenu(MainActivity mainActivity) {
        PopupMenu popup = new PopupMenu(getContext(), imgMenuOptions);
        popup.getMenu().add("Sign Out");
        popup.getMenu().add("Delete Account");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Sign Out")) {
                mAuth.signOut();
                mainActivity.gotoLogInFragment();
            } else if (item.getTitle().equals("Delete Account")) {
                showDeleteDialog();
            }
            return true;
        });
        popup.show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("This action is permanent. Continue?")
                .setPositiveButton("Yes", (d, w) -> safeDeleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    // نظام الحذف الآمن (يحذف كل شيء خطوة خطوة)
    private void safeDeleteAccount() {

        if (mAuth.getCurrentUser() == null) return;

        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        String email = user.getEmail();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. حذف اللوحات أولاً
        deletePiecesSafe(db, email, () -> {

            // 2. حذف بيانات المستخدم
            db.collection("users").document(uid)
                    .delete()
                    .addOnSuccessListener(unused -> {

                        // حذف الحساب من Firebase Authentication
                        deleteAuthSafe(user);

                    })
                    .addOnFailureListener(e -> {
                        handleError("User data delete failed", e);
                        deleteAuthSafe(user); // نحاول نكمل رغم الفشل
                    });

        });
    }

    // حذف اللوحات الخاصة بالمستخدم
    private void deletePiecesSafe(FirebaseFirestore db, String email, Runnable onComplete) {

        if (email == null) {
            onComplete.run();
            return;
        }

        db.collection("pieces")
                .whereEqualTo("currentUsereMail", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        onComplete.run();
                        return;
                    }

                    int total = querySnapshot.size();
                    final int[] counter = {0};

                    for (int i = 0; i < total; i++) {

                        String docId = querySnapshot.getDocuments().get(i).getId();

                        db.collection("pieces").document(docId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    counter[0]++;
                                    if (counter[0] == total) {
                                        onComplete.run();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    counter[0]++;
                                    handleError("Piece delete failed", e);
                                    if (counter[0] == total) {
                                        onComplete.run();
                                    }
                                });
                    }

                })
                .addOnFailureListener(e -> {
                    handleError("Query pieces failed", e);
                    onComplete.run();
                });
    }


    // Dialog تأكيد حذف الحساب
    private void deleteAuthSafe(FirebaseUser user) {

        user.delete()
                .addOnSuccessListener(unused -> {

                    finishDeleteFlow();

                })
                .addOnFailureListener(e -> {

                    // إذا يحتاج إعادة تسجيل دخول
                    if (e.getMessage() != null &&
                            e.getMessage().toLowerCase().contains("recent")) {

                        handleError("Requires re-authentication", e);

                        // هنا يمكنك لاحقًا فتح Dialog كلمة مرور
                        mAuth.signOut();
                        ((MainActivity) getActivity()).gotoLogInFragment();

                    } else {
                        handleError("Auth delete failed", e);
                        mAuth.signOut();
                        finishDeleteFlow();
                    }
                });
    }

    // إنهاء عملية حذف الحساب بعد النجاح
    private void finishDeleteFlow() {

        try {
            mAuth.signOut();

            if (getActivity() != null) {
                ((MainActivity) getActivity()).gotoAllPiecesFragment();
            }

            Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            handleError("Finish flow error", e);
        }
    }

    private void handleError(String tag, Exception e) {
        Log.e("DELETE_ACCOUNT", tag + " -> " + e.getMessage());

        if (getContext() != null) {
            Toast.makeText(getContext(),
                    tag + ": " + (e.getMessage() != null ? e.getMessage() : "Unknown error"),
                    Toast.LENGTH_LONG).show();
        }
    }
}