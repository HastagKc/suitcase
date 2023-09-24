package com.example.suitcase.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.telephony.SmsManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.suitcase.Database.Database;
import com.example.suitcase.Model.ProductDataModel;
import com.example.suitcase.R;

import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity {

    EditText smobile, stitle, sdes, squantity, sprice, slocation;
    Button btnsendsms;
    ImageView backimg;
    Integer pid;
    Double mlat, mlng;
    Geocoder geocoder;
    List<Address> address;
    Database db = new Database(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sms);
        //bind variable to layout tag

        //change notification color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.toolBarColor));
        }

        smobile = findViewById(R.id.smobile);
        stitle = findViewById(R.id.stitle);
        sdes = findViewById(R.id.sdescription);
        squantity = findViewById(R.id.squnatity);
        sprice = findViewById(R.id.sprice);
        btnsendsms = findViewById(R.id.btnsendsms);
        slocation = findViewById(R.id.slocation);
        backimg = findViewById(R.id.sbackimgf);

        try {
            pid = getIntent().getExtras().getInt("productid");
        } catch (Exception ex) {
            pid = -1; //Or some error status //
        }

        if (pid != -1) {
            settext();
        }


        //call SendMessage method
        btnsendsms.setOnClickListener(v -> {
            SendMessage();

        });

        backimg.setOnClickListener(view -> {
            startActivity(new Intent(SmsActivity.this, DashboardActivity.class));

        });

    }

    //SMS send method
    public void SendMessage() {
        String stringPhone = smobile.getText().toString().trim();
        String stringtitle = stitle.getText().toString().trim();
        String stringdes = sdes.getText().toString().trim();
        String stringquantity = squantity.getText().toString().trim();
        String stringprice = sprice.getText().toString().trim();
        String fullmessage = "title: " + stringtitle + "\n Description: " + stringdes + "\n quantity: " + stringquantity +
                "\n price: " + stringprice;


        if (stringPhone.equals("") || stringtitle.equals("") || stringquantity.equals("") || stringprice.equals("")) {
            Toast.makeText(this, "enter all field", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsman = SmsManager.getDefault();
            smsman.sendTextMessage(stringPhone, null, fullmessage, null, null);
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            smobile.getText().clear();
            stitle.getText().clear();
            sdes.getText().clear();
            squantity.getText().clear();
            sprice.getText().clear();
        }
    }

    public void settext() {
        ArrayList<ProductDataModel> pdm = db.productfetchdataformapload(pid);
      //  Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        stitle.setText(pdm.get(0).getProductname());
        sdes.setText(pdm.get(0).getProductdescription());
        squantity.setText(String.valueOf(pdm.get(0).getProductquantity()));
        sprice.setText(String.valueOf(pdm.get(0).getProductprice()));
        slocation.setText(pdm.get(0).getProductaddress());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SmsActivity.this, DashboardActivity.class);
        startActivity(intent);
    }
}