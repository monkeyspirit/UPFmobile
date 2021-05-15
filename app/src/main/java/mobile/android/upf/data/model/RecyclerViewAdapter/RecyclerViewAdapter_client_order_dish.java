package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;

import static android.widget.Toast.LENGTH_LONG;

public class RecyclerViewAdapter_client_order_dish extends RecyclerView.Adapter<RecyclerViewAdapter_client_order_dish.MyViewHolder> {

    private Context mContext;
    private ViewGroup parent;
    private List<Dish> mData;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;


    public RecyclerViewAdapter_client_order_dish(Context mContext, List<Dish> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_client_order_dish.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        view = mInflater.inflate(R.layout.cardview_item_dish_client_order, parent,false);
        return new RecyclerViewAdapter_client_order_dish.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_client_order_dish.MyViewHolder holder, int position) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String current_id = mAuth.getCurrentUser().getUid();

        holder.tv_dish_number_add_picker.setMinValue(1);
        holder.tv_dish_number_add_picker.setMaxValue(15);
        holder.tv_dish_number_add_picker.setValue(1);

        final int[] numberDishes = {1};

        holder.tv_dish_number_add_picker.setOnValueChangedListener(new com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(com.shawnlin.numberpicker.NumberPicker picker, int oldVal, int newVal) {
                numberDishes[0] = picker.getValue();
            }
        });

        holder.tv_dish_name.setText(mData.get(position).getName());
        holder.tv_dish_description.setText(mData.get(position).getDescription());
        holder.tv_dish_price.setText(String.valueOf(mData.get(position).getPrice()));

        holder.tv_dish_add_dish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dish dish = new Dish(mData.get(position).getId(), mData.get(position).getName(), mData.get(position).getDescription(), mData.get(position).getRestaurant_id(), mData.get(position).getPrice(), numberDishes[0]);

                mDatabase.child("Cart").child(current_id).child(mData.get(position).getId()).setValue(dish).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.add_dish_to_cart_success, LENGTH_LONG).show();
                        } else {
                           Toast.makeText(mContext, R.string.add_dish_to_cart_failed, LENGTH_LONG).show();
                        }
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

            TextView tv_dish_name,tv_dish_description,tv_dish_price;
            CardView tv_dish_card;
            Button tv_dish_add_dish;
            com.shawnlin.numberpicker.NumberPicker tv_dish_number_add_picker;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_dish_card = (CardView) itemView.findViewById(R.id.dish_card_client_order);

                tv_dish_name = (TextView) itemView.findViewById(R.id.dish_name_element_client_order);
                tv_dish_description = (TextView) itemView.findViewById(R.id.dish_description_element_client_order);
                tv_dish_price = (TextView) itemView.findViewById(R.id.dish_price_element_client_order);

                tv_dish_add_dish = (Button) itemView.findViewById(R.id.add_dish_client_order_btn);
                tv_dish_number_add_picker = (com.shawnlin.numberpicker.NumberPicker) itemView.findViewById(R.id.dish_number_order_picker);
            }
    }
}
