package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.Restaurant;

import static android.widget.Toast.LENGTH_LONG;

public class RecyclerViewAdapter_cart extends RecyclerView.Adapter<RecyclerViewAdapter_cart.MyViewHolder> {

    private Context mContext;
    private ViewGroup parent;
    private FirebaseAuth mAuth;

    private List<Dish> mData;
    private DatabaseReference mDatabase;


    public RecyclerViewAdapter_cart(Context mContext, List<Dish> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_cart.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_cart, parent, false);
        return new RecyclerViewAdapter_cart.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_cart.MyViewHolder holder, int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String current_id = mAuth.getCurrentUser().getUid();

        holder.tv_dish_name.setText(mData.get(position).getName());
        holder.tv_dish_number.setText(Integer.toString(mData.get(position).getNumber()));
        holder.tv_dish_price.setText(Double.toString(mData.get(position).getPrice()));

        holder.tv_dish_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Cart").child(current_id).child(mData.get(position).getId()).removeValue();
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_dish_name, tv_dish_number, tv_dish_price;
        Button tv_dish_delete;
        CardView tv_dish_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_dish_card = (CardView) itemView.findViewById(R.id.cart_card);

            tv_dish_name = (TextView) itemView.findViewById(R.id.cart_item_name);
            tv_dish_number = (TextView) itemView.findViewById(R.id.cart_item_number);
            tv_dish_price = (TextView) itemView.findViewById(R.id.cart_item_price);

            tv_dish_delete = (Button) itemView.findViewById(R.id.delete_cart_item_btn);
        }
    }
}
