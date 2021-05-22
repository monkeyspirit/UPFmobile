package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Notification;
import mobile.android.upf.ui.client.client_notifications.ClientNotificationsFragment;
import mobile.android.upf.ui.restaurant.restaurant_notifications.RestaurantNotificationsFragment;

public class RecyclerViewAdapter_restaurant_notification extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_notification.MyViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Notification> mData;
    private DatabaseReference mDatabase;

    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;

    public RecyclerViewAdapter_restaurant_notification(Context mContext, Fragment mFragment, List<Notification> mData) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_restaurant_notification.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        this.parent = parent;
        view = mInflater.inflate(R.layout.cardview_item_notification, parent,false);
        return new RecyclerViewAdapter_restaurant_notification.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_notification_date.setText(mData.get(position).getDate());
        holder.tv_notification_message.setText(mData.get(position).getContent());
        holder.tv_notification_time.setText(mData.get(position).getTime());


        holder.tv_notification_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.confirm_delete)
                        .setMessage(R.string.confirm_notification_delete)
                        .setIcon(R.drawable.ic_baseline_delete_24_black)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Id utente
                                String UserId = mAuth.getCurrentUser().getUid();
                                //Id notifica
                                String toDeleteId = mData.get(position).getId();
                                Log.d("Order to delete id: ", toDeleteId);


                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        mDatabase.child("Notifications").child(UserId).child(toDeleteId).removeValue();


                                        ((RestaurantNotificationsFragment) mFragment).updateRecycler();

                                        Toast.makeText(mContext, R.string.notification_deleted, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                        Log.e("firebase", "Error while removing data from db");
                                    }
                                });

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
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
                myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_notification_message, tv_notification_date, tv_notification_time;
        CardView tv_notification_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notification_card = (CardView) itemView.findViewById(R.id.notification_card);

            tv_notification_date = (TextView) itemView.findViewById(R.id.notification_date);
            tv_notification_time = (TextView) itemView.findViewById(R.id.notification_time);
            tv_notification_message = (TextView) itemView.findViewById(R.id.notification_message);


        }
    }


}
