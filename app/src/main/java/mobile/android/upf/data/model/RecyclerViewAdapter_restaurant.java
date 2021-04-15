package mobile.android.upf.data.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.android.upf.AddRestaurantActivity;
import mobile.android.upf.R;
import mobile.android.upf.RestaurantViewElementActivity;
import mobile.android.upf.ui.restaurant.restaurant_restaurants.RestaurantRestaurantsFragment;

import static android.graphics.Color.RED;

public class RecyclerViewAdapter_restaurant extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant.MyViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Restaurant> mData;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_restaurant(Context mContext, List<Restaurant> mData, Fragment mFragment) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.parent = parent;
        view = mInflater.inflate(R.layout.cardview_item_restaurant, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_restaurant_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code to go to edit activity
                Intent intent = new Intent(mContext, RestaurantViewElementActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        holder.tv_restaurant_name.setText(mData.get(position).getName());
        holder.tv_restaurant_address.setText(mData.get(position).getAddress());
//        holder.tv_restaurant_description.setText(mData.get(position).getDescription());
        holder.tv_restaurant_phone.setText(mData.get(position).getPhone());
        holder.tv_restaurant_email.setText(mData.get(position).getEmail());
        String uriS = mData.get(position).getImageUrl();

        if (uriS != "") {
            Uri uri = Uri.parse(mData.get(position).getImageUrl());
            Log.d("firebase", "Image Url: " + uri);
            Glide.with(mContext).load(uri).into(holder.tv_restaurant_pic);
        }

//        Ho paura che il colore rosso di sfondo possa dare idee sbagliate all'utente: rosso = ho sbagliato. Quindi per ora lo commento
//        Magari metterei due zone: zona approvati + zona non approvati
//        if (mData.get(position).getStatus() == 0) {
//            holder.tv_restaurant_card.setBackgroundColor(Color.parseColor("#ffa1a1"));
//        }

        holder.tv_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        // set message, title, and icon
                        .setTitle(R.string.confirm_delete)
                        .setMessage(R.string.confirm_delete)
                        .setIcon(R.drawable.ic_baseline_delete_24_black)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                String toDeleteId = mData.get(position).getId();
                                Log.d("To delete id: ", toDeleteId);

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mDatabase.child("Restaurants").child(toDeleteId).setValue(null);
                                        ((RestaurantRestaurantsFragment)mFragment).updateRecycler();
                                        Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("firebase", "Error while removing data from db");
                                    }
                                });


                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .create();
                myQuittingDialogBox.show();

                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#6200EE"));

            }
        });

        holder.tv_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog_styler_restaurant_modify, parent, false);
                final EditText name = (EditText) viewInflated.findViewById(R.id.modify_restaurant_name_txedit);
                name.setHint(mData.get(position).getName());
                final EditText description = (EditText) viewInflated.findViewById(R.id.modify_restaurant_description_txedit);
                description.setHint(mData.get(position).getDescription());
                final EditText email = (EditText) viewInflated.findViewById(R.id.modify_restaurant_email_txedit);
                email.setHint(mData.get(position).getEmail());
                final EditText address = (EditText) viewInflated.findViewById(R.id.modify_restaurant_address_txedit);
                address.setHint(mData.get(position).getAddress());
                final EditText phone = (EditText) viewInflated.findViewById(R.id.modify_restaurant_phone_txedit);
                phone.setHint(mData.get(position).getPhone());

//                final CircularImageView restaurant_pic = (CircularImageView) viewInflated.findViewById(R.id.restaurant_pic_modify);
//                restaurant_pic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        choosePicture();
//                    }
//                });

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        // set message, title, and icon
                        .setTitle(R.string.edit)
                        .setView(viewInflated)
                        .setIcon(R.drawable.ic_baseline_edit_24_black)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                String toEditId = mData.get(position).getId();
                                Log.d("Restaurant to edit id: ", toEditId);

                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String update_name, update_description, update_address, update_phone, update_email;

                                        if(!name.getText().toString().equals("")){
                                            update_name = name.getText().toString();
                                        }
                                        else {
                                            update_name = mData.get(position).getName();
                                        }

                                        if(!description.getText().toString().equals("")){
                                            update_description = description.getText().toString();
                                        }
                                        else {
                                            update_description = mData.get(position).getDescription();
                                        }

                                        if(!email.getText().toString().equals("")){
                                            update_email = email.getText().toString();
                                        }
                                        else {
                                            update_email = mData.get(position).getEmail();
                                        }

                                        if(!address.getText().toString().equals("")){
                                            update_address = address.getText().toString();
                                        }
                                        else {
                                            update_address = mData.get(position).getAddress();
                                        }

                                        if(!phone.getText().toString().equals("")){
                                            update_phone = phone.getText().toString();
                                        }
                                        else {
                                            update_phone = mData.get(position).getPhone();
                                        }

                                        Restaurant update = new Restaurant(toEditId, update_name, update_description, update_email, update_address, update_phone, mData.get(position).getRestaurateur_id(), mData.get(position).getImageUrl(), mData.get(position).getStatus());
                                        mDatabase.child("Restaurants").child(toEditId).setValue(update);

                                        ((RestaurantRestaurantsFragment)mFragment).updateRecycler();

                                        Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("firebase", "Error while editing data from db");
                                    }
                                });


                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .create();



                myQuittingDialogBox.show();

                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#6200EE"));

            }
        });

        holder.tv_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code to go to edit activity
                Intent intent = new Intent(mContext, RestaurantViewElementActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_restaurant_name,tv_restaurant_address,tv_restaurant_description,tv_restaurant_phone,tv_restaurant_email;
        ImageView tv_restaurant_pic;
        CardView tv_restaurant_card;
        Button tv_delete_btn, tv_edit_btn, tv_view_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_restaurant_card = (CardView) itemView.findViewById(R.id.restaurant_card);

            tv_restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name_element);
            tv_restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address_element);
            tv_restaurant_phone = (TextView) itemView.findViewById(R.id.restaurant_phone_element);
            tv_restaurant_email = (TextView) itemView.findViewById(R.id.restaurant_email_element);
//            tv_restaurant_description = (TextView) itemView.findViewById(R.id.restaurant_description_element);

            tv_restaurant_pic = (ImageView) itemView.findViewById(R.id.restaurant_card_pic);

            tv_delete_btn = (Button) itemView.findViewById(R.id.delete_restaurant_card_btn);
            tv_edit_btn = (Button) itemView.findViewById(R.id.edit_restaurant_card_btn);
            tv_view_btn = (Button) itemView.findViewById(R.id.view_restaurant_card_btn);
        }
    }




}
