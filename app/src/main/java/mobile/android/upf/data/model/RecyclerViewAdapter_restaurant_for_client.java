package mobile.android.upf.data.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mobile.android.upf.R;

public class RecyclerViewAdapter_restaurant_for_client extends RecyclerView.Adapter<RecyclerViewAdapter_restaurant_for_client.MyViewHolder> {

    private Context mContext;
    private List<Restaurant> mData;

    public RecyclerViewAdapter_restaurant_for_client(Context mContext, List<Restaurant> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_restaurant_for_client, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_restaurant_name.setText(mData.get(position).getName());
        holder.tv_restaurant_address.setText(mData.get(position).getAddress());
        holder.tv_restaurant_description.setText(mData.get(position).getDescription());
        holder.tv_restaurant_phone.setText(mData.get(position).getPhone());
        holder.tv_restaurant_email.setText(mData.get(position).getEmail());


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_restaurant_name,tv_restaurant_address,tv_restaurant_description,tv_restaurant_phone,tv_restaurant_email ;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name_element_for_client);
            tv_restaurant_address = (TextView) itemView.findViewById(R.id.restaurant_address_element_for_client);
            tv_restaurant_description = (TextView) itemView.findViewById(R.id.restaurant_phone_element_for_client);
            tv_restaurant_phone = (TextView) itemView.findViewById(R.id.restaurant_email_element_for_client);
            tv_restaurant_email = (TextView) itemView.findViewById(R.id.restaurant_description_element_for_client);

        }
    }
}
