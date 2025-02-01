package com.example.mysecondclasshit.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mysecondclasshit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import model.Product;
import java.util.ArrayList;
import java.util.List;

public class FragmentTh extends Fragment {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_th, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        initializeViews(view);
        setupLogoutButton(view);
        loadProductsFromFirebase();

        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.charactersRecyclerView);
        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        addButton.setOnClickListener(v -> showAddProductDialog());
    }

    private void setupLogoutButton(View view) {
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Navigation.findNavController(v).navigate(R.id.action_fragmentTh_to_fragmentOne);
            Toast.makeText(getContext(), "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductsFromFirebase() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        databaseRef.child(currentUserEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    Product product = productSnap.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(productSnap.getKey());
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת המוצרים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);

        EditText nameInput = dialogView.findViewById(R.id.productNameInput);
        EditText quantityInput = dialogView.findViewById(R.id.productQuantityInput);

        builder.setView(dialogView)
                .setTitle("הוספת מוצר חדש")
                .setPositiveButton("הוסף", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String quantityStr = quantityInput.getText().toString().trim();

                    if (!name.isEmpty() && !quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            addProductToFirebase(name, quantity);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "אנא הזן כמות תקינה", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("בטל", null)
                .show();
    }

    private void addProductToFirebase(String name, int quantity) {
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        String productId = databaseRef.child(currentUserEmail).push().getKey();

        Product product = new Product(name, quantity, currentUserEmail);
        product.setProductId(productId);

        if (productId != null) {
            databaseRef.child(currentUserEmail).child(productId).setValue(product)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), "המוצר נוסף בהצלחה", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "שגיאה בהוספת המוצר", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteProduct(Product product) {
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        if (product.getProductId() != null) {
            databaseRef.child(currentUserEmail).child(product.getProductId()).removeValue()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getContext(), "המוצר נמחק בהצלחה", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "שגיאה במחיקת המוצר", Toast.LENGTH_SHORT).show());
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> products;
        private List<Product> filteredProducts;

        ProductAdapter(List<Product> products) {
            this.products = products;
            this.filteredProducts = new ArrayList<>(products);
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_item, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = filteredProducts.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return filteredProducts.size();
        }

        void filter(String query) {
            filteredProducts.clear();
            if (query.isEmpty()) {
                filteredProducts.addAll(products);
            } else {
                for (Product product : products) {
                    if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredProducts.add(product);
                    }
                }
            }
            notifyDataSetChanged();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            private final TextView nameText;
            private final TextView quantityText;
            private final ImageButton deleteButton;

            ProductViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                quantityText = itemView.findViewById(R.id.quantityText);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }

            void bind(Product product) {
                nameText.setText(product.getName());
                quantityText.setText("כמות: " + product.getQuantity());
                deleteButton.setOnClickListener(v -> deleteProduct(product));
            }
        }
    }
}