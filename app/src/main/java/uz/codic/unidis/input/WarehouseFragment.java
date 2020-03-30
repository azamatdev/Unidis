package uz.codic.unidis.input;


import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import uz.codic.unidis.R;
import uz.codic.unidis.data.Warehouse;
import uz.codic.unidis.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WarehouseFragment extends Fragment {


    private Spinner brandSpinner, productSpinner;
    //    private EditText edtxtPrice;
    private EditText edtxtQuantity;
    private TextView warehouseQuantity;
    private FloatingActionButton fab;
//    private TextView warehousePrice;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    List<String> brandList;
    List<Product> productList;
    List<Warehouse> warehouseList;
    RelativeLayout btnAdd;

    ListenerRegistration brandsListener;
    ListenerRegistration productListener;
    ListenerRegistration warehouseListener;
    LottieAnimationView animSuccess;

    public WarehouseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warehouse, container, false);
        brandSpinner = view.findViewById(R.id.spinner_brands);
        productSpinner = view.findViewById(R.id.spinner_products);
//        edtxtPrice = view.findViewById(R.id.product_price);
        edtxtQuantity = view.findViewById(R.id.edtxt_product_quantity);
        btnAdd = view.findViewById(R.id.add_product_warehouse);
        warehouseQuantity = view.findViewById(R.id.warehouse_quantity);
        animSuccess = view.findViewById(R.id.anim_success_warehouse);
        fab = view.findViewById(R.id.fab_add_products);
        animSuccess.setSpeed(1.2f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brandList = new ArrayList<>();
        productList = new ArrayList<>();
        warehouseList = new ArrayList<>();


        getBrandName();

        getProductList();

        getWareHouseList();

        listenForBrandChange();

        listenForProductChange();

        addToWarehouse();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProduct = new Intent(getActivity(), AddProductActivity.class);
                startActivity(addProduct);
            }
        });


    }


    private void getBrandName() {
        brandsListener = firestore.collection("brands")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if (!brandList.isEmpty()) {
                            brandList.clear();
                        }
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            brandList.add((String) dc.getData().get("name"));
                        }
                        showBrandList(brandList);

                    }
                });
    }

    private void getProductList() {
        productListener = firestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if (!productList.isEmpty()) {
                            productList.clear();
                        }
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            Product product = new Product();
                            product.setProductId(dc.getId());
                            product.setName((String) dc.getData().get("name"));
                            product.setBrandName((String) dc.getData().get("brandName"));
                            productList.add(product);
                        }
                        showProductList(brandSpinner.getSelectedItem().toString());
                    }
                });
    }

    private void getWareHouseList() {
        warehouseListener = firestore.collection("warehouse")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if (!warehouseList.isEmpty()) {
                            warehouseList.clear();
                        }
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            Warehouse warehouse = dc.toObject(Warehouse.class);
                            assert warehouse != null;
                            warehouse.setProductId(dc.getId());
                            warehouseList.add(warehouse);
                        }

                        Product product = getSelectedProduct(productSpinner.getSelectedItem().toString());
                        if (product.getProductId() != null) {
                            if (getWarehouseObject(product.getProductId()) != null) {
                                updateWarehouseData(getWarehouseObject(product.getProductId()));
                            } else {
//                                warehousePrice.setText("");
                                warehouseQuantity.setText("");
                            }
                        }

                    }
                });
    }

    private void listenForBrandChange() {
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProductList(brandSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void listenForProductChange() {
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Product product = getSelectedProduct(productSpinner.getSelectedItem().toString());
                if (getWarehouseObject(product.getProductId()) != null) {
                    updateWarehouseData(getWarehouseObject(product.getProductId()));
                } else {
//                    warehousePrice.setText("");
                    warehouseQuantity.setText("");
                }
//                edtxtPrice.setText("");
                edtxtQuantity.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addToWarehouse() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(getContext())) {
                    Product product;
                    //if quantity is specified
                    if (!edtxtQuantity.getText().toString().isEmpty()) {
                        //if the product is chosen
                        if (getSelectedProduct(productSpinner.getSelectedItem().toString()) != null) {
                            product = getSelectedProduct(productSpinner.getSelectedItem().toString());

                            //if product is already added to database
                            //need to update
                            if (getWarehouseObject(product.getProductId()) != null
                                    && !edtxtQuantity.getText().toString().equals(".")) {

//                                    Double total = (inputPrice * inputQuantity + warehousePrice * warehouseQuantity) /
//                                            (inputQuantity + warehouseQuantity);

                                long quantity = Long.parseLong(edtxtQuantity.getText().toString());
                                firestore.collection("warehouse").document(product.getProductId())
                                        .update("quantity", FieldValue.increment(quantity))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                animSuccess.setVisibility(View.VISIBLE);
                                                animSuccess.playAnimation();
                                                Toast.makeText(getActivity(), "Muavaffaqiyatli yangilandi!", Toast.LENGTH_SHORT).show();
                                                edtxtQuantity.setText("");
                                                animSuccess.addAnimatorListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        animSuccess.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                });
                                            }

                                        });
                            }
                            // the product is not added to database
                            else {
                                if (!edtxtQuantity.getText().toString().equals(".")) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("brandName", product.getBrandName());
                                    map.put("productName", product.getName());
                                    map.put("quantity", Integer.parseInt(edtxtQuantity.getText().toString()));
                                    map.put("inputPrice", 0.0);
                                    map.put("salesPrice", 0.0);
                                    firestore.collection("warehouse").document(product.getProductId())
                                            .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            animSuccess.setVisibility(View.VISIBLE);
                                            animSuccess.playAnimation();
                                            animSuccess.addAnimatorListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    Toast.makeText(getActivity(), "Muavaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show();
                                                    edtxtQuantity.setText("");
                                                    animSuccess.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animation) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animation) {

                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        } else
                            Toast.makeText(getActivity(), "Iltimos, maxsulotni tanlang", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Iltimos, maxsulot sonini kiriting!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showMessage("Internet aloqasi yo'q");
                }

            }
        });
    }

    private Warehouse getWarehouseObject(String productId) {
        for (Warehouse warehouse : warehouseList) {
            if (warehouse.getProductId().equals(productId))
                return warehouse;
        }
        return null;
    }

    private void updateWarehouseData(Warehouse warehouse) {
//        if (warehouse.getInputPrice() != 0.0)
////            warehousePrice.setText("");
//        else
////            warehousePrice.setText(warehouse.getInputPrice() + " $");
        warehouseQuantity.setText(warehouse.getQuantity() + " ta");
    }

    public Product getSelectedProduct(String name) {
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    private void showBrandList(List<String> brandList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(adapter);
    }

    private void showProductList(String brandName) {
        List<String> productNames = new ArrayList<>();

        for (Product product : productList) {
            if (product.getBrandName().equals(brandName))
                productNames.add(product.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);
    }

    public void showMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG)
                .setAction("Sozlamalar ", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.GREEN);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    @Override
    public void onStop() {
        warehouseListener.remove();
        productListener.remove();
        brandsListener.remove();
        super.onStop();

    }
}
