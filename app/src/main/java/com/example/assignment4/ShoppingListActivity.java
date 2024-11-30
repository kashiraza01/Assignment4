package com.example.assignment4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference shoppingListRef;
    private View fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        shoppingListRef = db.collection("shopping_list");

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set layout manager
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        // Initialize Floating Action Button (FAB)
        fabAddItem = findViewById(R.id.fabAddItem);
        fabAddItem.setOnClickListener(v -> showAddItemDialog());

        // Fetch items from Firestore initially and set up real-time updates
        fetchItems();

        // Real-time listener to get updates from Firestore
        shoppingListRef.addSnapshotListener(this, (QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
            if (e != null) {
                Toast.makeText(ShoppingListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear and refresh item list with real-time updates
            itemList.clear();
            for (DocumentSnapshot document : snapshots.getDocuments()) {
                Item item = document.toObject(Item.class);
                if (item != null) {
                    itemList.add(item);
                }
            }
            itemAdapter.notifyDataSetChanged();
        });
    }

    private void fetchItems() {
        // Fetch items initially
        shoppingListRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        itemList.clear();
                        for (DocumentSnapshot document : querySnapshot) {
                            Item item = document.toObject(Item.class);
                            itemList.add(item);
                        }
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShoppingListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddItemDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.add_item_dialog, null);
        EditText itemNameEditText = dialogView.findViewById(R.id.itemNameEditText);
        EditText itemQuantityEditText = dialogView.findViewById(R.id.itemQuantityEditText);
        EditText itemPriceEditText = dialogView.findViewById(R.id.itemPriceEditText);
        Button saveItemButton = dialogView.findViewById(R.id.saveItemButton);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set button click listener
        saveItemButton.setOnClickListener(v -> {
            String itemName = itemNameEditText.getText().toString().trim();
            String itemQuantity = itemQuantityEditText.getText().toString().trim();
            String itemPrice = itemPriceEditText.getText().toString().trim();

            if (itemName.isEmpty() || itemQuantity.isEmpty() || itemPrice.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new Item object
            Item newItem = new Item(itemName, Integer.parseInt(itemQuantity), Double.parseDouble(itemPrice));

            // Add item to Firestore
            shoppingListRef.add(newItem)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ShoppingListActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ShoppingListActivity.this, "Error adding item", Toast.LENGTH_SHORT).show());
        });

        // Show the dialog
        dialog.show();
    }
}
