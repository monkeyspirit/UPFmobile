package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;

public class RecyclerViewAdapter_restaurant_view_order extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_view_order.MyViewHolder>{

    private Context mContext;
    private Fragment mFragment;
    private List<Order> mData;

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
        view = mInflater.inflate(R.layout.cardview_item_order_restaurant, parent,false);
        return new RecyclerViewAdapter_restaurant_view_order.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_restaurant_view_order.MyViewHolder holder, int position) {

        holder.tv_order_id.setText(mData.get(position).getDishes_summary());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_order_id;
        Button tv_yes_btn, tv_no_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_order_id = (TextView) itemView.findViewById(R.id.restaurant_order_dishes_summary);

            tv_yes_btn = (Button) itemView.findViewById(R.id.order_restaurant_yes_card_btn);
            tv_no_btn = (Button) itemView.findViewById(R.id.order_restaurant_no_card_btn);

        }
    }


}
