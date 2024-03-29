package com.example.suitcase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suitcase.Activity.MapActivity;
import com.example.suitcase.Activity.UpdateProductActivity;
import com.example.suitcase.Activity.SmsActivity;
import com.example.suitcase.Model.ProductDataModel;
import com.example.suitcase.R;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ProductDataModel> arrayList;

    public ProductListAdapter(Context context, ArrayList<ProductDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productDescription, productPrice, productQuantity;
        TextView productStatusBox;
        ImageView editButton, deleteButton, productImage, mapButton, smsButton;
        CardView productCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title_id);
            productDescription = itemView.findViewById(R.id.product_des_id);
            productPrice = itemView.findViewById(R.id.product_price_id);
            productQuantity = itemView.findViewById(R.id.product_quantity_id);
            productStatusBox = itemView.findViewById(R.id.productpurchasedstatus);
            productImage = itemView.findViewById(R.id.product_img_id);
            editButton = itemView.findViewById(R.id.productlistedit);
            productCardView = itemView.findViewById(R.id.cardview_id);
            mapButton = itemView.findViewById(R.id.productlistmap);
            smsButton = itemView.findViewById(R.id.productlistsms);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_product_lay, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDataModel pdm = arrayList.get(position);

        holder.productTitle.setText(pdm.getProductname());
        holder.productDescription.setText(pdm.getProductdescription());
        holder.productPrice.setText(String.valueOf(pdm.getProductprice()));
        holder.productQuantity.setText(String.valueOf(pdm.getProductquantity()));

        byte[] imageData = pdm.getProductimage();
        if (imageData != null && imageData.length > 0) {
            Bitmap imageDataInBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.productImage.setImageBitmap(imageDataInBitmap);
        } else {
            // Set a placeholder image if the image data is missing or empty
            holder.productImage.setImageResource(R.drawable.ic_product);
        }

        if (pdm.getProductstatus() < 0) {
            holder.productStatusBox.setText("No");
        } else {
            holder.productStatusBox.setText("Yes");
        }

        holder.mapButton.setOnClickListener(view -> {
            Intent mapIntent = new Intent(context, MapActivity.class);
            mapIntent.putExtra("productid", pdm.getProductid());
            mapIntent.putExtra("productname", pdm.getProductname());
            context.startActivity(mapIntent);
        });

        holder.smsButton.setOnClickListener(view -> {
            Toast.makeText(context,"Message",Toast.LENGTH_SHORT).show();
            Intent smsIntent = new Intent(context, SmsActivity.class);
            smsIntent.putExtra("productid", pdm.getProductid());
            context.startActivity(smsIntent);
        });

        holder.editButton.setOnClickListener(view -> {
            Intent editIntent = new Intent(context, UpdateProductActivity.class);
            editIntent.putExtra("productid", pdm.getProductid());
            editIntent.putExtra("pcid", pdm.getProductcategoryid());
            context.startActivity(editIntent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
