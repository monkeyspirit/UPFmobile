package mobile.android.upf.data.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;

public class RecyclerViewAdapter_client_view_order extends RecyclerView.Adapter<RecyclerViewAdapter_client_view_order.MyViewHolder> {

    private Context mContext;
    private List<Order> mData;

    public RecyclerViewAdapter_client_view_order(Context mContext, List<Order> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_order_client, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_order_id.setText(mData.get(position).getDishes_summary());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_order_id;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_order_id = (TextView) itemView.findViewById(R.id.client_order_dishes_summary);


        }
    }
}
