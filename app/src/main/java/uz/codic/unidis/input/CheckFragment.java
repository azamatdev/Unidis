package uz.codic.unidis.input;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.google.firebase.firestore.*;
import uz.codic.unidis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckFragment extends Fragment {


    private EditText edtxtBrandName, edtxtModelId;
    private Spinner brandNamesSpinner;
    LinearLayout addBrand, addModel;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    List<String> brands;
    List<Product> products;

    ListenerRegistration brandsListener;
    ListenerRegistration productListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        edtxtBrandName = view.findViewById(R.id.add_brand_name);
        edtxtModelId = view.findViewById(R.id.add_model_id);
        brandNamesSpinner = view.findViewById(R.id.spinner_brands);
        addBrand = view.findViewById(R.id.add_brand);
        addModel = view.findViewById(R.id.add_model);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brands = new ArrayList<>();
        products = new ArrayList<>();

        getBrandNames();

        getProductList();

        onAddBrandClick();

        onAddModelIdClick();
    }

    private void getProductList() {
       productListener =  firestore.collection("products")
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if (!products.isEmpty()) {
                            products.clear();
                        }
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            Product product = new Product();
                            product.setName((String) dc.getData().get("name"));
                            product.setBrandName((String) dc.getData().get("brandName"));
                            products.add(product);
                        }
                    }
                });
    }

    private void getBrandNames() {
        brandsListener = firestore.collection("brands")
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }
                        if (!brands.isEmpty()) {
                            brands.clear();
                        }
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                            brands.add((String) dc.getData().get("name"));
                            showBrandList(brands);
                        }
                    }
                });

    }

    private void showBrandList(List<String> brandList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, brandList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandNamesSpinner.setAdapter(adapter);
    }


    public void onAddBrandClick() {
        addBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtxtBrandName.getText().toString().isEmpty()) {
                    if (!isBrandAlreadyExists(edtxtBrandName.getText().toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", edtxtBrandName.getText().toString());
                        firestore.collection("brands").add(map);
                    } else {
                        Toast.makeText(getActivity(), "Bu brend allaqachon qo'shilgan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Bo'sh maydon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isBrandAlreadyExists(String name) {
        for (String brandName : brands) {
            if (name.toLowerCase().equals(brandName.toLowerCase()))
                return true;
        }
        return false;
    }

    private boolean isModelAlreadyExists(String modelID, String brandName) {
        for (Product product : products) {
            if (product.getName() != null && product.getBrandName() != null)
                if (modelID.toLowerCase().equals(product.getName().toLowerCase()) && brandName.toLowerCase().equals(product.getBrandName().toLowerCase()))
                    return true;
        }
        return false;
    }

    public void onAddModelIdClick() {
        addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtxtModelId.getText().toString().isEmpty()) {
                    if (!isModelAlreadyExists(edtxtModelId.getText().toString(), brandNamesSpinner.getSelectedItem().toString())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("brandName", brandNamesSpinner.getSelectedItem().toString());
                        map.put("name", edtxtModelId.getText().toString());
                        firestore.collection("products").add(map);
                    } else {
                        Toast.makeText(getActivity(), "Bu model allaqachon qo'shilgan", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Bo'sh maydon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public CheckFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStop() {
        brandsListener.remove();
        productListener.remove();
        super.onStop();
    }
}
