package com.example.assignment4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private EditText itemNameEditText, itemQuantityEditText, itemPriceEditText;
    private Button addItemButton;

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

        // Initialize views for adding a new item
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemQuantityEditText = findViewById(R.id.itemQuantityEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        addItemButton = findViewById(R.id.addItemButton);

        // Fetch items from Firestore initially and set up real-time updates
        fetchItems();

        // Set up button to add new item
        addItemButton.setOnClickListener(v -> addItemToList());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Real-time update listener to get changes from Firestore
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

    private void addItemToList() {
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
                    itemNameEditText.setText("");
                    itemQuantityEditText.setText("");
                    itemPriceEditText.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(ShoppingListActivity.this, "Error adding item", Toast.LENGTH_SHORT).show());
    }

    public void deleteItem(DocumentSnapshot itemDocSnapshot) {
        // Get document reference for the item to be deleted
        DocumentReference itemDocRef = shoppingListRef.document(itemDocSnapshot.getId());

        // Delete the item from Firestore
        itemDocRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ShoppingListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(ShoppingListActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show());
    }
}
