package com.example.assignment4;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;  // For generating random ID

public class ShoppingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference shoppingListRef;
    private View fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
            return;
        }


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

        // Set the delete listener
        itemAdapter.setOnItemDeleteListener(position -> {
            // Call the deleteItem method
            deleteItem(position);
        });

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
                    item.setId(document.getId());  // Ensure the ID is set
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
                        itemList.clear();  // Clear list to prevent duplicates
                        for (DocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            if (item != null) {
                                // Set a random ID for items fetched from Firestore (to make the ID unique on client-side)
                                item.setId(UUID.randomUUID().toString());
                                itemList.add(item);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();  // Notify adapter to refresh view
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

            // Create a new Item object with a random ID
            Item newItem = new Item(itemName, Integer.parseInt(itemQuantity), Double.parseDouble(itemPrice));
            newItem.setId(UUID.randomUUID().toString());  // Set a random ID for the item

            // Add item to Firestore
            shoppingListRef.add(newItem)
                    .addOnSuccessListener(documentReference -> {
                        // Add the item to the local list
                        itemList.add(newItem);
                        itemAdapter.notifyItemInserted(itemList.size() - 1);  // Notify the adapter

                        Toast.makeText(ShoppingListActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ShoppingListActivity.this, "Error adding item", Toast.LENGTH_SHORT).show());
        });

        // Show the dialog
        dialog.show();
    }

    private void deleteItem(int position) {
        Item itemToDelete = itemList.get(position);

        // Ensure the item has a valid ID
        if (itemToDelete.getId() == null || itemToDelete.getId().isEmpty()) {
            Toast.makeText(ShoppingListActivity.this, "Item ID is null or empty, cannot delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the ID to ensure it's not null or empty
        Log.d("ShoppingListActivity", "Deleting item with ID: " + itemToDelete.getId());

        // Get the document reference for the item using its unique ID
        DocumentReference docRef = shoppingListRef.document(itemToDelete.getId());

        // Log the document reference
        Log.d("ShoppingListActivity", "Document reference: " + docRef.getPath());

        // Delete the item from Firestore
        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted from Firestore
                    itemList.remove(position); // Remove item from local list
                    itemAdapter.notifyItemRemoved(position); // Notify adapter to remove item from view
                    Toast.makeText(ShoppingListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(ShoppingListActivity.this, "Error deleting item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ShoppingListActivity", "Error deleting item", e);
                });
    }



}
