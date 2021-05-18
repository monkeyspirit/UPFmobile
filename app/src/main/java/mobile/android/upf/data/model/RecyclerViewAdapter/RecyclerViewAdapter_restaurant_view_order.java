package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaActionSound;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.Notification;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RestStats;
import mobile.android.upf.data.model.Restaurant;

public class RecyclerViewAdapter_restaurant_view_order extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_view_order.MyViewHolder>{

    private Context mContext;
    private Fragment mFragment;
    private List<Order> mData;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_restaurant_view_order(Context mContext, List<Order> mData, Fragment mFragment) {
        this.mContext = mContext;
        this.mData = mData;
        this.mFragment = mFragment;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_restaurant_view_order.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        view = mInflater.inflate(R.layout.cardview_item_order_restaurant, parent,false);
        return new RecyclerViewAdapter_restaurant_view_order.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_restaurant_view_order.MyViewHolder holder, int position) {
        holder.tv_order_address.setText(mData.get(position).getAddress());
        holder.tv_order_summary.setText(mData.get(position).getDishes_summary());

        if(mData.get(position).getState() == 1) {
            holder.tv_yes_btn.setVisibility(View.VISIBLE);
            holder.tv_no_btn.setVisibility(View.VISIBLE);

            holder.tv_yes_btn.setEnabled(true);
            holder.tv_yes_btn.setBackgroundColor(Color.GREEN);
            holder.tv_no_btn.setEnabled(true);
            holder.tv_no_btn.setBackgroundColor(Color.RED);


            holder.tv_yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                2 = ORDINE ACCETTATO
                    mDatabase.child("Orders").child(mData.get(position).getId()).child("state").setValue(2);

                    String msg = mData.get(position).getRestaurant_name()+"\n"+mContext.getString(R.string.msg_notification_order_accepted);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf.format(cal.getTime());
                    SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
                    String time = sdf_time.format(cal.getTime());

                    Notification notification = new Notification(mData.get(position).getUser_id(),date, time, "1",msg);
                    mDatabase.child("Notifications").child(mData.get(position).getUser_id()).child(String.valueOf(notification.getId())).setValue(notification);

                    HashMap<String, Integer> lstDishesInOrder = new HashMap<String, Integer>();

                    mDatabase.child("Orders").child(mData.get(position).getId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {

                            DataSnapshot dishes_snapshot = task.getResult().child("dishes");;
                            Log.d("ORDINE", String.valueOf(task.getResult().child("dishes").getValue()));

                            for (DataSnapshot dish: dishes_snapshot.getChildren()) {
                                String name = String.valueOf(dish.child("name").getValue());
                                int number = Integer.parseInt(String.valueOf(dish.child("number").getValue()));
                                Log.d("PIATTO", name+" "+number);
                                lstDishesInOrder.put(name, number);
                            }

                            for (Map.Entry<String, Integer> entry : lstDishesInOrder.entrySet()) {
                                String name = entry.getKey();
                                final Integer[] number = {entry.getValue()};
                                mDatabase.child("RestStats").child(mData.get(position).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("number").getValue() != null) {
                                            int oldNumber = Integer.parseInt(String.valueOf(dataSnapshot.child("number").getValue()));
                                            number[0] = number[0] + oldNumber;
                                        }

                                        RestStats restStats = new RestStats(name, number[0]);
                                        mDatabase.child("RestStats").child(mData.get(position).getUser_id()).child(name).setValue(restStats);
                                    }
                                });
                            }
                        }
                    });

                    Toast.makeText(mContext, R.string.order_accepted, Toast.LENGTH_SHORT).show();
                }
            });
            holder.tv_no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                -1 = ORDINE NON ACCETTATO
                    mDatabase.child("Orders").child(mData.get(position).getId()).child("state").setValue(-1);

                    String msg = mContext.getString(R.string.msg_notification_order_not_accepted);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String date = sdf.format(cal.getTime());
                    SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
                    String time = sdf_time.format(cal.getTime());

                    Notification notification = new Notification(mData.get(position).getUser_id(),date, time, "1",msg);
                    mDatabase.child("Notifications").child(mData.get(position).getUser_id()).child(String.valueOf(notification.getId())).setValue(notification);

                    Toast.makeText(mContext, R.string.order_rejected, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(mData.get(position).getState()==2) {
            holder.tv_complete_btn.setVisibility(View.VISIBLE);
            holder.tv_complete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 3 = ORDINE COMPLETATO E PRONTO PER LA CONSEGNA
                    mDatabase.child("Orders").child(mData.get(position).getId()).child("state").setValue(3);

//                    String msg = mContext.getString(R.string.msg_notification_order_ready_to_delivery);
//
//                    Calendar cal = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    String date = sdf.format(cal.getTime());
//                    SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
//                    String time = sdf_time.format(cal.getTime());
//
//                    Notification notification = new Notification(mData.get(position).getUser_id(),date, time, "1",msg);
//                    mDatabase.child("Notifications").child(mData.get(position).getUser_id()).child(String.valueOf(notification.getId())).setValue(notification);

                    Toast.makeText(mContext, R.string.order_completed, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(mData.get(position).getState()==3) {
            holder.tv_order_wait_for_shipping.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_order_summary, tv_order_address, tv_order_wait_for_shipping;
        Button tv_yes_btn, tv_no_btn, tv_complete_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_order_summary = (TextView) itemView.findViewById(R.id.restaurant_order_dishes_summary);
            tv_order_address = (TextView) itemView.findViewById(R.id.restaurant_order_address);
            tv_order_wait_for_shipping = (TextView) itemView.findViewById(R.id.restaurant_order_shipping);

            tv_yes_btn = (Button) itemView.findViewById(R.id.order_restaurant_yes_card_btn);
            tv_no_btn = (Button) itemView.findViewById(R.id.order_restaurant_no_card_btn);
            tv_complete_btn = (Button) itemView.findViewById(R.id.order_restaurant_complete_card_btn);
        }
    }


}
