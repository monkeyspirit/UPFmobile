package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Restaurant;

public class RecyclerViewAdapter_delivery_restaurant extends RecyclerView.Adapter<RecyclerViewAdapter_delivery_restaurant.MyViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private List<Restaurant> mData;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_delivery_restaurant(Context mContext, Fragment mFragment, List<Restaurant> mData) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mData = mData;
    }

    public RecyclerViewAdapter_delivery_restaurant(Context mContext, List<Restaurant> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_restaurant_delivery, parent,false);
        return new MyViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String current_id = mAuth.getCurrentUser().getUid();

        holder.tv_restaurant_name.setText(mData.get(position).getName());
        holder.tv_restaurant_address.setText(mData.get(position).getAddress());
        holder.tv_restaurant_description.setText(mData.get(position).getDescription());
        holder.tv_restaurant_phone.setText(mData.get(position).getPhone());
        holder.tv_restaurant_email.setText(mData.get(position).getEmail());

        mDatabase.child("Users").child(current_id).child("Subscriptions").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Iterable<DataSnapshot> subscription_database = task.getResult().getChildren();


                    if (!task.getResult().exists()){
                        holder.subscribe_btn.setVisibility(View.VISIBLE);
                    }

                    ArrayList<String> view = new ArrayList<>();
                    for (DataSnapshot subscription : subscription_database) {

                        if(!view.contains(mData.get(position).getId()) & subscription.getKey().equals(mData.get(position).getId())){
                            holder.unsubscribe_btn.setVisibility(View.VISIBLE);
                            holder.subscribe_btn.setVisibility(View.INVISIBLE);
                           view.add(mData.get(position).getId());
                        }
                        else if(!view.contains(mData.get(position).getId())){
                            Log.d("Key sub: ", subscription.getKey());
                            holder.unsubscribe_btn.setVisibility(View.INVISIBLE);
                            holder.subscribe_btn.setVisibility(View.VISIBLE);

                        }
                    }
                }


            }

        });


       holder.subscribe_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mDatabase.child("Users").child(current_id).child("Subscriptions").child(mData.get(position).getId()).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {
                           holder.unsubscribe_btn.setVisibility(View.VISIBLE);
                           holder.subscribe_btn.setVisibility(View.INVISIBLE);
                           mData.remove(position);
                           notifyItemRemoved(position);
                           notifyItemRangeChanged(position, mData.size());
                       } else {
//                           Toast.makeText(mContext, mContext.getString(R.string.restaurant_registration_db_failed), LENGTH_LONG).show();
                       }
                   }
               });

               mDatabase.child("Restaurants").child(mData.get(position).getId()).child("Subscribers").child(current_id).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {

//                           Toast.makeText(mContext, mContext.getString(R.string.restaurant_registration_db_s), LENGTH_LONG).show();
                       } else {
//                           Toast.makeText(mContext, mContext.getString(R.string.restaurant_registration_db_failed), LENGTH_LONG).show();
                       }
                   }
               });

           }
       });

        holder.unsubscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Users").child(current_id).child("Subscriptions").child(mData.get(position).getId()).removeValue();
                mDatabase.child("Restaurants").child(mData.get(position).getId()).child("Subscribers").child(current_id).removeValue();
                holder.unsubscribe_btn.setVisibility(View.INVISIBLE);
                holder.subscribe_btn.setVisibility(View.VISIBLE);

                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
            }
        });

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

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CardView tv_restaurant_card;
        TextView tv_restaurant_name,tv_restaurant_address,tv_restaurant_phone,tv_restaurant_email, tv_restaurant_description;
        ImageView tv_restaurant_pic;
        Button subscribe_btn, unsubscribe_btn;


        public MyViewHolder(@NonNull View itemView, Context mContext) {
            super(itemView);

            tv_restaurant_card = (CardView) itemView.findViewById(R.id.delivery_restaurant_card);

            tv_restaurant_name = (TextView) itemView.findViewById(R.id.delivery_restaurant_name_element);
            tv_restaurant_address = (TextView) itemView.findViewById(R.id.delivery_restaurant_address_element);
            tv_restaurant_description = (TextView) itemView.findViewById(R.id.delivery_restaurant_descpription_element);
            tv_restaurant_phone = (TextView) itemView.findViewById(R.id.delivery_restaurant_phone_element);
            tv_restaurant_email = (TextView) itemView.findViewById(R.id.delivery_restaurant_email_element);

            tv_restaurant_pic = (ImageView) itemView.findViewById(R.id.delivery_restaurant_card_pic);

            subscribe_btn = (Button) itemView.findViewById(R.id.delivery_subscription_restaurant_card_btn);
            unsubscribe_btn = (Button) itemView.findViewById(R.id.delivery_unsubscription_restaurant_card_btn);

        }
    }
}
