package mobile.android.upf.data.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.RestaurantViewElementForClientActivity;

public class RecyclerViewAdapter_restaurant_for_client extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_for_client.MyViewHolder> {

    private Context mContext;
    private List<Restaurant> mData;

    public RecyclerViewAdapter_restaurant_for_client(Context mContext, List<Restaurant> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_restaurant_for_client, parent,false);
        return new MyViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_restaurant_name.setText(mData.get(position).getName());
        holder.tv_restaurant_address.setText(mData.get(position).getAddress());
        //holder.tv_restaurant_description.setText(mData.get(position).getDescription());
        holder.tv_restaurant_phone.setText(mData.get(position).getPhone());
        holder.tv_restaurant_email.setText(mData.get(position).getEmail());

        String uriS = mData.get(position).getImageUrl();

        if (uriS != "") {
            Uri uri = Uri.parse(mData.get(position).getImageUrl());
            Log.d("firebase", "Image Url: " + uri);
            Glide.with(mContext).load(uri).into(holder.tv_restaurant_pic);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView tv_restaurant_card;
        TextView tv_restaurant_name,tv_restaurant_address,tv_restaurant_phone,tv_restaurant_email;
        ImageView tv_restaurant_pic;
        Button call_restaurant_btn, email_restaurant_btn, nav_restaurant_btn;

        public MyViewHolder(@NonNull View itemView, Context mContext) {
            super(itemView);

            tv_restaurant_card = (CardView) itemView.findViewById(R.id.card_restaurant_client);

            tv_restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name_element_for_client);
            tv_restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address_element_for_client);
            //tv_restaurant_description = (TextView) itemView.findViewById(R.id.restaurant_description_element_for_client);
            tv_restaurant_phone = (TextView) itemView.findViewById(R.id.restaurant_phone_element_for_client);
            tv_restaurant_email = (TextView) itemView.findViewById(R.id.restaurant_email_element_for_client);

            tv_restaurant_pic = (ImageView) itemView.findViewById(R.id.restaurant_card_pic_for_client);

            call_restaurant_btn = (Button) itemView.findViewById(R.id.call_restaurant_btn);
            email_restaurant_btn = (Button) itemView.findViewById(R.id.email_restaurant_btn);
            nav_restaurant_btn = (Button) itemView.findViewById(R.id.nav_restaurant_btn);

            tv_restaurant_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Non funziona... non va bene il contesto
                    Intent intent = new Intent(v.getContext(), RestaurantViewElementForClientActivity.class);
                    mContext.startActivity(intent);
                }
            });

            call_restaurant_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent .setData(Uri.parse("tel:"+tv_restaurant_phone.getText().toString()));
                    mContext.startActivity(callIntent);
                }
            });

            email_restaurant_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_DIAL);
                    emailIntent.setData(Uri.parse("email:"+tv_restaurant_email.getText().toString()));
                    mContext.startActivity(emailIntent);
                }
            });

            nav_restaurant_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent navIntent = new Intent(Intent.ACTION_DIAL);
                    Log.d("map", "Opening the map");
                }
            });

        }
    }
}
