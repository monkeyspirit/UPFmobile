package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.RestaurantViewElementForClientActivity;
import mobile.android.upf.data.model.Notification;
import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.ui.admin.admin_homepage.AdminHomepageFragment;
import mobile.android.upf.ui.admin.admin_restaurants.AdminRestaurantsFragment;

public class RecyclerViewAdapter_admin_all_restaurants extends RecyclerView.Adapter<RecyclerViewAdapter_admin_all_restaurants.MyViewHolder>  {

    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Restaurant> mData;
    private DatabaseReference mDatabase;

    private String token;
    private EditText decline_msgEditText;

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    public RecyclerViewAdapter_admin_all_restaurants(Context mContext, List<Restaurant> mData, Fragment mFragment) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_admin_all_restaurants.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        this.parent = parent;
        view = mInflater.inflate(R.layout.cardview_item_restaurant_for_admin, parent,false);

        return new RecyclerViewAdapter_admin_all_restaurants.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_admin_all_restaurants.MyViewHolder holder, int position) {
        holder.tv_restaurant_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantViewElementForClientActivity.class);
                intent.putExtra("id", mData.get(position).getId());
                intent.putExtra("admin", true);
                mContext.startActivity(intent);
            }
        });
        holder.tv_restaurant_name.setText(mData.get(position).getName());
        holder.tv_restaurant_city.setText(mData.get(position).getCity());
        holder.tv_restaurant_address.setText(mData.get(position).getAddress());
        holder.tv_restaurant_description.setText(mData.get(position).getDescription());
        holder.tv_restaurant_phone.setText(mData.get(position).getPhone());
        holder.tv_restaurant_email.setText(mData.get(position).getEmail());
        String uriS = mData.get(position).getImageUrl();

        if (!uriS.equals("")) {
            Uri uri = Uri.parse(mData.get(position).getImageUrl());
            Log.d("firebase", "Image Url: " + uri);
            Glide.with(mContext).load(uri).into(holder.tv_restaurant_pic);
        }


        holder.tv_alert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog_styler_admin_restaurant, parent, false);
                decline_msgEditText = (EditText) viewInflated.findViewById(R.id.decline_msg_txtedit);

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        // set message, title, and icon
                        .setTitle(R.string.report)
                        .setView(viewInflated)
                        .setMessage(R.string.report_msg)
                        .setIcon(R.drawable.ic_baseline_report_24_black)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

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
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#D3D3D3"));



                decline_msgEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                        if (decline_msgEditText.getText().toString().length()>0){
                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#FF9800"));
                        } else {
                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#D3D3D3"));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String toDecline = mData.get(position).getId();
                        Log.d("R. decline id: ", toDecline);

                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                mDatabase.child("Restaurants").child(toDecline).child("status").setValue(2);


                                String msg = mData.get(position).getName()+" " + mContext.getString(R.string.notification_report_msg);

                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String date = sdf.format(cal.getTime());
                                SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
                                String time = sdf_time.format(cal.getTime());

                                Notification notification = new Notification(mData.get(position).getRestaurateur_id(),date, time, "1",msg);
                                mDatabase.child("Notifications").child(mData.get(position).getRestaurateur_id()).child(String.valueOf(notification.getId())).setValue(notification);

                                ((AdminRestaurantsFragment) mFragment).updateRecycler();

                                Toast.makeText(mContext, R.string.update, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("firebase", "Error while editing data from db in reject restaurant");
                            }

                        });
                        myQuittingDialogBox.dismiss();

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView tv_restaurant_card;
        TextView tv_restaurant_name, tv_restaurant_city, tv_restaurant_address,
                tv_restaurant_description, tv_restaurant_phone, tv_restaurant_email;
        ImageView tv_restaurant_pic;
        Button tv_yes_btn, tv_no_btn, tv_alert_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_restaurant_card = (CardView) itemView.findViewById(R.id.admin_restaurant_card);
            tv_restaurant_name = (TextView) itemView.findViewById(R.id.admin_restaurant_name_element);
            tv_restaurant_city = (TextView) itemView.findViewById(R.id.admin_restaurant_city_element);
            tv_restaurant_address = (TextView) itemView.findViewById(R.id.admin_restaurant_address_element);
            tv_restaurant_phone = (TextView) itemView.findViewById(R.id.admin_restaurant_phone_element);
            tv_restaurant_email = (TextView) itemView.findViewById(R.id.admin_restaurant_email_element);
            tv_restaurant_description = (TextView) itemView.findViewById(R.id.admin_restaurant_descpription_element);

            tv_restaurant_pic = (ImageView) itemView.findViewById(R.id.admin_restaurant_card_pic);


            tv_yes_btn = (Button) itemView.findViewById(R.id.admin_yes_restaurant_card_btn);
            tv_yes_btn.setVisibility(View.GONE);
            tv_no_btn = (Button) itemView.findViewById(R.id.admin_no_restaurant_card_btn);
            tv_no_btn.setVisibility(View.GONE);
            tv_alert_btn = (Button) itemView.findViewById(R.id.admin_alert_restaurant_card_btn);
            tv_alert_btn.setVisibility(View.VISIBLE);

        }
    }

}
