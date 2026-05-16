package com.example.musee.Data;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musee.Adapters.AllPiecesAdapter;
import com.example.musee.Fragments.PieceDetailsFragment;
import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.classes.PieceClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckOutFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private View payPalButton; // ملاحظة: استخدمنا View بدلاً من PayPalButton لتجاوز مشكلة المكتبة
    private ImageButton btnBackFromCheckout;
    private AllPiecesAdapter cartAdapter;
    private ArrayList<PieceClass> cartList;
    private ArrayList<String> cartIds;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public CheckOutFragment() {}

    public static CheckOutFragment newInstance(ArrayList<String> ids) {
        CheckOutFragment fragment = new CheckOutFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("CART_IDS", ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_check_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvCart = view.findViewById(R.id.rv_checkout_items);
        tvTotal = view.findViewById(R.id.tv_total_amount);
        payPalButton = view.findViewById(R.id.payPalButtonEnd);

        // ملاحظة: كود Config الخاص ببايبال تم تجميده هنا لضمان عمل التطبيق بدون أخطاء السيرفر
        /*
        CheckoutConfig config = new CheckoutConfig(
            requireActivity().getApplication(),
            "YOUR_CLIENT_ID",
            Environment.SANDBOX,
            CurrencyCode.USD,
            UserAction.PAY_NOW,
            "com.example.musee://paypalpay"
        );
        PayPalCheckout.setConfig(config);
        */

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new AllPiecesAdapter(getContext(), cartList);
        rvCart.setAdapter(cartAdapter);

        cartAdapter.setOnItemClickListener(position -> {

            PieceClass selectedPiece = cartList.get(position);

            Bundle args = new Bundle();
            args.putParcelable("pieces", selectedPiece);
            args.putString("pieceDocId", selectedPiece.getPieceId());

            // مهم جدًا
            args.putString("from", "checkout");

            PieceDetailsFragment fragment = new PieceDetailsFragment();
            fragment.setArguments(args);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayOutMain, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (getArguments() != null) {
            cartIds = getArguments().getStringArrayList("CART_IDS");
            if (cartIds != null && !cartIds.isEmpty()) {
                fetchCartItems(cartIds);
            }
        }

        setupPayPalAction();
        btnBackFromCheckout = view.findViewById(R.id.btnBackFromCheckout);
        btnBackFromCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoUserHomeFragment();
            }
        });
    }

    private void fetchCartItems(List<String> ids) {
        for (String id : ids) {
            db.collection("pieces").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            PieceClass piece = documentSnapshot.toObject(PieceClass.class);
                            if (piece != null) {
                                // ملاحظة: هنا نتأكد من وضع الـ ID يدوياً إذا لم يكن مخزناً داخل الـ Object
                                piece.setPieceId(documentSnapshot.getId());
                                cartList.add(piece);
                                cartAdapter.notifyItemInserted(cartList.size() - 1);
                                updateTotal();
                            }
                        }
                    });
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (PieceClass piece : cartList) {
            try {
                total += Double.parseDouble(piece.getPrice());
            } catch (Exception e) { e.printStackTrace(); }
        }
        tvTotal.setText("Total: " + String.format(Locale.US, "%.2f", total) + " $");
    }

    private void setupPayPalAction() {
        payPalButton.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // محاكاة الاتصال
            Toast.makeText(getContext(), "Connecting to PayPal...", Toast.LENGTH_SHORT).show();

            // ملاحظة: التحديث في Firebase يتم فقط *بعد* الضغط على الزر وانتظار ثانيتين (محاكاة الدفع)
            v.postDelayed(() -> {
                handlePaymentSuccess();
                Toast.makeText(getContext(), "Purchase completed successfully! Thank you.",
                        Toast.LENGTH_LONG).show();
            }, 2000);
        });
    }

    private void handlePaymentSuccess() {
        // 1. الآن فقط تصبح اللوحات "مباعة" (true)
        markPiecesAsSold();

        // 2. إفراغ سلة المستخدم
        clearUserCart();

        // 3. العودة للشاشة السابقة
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void markPiecesAsSold() {
        for (PieceClass piece : cartList) {
            if (piece.getPieceId() != null) {
                // ملاحظة: هذا السطر هو الذي يجعل اللوحة "مباعة" في الفايربيس
                db.collection("pieces").document(piece.getPieceId())
                        .update("isSold", true);
            }
        }
    }

    private void clearUserCart() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            // ملاحظة: نقوم بمسح القائمة بالكامل من حساب المستخدم بعد الدفع
            db.collection("users").document(uid)
                    .update("userPiecesCart", new ArrayList<>());
        }
    }
}