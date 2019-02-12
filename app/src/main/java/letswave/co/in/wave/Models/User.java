package letswave.co.in.wave.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String id;
    private String authorityIssuerName;
    private String authorityIssuedId;
    private String name;
    private String email;
    private String photo;
    private String phone;

    public User(String id, String authorityIssuerName, String authorityIssuedId, String name, String email, String photo, String phone) {
        this.id = id;
        this.authorityIssuerName = authorityIssuerName;
        this.authorityIssuedId = authorityIssuedId;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorityIssuerName() {
        return authorityIssuerName;
    }

    public void setAuthorityIssuerName(String authorityIssuerName) {
        this.authorityIssuerName = authorityIssuerName;
    }

    public String getAuthorityIssuedId() {
        return authorityIssuedId;
    }

    public void setAuthorityIssuedId(String authorityIssuedId) {
        this.authorityIssuedId = authorityIssuedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", authorityIssuerName='" + authorityIssuerName + '\'' +
                ", authorityIssuedId='" + authorityIssuedId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.authorityIssuerName);
        dest.writeString(this.authorityIssuedId);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.photo);
        dest.writeString(this.phone);
    }

    private User(Parcel in) {
        this.id = in.readString();
        this.authorityIssuerName = in.readString();
        this.authorityIssuedId = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.photo = in.readString();
        this.phone = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
