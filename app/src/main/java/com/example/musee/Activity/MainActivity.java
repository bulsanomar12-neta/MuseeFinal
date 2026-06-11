package com.example.musee.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;

import com.example.musee.Data.AddPieceFragment;
import com.example.musee.Data.CheckOutFragment;
import com.example.musee.Fragments.AllPiecesFragment;
import com.example.musee.Data.EditUserDetailsFragment;
import com.example.musee.Fragments.ForgotPasswordFragment;
import com.example.musee.Fragments.LogInFragment;
import com.example.musee.Data.SignUpFragment;
import com.example.musee.Fragments.UserHomePgFragment;
import com.example.musee.R;
import com.example.musee.classes.FirebaseServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//تهيئة paypal
/*
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;

 */

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private Stack<Fragment> fragmentStack = new Stack<>();
    private FirebaseServices fbs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
/*
        //  تهيئة PayPal
        CheckoutConfig config = new CheckoutConfig(
                this.getApplication(),                      // Context للتطبيق: يستخدمه PayPal SDK للوصول لإعدادات التطبيق
                "ATe-cGOLi0mIsBCU_IVa6_OX1vaZG1HQ3z8lqKX" +
                        "NTFaccAEKTmNaQAEKyAAs7sYVIJfr3zbu3g6kZeA0",                   // Client ID من حساب Sandbox الخاص بك على PayPal
                Environment.SANDBOX,                        // بيئة الاختبار Sandbox (ليست حقيقية، للتجربة فقط)
                "https://example.com/return"                // رابط العودة بعد الدفع (يمكن أن يكون رابط وهمي للاختبار)
        );
        PayPalCheckout.setConfig(config);                 // تهيئة مكتبة PayPal بالاعدادات أعلاه


 */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        if (savedInstanceState == null) {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Check if user is signed in (non-null).
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {
                // إذا لم يوجد مستخدم
                gotoAllPiecesFragment();

            } else
                    gotoUserHomeFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayOutMain);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void gotoAllPiecesFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new AllPiecesFragment());
        ft.commit();
    }

    public void gotoLogInFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new LogInFragment());// ادخال من والى
        ft.commit();
    }

    public void gotoSignUpFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();//.getActivity()=> لاننا ب fragment  وليس ب activity.
        ft.replace(R.id.frameLayOutMain, new SignUpFragment());// ادخال من والى
        ft.commit();
    }

    public void gotoAddPieceFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new AddPieceFragment());// ادخال من والى
        ft.commit();
    }
    public void gotoUserHomePgFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new UserHomePgFragment());// ادخال من والى
        ft.commit();
    }

    public void gotoEditUserDetailsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new EditUserDetailsFragment());// ادخال من والى
        ft.commit();
    }
    public void gotoForgotPasswordFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new ForgotPasswordFragment());// ادخال من والى
        ft.commit();
    }
    public void gotoUserHomeFragment (){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayOutMain, new UserHomePgFragment());// ادخال من والى
        ft.commit();
    }

}