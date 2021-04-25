package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;

public class RecyclerViewAdapter_cart extends RecyclerView.Adapter<RecyclerViewAdapter_cart.MyViewHolder> {

    private Context mContext;
    private ViewGroup parent;
    private List<Dish> mData;
    private DatabaseReference mDatabase;


    @NonNull
    @Override
    public RecyclerViewAdapter_cart.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        view = mInflater.inflate(R.layout.cardview_item_cart, parent, false);
        return new RecyclerViewAdapter_cart.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_cart.MyViewHolder holder, int position) {

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
