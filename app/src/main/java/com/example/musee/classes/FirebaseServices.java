package com.example.musee.classes;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseServices {
    private static  FirebaseServices instance;
    private FirebaseAuth auth;
    private FirebaseFirestore fire;
    private FirebaseStorage storage;
    private Uri selectedImageURL;
    private boolean userChangeFlag;
    private ArrayList<User> users;
    private StorageReference storageRef;




    public FirebaseServices(){
        auth = FirebaseAuth.getInstance();
        fire = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        users = new ArrayList<>();
        selectedImageURL = null;
    }
    public Uri getSelectedImageURL() {
        return selectedImageURL;
    }

    public void setSelectedImageURL(Uri selectedImageURL) {
        this.selectedImageURL = selectedImageURL;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseFirestore getFire() {
        return fire;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    //أعطني نسخة واحدة فقط من FirebaseServices، وإذا لم تكن موجودة أنشئها
    public static FirebaseServices getInstance(){
        if (instance == null) {
            instance = new FirebaseServices();
        }
        return instance ;
    }
    /*
    public void setSelectedImageURL(Uri selectedImageURL) {
        this.selectedImageURL = selectedImageURL;
    }
     */
    public void setUserChangeFlag(boolean userChangeFlag) {
        this.userChangeFlag = userChangeFlag;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    
    public void updateUser(User user,
                           OnSuccessListener<Void> onSuccess,
                           OnFailureListener onFailure)
    {
        //  استخدامت UID بدل البحث بالـ userName
        String uid = auth.getCurrentUser().getUid();

        //  حذف الـ Query بالكامل واستخدام document(uid)
        fire.collection("users")
                .document(uid)
                .update(
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "userName", user.getUserName(),
                        "address", user.getAddress(),
                        "phoneNum", user.getPhoneNum(),
                        "photo", user.getPhoto(),
                        "eMail", user.geteMail()
                )
                //   تمرير success و failure من الخارج
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
