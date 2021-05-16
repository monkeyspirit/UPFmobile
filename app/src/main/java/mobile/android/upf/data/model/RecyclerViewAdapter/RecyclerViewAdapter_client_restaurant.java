package mobile.android.upf.data.model.RecyclerViewAdapter;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.RestaurantViewElementForClientActivity;
import mobile.android.upf.data.model.Restaurant;

public class RecyclerViewAdapter_client_restaurant extends RecyclerView.Adapter<RecyclerViewAdapter_client_restaurant.MyViewHolder> {

    private Context mContext;
    private List<Restaurant> mData;

    public RecyclerViewAdapter_client_restaurant(Context mContext, List<Restaurant> mData) {
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

        holder.tv_restaurant_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantViewElementForClientActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                mContext.startActivity(intent);
            }
        });


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

        holder.call_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String number = String.format("tel:%s", mData.get(position).getPhone());
                callIntent.setData(Uri.parse(number));
                mContext.startActivity(callIntent);
            }
        });

        holder.email_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));

                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"address@mail.com"});
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                emailIntent.setSelector( emailSelectorIntent );
                mContext.startActivity(emailIntent);

            }
        });

        holder.nav_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navIntent = new Intent(Intent.ACTION_DIAL);
                Log.d("map", "Opening the map");
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

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


        }
    }
}
