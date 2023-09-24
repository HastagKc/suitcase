package com.example.suitcase.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suitcase.Database.Database;
import com.example.suitcase.R;

import java.io.ByteArrayOutputStream;


public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }

    final int GALLERY_CODE = 200;
    Database db;
    String value;
    TextView userfragFullname, userfragEmail, userfragFullnameforlogo;
    ImageView profile;
    byte[] imagebyte;

    private Button edit_profile_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userfragFullname = view.findViewById(R.id.userfragfullname);
        userfragEmail = view.findViewById(R.id.userfragEmail);
        userfragFullnameforlogo = view.findViewById(R.id.tv_name);
        profile = view.findViewById(R.id.imgUserr);

        // edit profile
        edit_profile_btn = view.findViewById(R.id.edit_profile_btn);

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = userfragEmail.getText().toString(); // Get the email text
                String userName = userfragFullname.getText().toString(); // Get the email text
                Bundle bundle = new Bundle();
                bundle.putString("user_email", userEmail);
                manageUserFragment fragment = new manageUserFragment();
                fragment.setArguments(bundle);
                goToUpdateUser(fragment);
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Login", 0);
        value = sharedPreferences.getString("email", "null");
        db = new Database(getActivity());


        try {
            imagebyte = db.authfetchforimage(value);
            Bitmap ImageDataInBitmap = BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
            profile.setImageBitmap(ImageDataInBitmap);
        } catch (Exception ex) {

        }


        if (value != null) {
            String fullname = db.getfullname(value);
            userfragEmail.setText(value);
            userfragFullname.setText(fullname);
            userfragFullnameforlogo.setText(fullname);
        }

        profile.setOnClickListener(view1 -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                //for gallery
                SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Login", 0);
                value = sharedPreferences.getString("email", "null");
                profile.setImageURI(data.getData());
                int result = db.updateimage(convertImageViewToByteArray(profile), value);

                if (result < 0) {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void goToUpdateUser(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.f_container, fragment).commit();
    }

}