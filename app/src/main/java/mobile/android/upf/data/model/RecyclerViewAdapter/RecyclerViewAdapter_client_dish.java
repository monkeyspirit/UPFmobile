package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;

public class RecyclerViewAdapter_client_dish extends RecyclerView.Adapter<RecyclerViewAdapter_client_dish.MyViewHolder> {

    private Context mContext;
    private ViewGroup parent;
    private List<Dish> mData;
    private DatabaseReference mDatabase;

    public RecyclerViewAdapter_client_dish(Context mContext, List<Dish> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter_client_dish.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        this.parent = parent;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        view = mInflater.inflate(R.layout.cardview_item_dish_client, parent,false);
        return new RecyclerViewAdapter_client_dish.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_client_dish.MyViewHolder holder, int position) {
        holder.tv_dish_name.setText(mData.get(position).getName());
        holder.tv_dish_description.setText(mData.get(position).getDescription());
        holder.tv_dish_price.setText(String.valueOf(mData.get(position).getPrice()));
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_dish_name,tv_dish_description,tv_dish_price;
        CardView tv_dish_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_dish_card = (CardView) itemView.findViewById(R.id.dish_card_client);

            tv_dish_name = (TextView) itemView.findViewById(R.id.dish_name_element_client);
            tv_dish_description = (TextView) itemView.findViewById(R.id.dish_description_element_client);
            tv_dish_price = (TextView) itemView.findViewById(R.id.dish_price_element_client);

        }
    }
}
