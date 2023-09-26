package com.example.suitcase.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.suitcase.Database.Database;
import com.example.suitcase.R;
import com.google.android.material.textfield.TextInputEditText;

public class manageUserFragment extends Fragment {


    private TextInputEditText first_name_et, last_name_et, email_et, password_et, cm_password_et;
    private Button update_user_btn, backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);


        backBtn = view.findViewById(R.id.back_btn);
        first_name_et = view.findViewById(R.id.first_name_et);
        last_name_et = view.findViewById(R.id.last_name_et);
        email_et = view.findViewById(R.id.email_et);
        password_et = view.findViewById(R.id.password_et);
        cm_password_et = view.findViewById(R.id.cm_password_et);

        update_user_btn = view.findViewById(R.id.update_user_btn);


        // Inside your fragment's onCreateView or any method
        Bundle args = getArguments();
        if (args != null) {
            String userEmail = args.getString("user_email");
            email_et.setText(userEmail);

        }


        update_user_btn.setOnClickListener(v -> {
            updateUser();
        });

        backBtn.setOnClickListener(v -> {
            replaceFragment(new UserFragment());
        });

        return view;
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.f_container, newFragment);
        fragmentTransaction.commit();
    }

    private void updateEmail(String newEmail) {
        // Use getActivity() to access the context and getSharedPreferences
        SharedPreferences sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", newEmail);
        editor.apply();
    }

    //   update user
    private void updateUser() {
        String fname = first_name_et.getText().toString().trim();
        String lname = last_name_et.getText().toString().trim();
        String email = email_et.getText().toString().trim();
        String pass = password_et.getText().toString().trim();
        String cmPass = cm_password_et.getText().toString().trim();

        if (email.isEmpty() || fname.isEmpty() || pass.isEmpty() || cmPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(cmPass)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            Database db = new Database(getContext());
            boolean updateSuccessful = db.updateUserByEmail(email, fname, lname, pass);

            if (updateSuccessful) {
                Toast.makeText(getActivity(), "Profile is Updated Successfully", Toast.LENGTH_SHORT).show();
                updateEmail(email);
                replaceFragment(new UserFragment());
            } else {
                Toast.makeText(getActivity(), "Profile Failed to Update", Toast.LENGTH_SHORT).show();
            }
        }
    }

}