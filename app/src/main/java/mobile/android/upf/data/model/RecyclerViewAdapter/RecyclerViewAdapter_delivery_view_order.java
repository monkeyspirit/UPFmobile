package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Color;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;

public class RecyclerViewAdapter_delivery_view_order extends RecyclerView.Adapter<RecyclerViewAdapter_delivery_view_order.MyViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private List<Order> mData;
    private ViewGroup parent;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_delivery_view_order(Context mContext, List<Order> mData, Fragment mFragment) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        view = mInflater.inflate(R.layout.cardview_item_order_delivery, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_order_summary.setText(mData.get(position).getDishes_summary());
        holder.tv_order_address.setText(mData.get(position).getAddress());
        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();
                    for (DataSnapshot restaurant: restaurants_database) {
                        if (mData.get(position).getRestaurant_id().equals(restaurant.getKey())) {
                            holder.tv_order_restaurant.setText(String.valueOf(restaurant.child("name").getValue()));
                        }
                    }
                }
            }
        });

        if(mData.get(position).getState() == 3){
            holder.tv_add_order_btn.setEnabled(true);
            holder.tv_add_order_btn.setBackgroundColor(Color.parseColor("#03A9F4"));
        }
        else{
            holder.tv_remove_order_btn.setEnabled(true);
            holder.tv_remove_order_btn.setBackgroundColor(Color.RED);
        }

        holder.tv_add_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                3 = ORDINE ACCETTATO DAL FATTORINO
                mDatabase.child("Orders").child(mData.get(position).getId()).child("state").setValue(4);
                Toast.makeText(mContext, "Ordine preso in carico.", Toast.LENGTH_SHORT).show();
                mDatabase.child("Users").child(currentUser.getUid()).child("busy").setValue(mData.get(position).getId());
            }
        });

        holder.tv_remove_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Orders").child(mData.get(position).getId()).child("state").setValue(2);
                Toast.makeText(mContext, "Ordine annullato.", Toast.LENGTH_SHORT).show();
                mDatabase.child("Users").child(currentUser.getUid()).child("busy").setValue(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_order_summary, tv_order_address, tv_order_restaurant;
        Button tv_add_order_btn, tv_remove_order_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_order_summary = (TextView) itemView.findViewById(R.id.delivery_order_dishes_summary);
            tv_order_address = (TextView) itemView.findViewById(R.id.delivery_order_address);
            tv_order_restaurant = (TextView) itemView.findViewById(R.id.delivery_order_restaurant_name);

            tv_add_order_btn = (Button) itemView.findViewById(R.id.delivery_add_order_btn);
            tv_remove_order_btn = (Button) itemView.findViewById(R.id.delivery_remove_order_btn);
        }
    }
}
