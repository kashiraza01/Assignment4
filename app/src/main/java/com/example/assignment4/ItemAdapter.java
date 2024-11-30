package com.example.assignment4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private OnItemDeleteListener onItemDeleteListener;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // ViewHolder class to hold individual item views
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemQuantityTextView, itemPriceTextView;
        Button deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item currentItem = itemList.get(position);

        holder.itemNameTextView.setText(currentItem.getName());
        holder.itemQuantityTextView.setText("Quantity: " + currentItem.getQuantity());
        holder.itemPriceTextView.setText("Price: $" + currentItem.getPrice());

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemDeleteListener != null) {
                onItemDeleteListener.onItemDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }
}
