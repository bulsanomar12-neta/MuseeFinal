package com.example.musee.classes;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.IgnoreExtraProperties;////////////

import androidx.annotation.NonNull;

@IgnoreExtraProperties/// /////
public class PieceClass implements Parcelable {
    private String name;
    private String category;
    private String artistName;
    private String hours;
    private String size;
    private String information;
    private  String price;
    private String photo;
    private String currentUsereMail;
    private String pieceId;
    @PropertyName("isSold")
    private boolean isSold;


    public PieceClass()
    {}

    public PieceClass(String name, String category, String artistName, String hours,String size, String information,String price, String photo,String currentUsereMail){
        this.name = name;
        this.category = category;
        this.artistName = artistName;
        this.hours = hours;
        this.size = size;
        this.information = information;
        this.price = price;
        this.photo = photo;
        this.currentUsereMail = currentUsereMail;
        this.pieceId = null;
        this.isSold = false; // افتراضياً اللوحة غير مباعة
    }

    // تحديث Parcelable (مهم جداً لنقل البيانات بين الصفحات)
    protected PieceClass(Parcel in) {
        name = in.readString();
        category = in.readString();
        artistName = in.readString();
        hours = in.readString();
        size = in.readString();
        information = in.readString();
        price = in.readString();
        photo = in.readString();
        currentUsereMail = in.readString();
        pieceId = in.readString();
        isSold = in.readByte() != 0; // قراءة البولين
    }


    public static final Creator<PieceClass> CREATOR = new Creator<PieceClass>() {
        @Override
        public PieceClass createFromParcel(Parcel in) {
            return new PieceClass(in);
        }

        @Override
        public PieceClass[] newArray(int size) {
            return new PieceClass[size];
        }
    };

    public String getname(){return name;}
    public String getCategory(){return category;}
    public String getArtistName(){return artistName;}
    public String getHours(){return hours;}
    public String getSize(){return size;}
    public  String getInformation(){return information;}
    public String getPrice() {return price;}
    public String getPhoto(){return photo;}
    public String getCurrentUsereMail() {return currentUsereMail;}
    @PropertyName("isSold")
    public boolean isSold() {
        return isSold;
    }

    @PropertyName("isSold")
    public void setSold(boolean sold) {
        isSold = sold;
    }

    // 2️⃣ أضف getter و setter
    public String getPieceId() {
        return pieceId;
    }

    public void setPieceId(String pieceId) {
        this.pieceId = pieceId;
    }




    public String toString() {
        return "Piece{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", artistName='" + artistName + '\'' +
                ", hours='" + hours + '\'' +
                ", size='" + size + '\'' +
                ", information='" + information + '\'' +
                ", price='" + price + '\'' +
                ", photo='" + photo + '\'' +
                ", currentUsereMail='" + currentUsereMail + '\'' +
                '}';
    }

    // تحويل الكائن الي شكل قابل للتنفل بين الصفحات
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(artistName);
        dest.writeString(hours);
        dest.writeString(size);
        dest.writeString(information);
        dest.writeString(price);
        dest.writeString(photo);
        dest.writeString(currentUsereMail);
        dest.writeString(pieceId);
        dest.writeByte((byte) (isSold ? 1 : 0)); // كتابة البولين
    }

    @Override
    public int describeContents() {return 0;}
}
