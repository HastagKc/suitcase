package com.example.suitcase.Activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.suitcase.Database.Database;
import com.example.suitcase.Model.ProductDataModel;
import com.example.suitcase.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GoogleMap mgoogleMap;
    NetworkInfo networkInfo;
    ConnectivityManager manager;
    List<Address> address;
    String mAddress, knownName;
    Geocoder geocoder;
    TextView productaddcamera, productaddgallery;
    Button productaddbtn;
    ImageView productaddimage, backicon;
    EditText productaddname, productadddes, productaddquantity, productaddprice;
    final int CAMERA_CODE = 100;
    final int GALLERY_CODE = 200;
    Double lat, lng;
    int procatid;
    ProductDataModel productDataModel;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize category id value from Intent data
        procatid = getIntent().getExtras().getInt("pcid");

        // Change notification color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPurple));
        }

        // Initialize database connection
        Database db = new Database(this);


        // Initialize UI elements
        productaddbtn = findViewById(R.id.productaddbtnid);
        productaddname = findViewById(R.id.productaddtitleid);
        productaddquantity = findViewById(R.id.productaddquantityid);
        productaddprice = findViewById(R.id.productaddpriceid);
        productadddes = findViewById(R.id.productadddesid);
        productaddimage = findViewById(R.id.productaddimageid);
        productaddgallery = findViewById(R.id.productaddfromgallery);
        productaddcamera = findViewById(R.id.productaddfromcamera);
        backicon = findViewById(R.id.backimgf);

        // Add product image from gallery
        productaddgallery.setOnClickListener(v -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });

        // Add product image from camera
        productaddcamera.setOnClickListener(v -> {
            Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(icamera, CAMERA_CODE);
        });

        // Initialize map fragment and get user's location
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions here
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions are already granted
            getMyLocation();
            smf.getMapAsync(this);
        }

        // Add product data to the database
        productaddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =productaddname.getText().toString().trim();
                String price=productaddprice.getText().toString().trim();
                String quantity =productaddquantity.getText().toString().trim();
                if(name.equals("")|| price.equals("") ||quantity.equals("")){
                    Toast.makeText(AddProductActivity.this,"Empty data can't be added ",Toast.LENGTH_SHORT).show();
                }
                else {
                    addProductToDatabase();
                }
            }
        });
        // Navigate back to ProductListActivity
        backicon.setOnClickListener(v -> navigateToProductListActivity());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                // Handle image from camera
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                productaddimage.setImageBitmap(img);
            } else if (requestCode == GALLERY_CODE) {
                // Handle image from gallery
                productaddimage.setImageURI(data.getData());
            }
        }
    }

    public byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mgoogleMap = googleMap;
    }

    public void getMyLocation() {
        // Check and request location permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions here
            return;
        }

        // Get user's last known location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(l ->
                smf.getMapAsync(googleMap -> {
                    if (l != null) {
                        lat = l.getLatitude();
                        lng = l.getLongitude();
                        try {
                            getAddress(lat, lng);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mgoogleMap = googleMap;
                        mgoogleMap.setOnMapClickListener(lating -> {
                            mgoogleMap.clear();
                            checkConnection();
                            if (networkInfo.isConnected() && networkInfo.isAvailable()) {
                                lat = lating.latitude;
                                lng = lating.longitude;
                                try {
                                    getAddress(lat, lng);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(this, "Please Check Network", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Location not available. Please enable location services.", Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    // Check internet connectivity
    public void checkConnection() {
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }

    // Get address
    private void getAddress(double mlat, double mlng) throws IOException {
        geocoder = new Geocoder(this, Locale.getDefault());
        if (mlat != 0) {
            address = geocoder.getFromLocation(mlat, mlng, 1);
            if (address != null && !address.isEmpty()) {
                mAddress = address.get(0).getAddressLine(0);
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String Country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                knownName = address.get(0).getFeatureName();
                if (mAddress != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng lating = new LatLng(mlat, mlng);
                    markerOptions.position(lating).title(mAddress);
                    mgoogleMap.addMarker(markerOptions);
                    mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lating, 18));
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        navigateToProductListActivity();
    }

    private void addProductToDatabase() {
        Integer prdstatus = -1;
        productDataModel = new ProductDataModel();
        productDataModel.setProductimage(convertImageViewToByteArray(productaddimage));
        productDataModel.setProductname(productaddname.getText().toString());
        productDataModel.setProductquantity(Integer.parseInt(productaddquantity.getText().toString()));
        productDataModel.setProductprice(Double.parseDouble(productaddprice.getText().toString()));
        productDataModel.setProductdescription(productadddes.getText().toString());
        productDataModel.setProductstatus(prdstatus);
        productDataModel.setProductcategoryid(procatid);
        productDataModel.setProductlat(lat);
        productDataModel.setProductlong(lng);
        productDataModel.setProductaddress(mAddress);


        Database db = new Database(this);
        long result = db.productadd(productDataModel);
        if (result == -1) {
            Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product added successfully.", Toast.LENGTH_SHORT).show();
            navigateToProductListActivity();
        }
    }

    private void navigateToProductListActivity() {
        Intent intent = new Intent(AddProductActivity.this, ProductListActivity.class);
        intent.putExtra("pcid", procatid);
        startActivity(intent);
    }
}
