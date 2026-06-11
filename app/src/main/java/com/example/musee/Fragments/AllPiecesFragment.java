package com.example.musee.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager; // ✅ تم إضافتها لتفعيل تصميم الشلال

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.musee.Activity.MainActivity;
import com.example.musee.R;
import com.example.musee.Adapters.AllPiecesAdapter;
import com.example.musee.classes.FirebaseServices;
import com.example.musee.classes.PieceClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllPiecesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPiecesFragment extends Fragment {


    private RecyclerView rvAllPiecesFragment;
    private FirebaseServices fbs;
    private AllPiecesAdapter myAdapter;
    private ArrayList<PieceClass> pieces, filteredList;
    private Button btHomeAllPiecesFragment;

    //  إضافات البحث
    private LinearLayout searchLayout;
    private EditText searchBar, minPriceEditText, maxPriceEditText;
    private Spinner categorySpinner, sizeSpinner;

    private Button btnSearch;
    private ImageButton btnSearchToggle;

    private String[] categories = {"Select Category", "Oil painting", "acrylic painting", "watercolor painting",
            "pencil drawing", "digital drawing", "other.."};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllPiecesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllPiecesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllPiecesFragment newInstance(String param1, String param2) {
        AllPiecesFragment fragment = new AllPiecesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_pieces, container, false);

        init(view); // ✅ أهم سطر

        // --- SEARCH UI ---
        searchLayout = view.findViewById(R.id.searchLayout);
        searchBar = view.findViewById(R.id.searchBar);
        minPriceEditText = view.findViewById(R.id.minPrice);
        maxPriceEditText = view.findViewById(R.id.maxPrice);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearchToggle = view.findViewById(R.id.btnSearchToggle);

        categorySpinner.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        ));

        sizeSpinner = view.findViewById(R.id.sizeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.types_array_size,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapter);

        btnSearchToggle.setOnClickListener(v -> {
            if (searchLayout.getVisibility() == View.GONE)
                searchLayout.setVisibility(View.VISIBLE);
            else
                searchLayout.setVisibility(View.GONE);
        });

        btnSearch.setOnClickListener(v -> performSearch());

        return view;
    }


    private void performSearch() {
        String text = searchBar.getText().toString().trim().toLowerCase();
        String cat = categorySpinner.getSelectedItem().toString();
        String sz = sizeSpinner.getSelectedItem().toString();
        String minText = minPriceEditText.getText().toString().trim();
        String maxText = maxPriceEditText.getText().toString().trim();

        boolean catFlag = !cat.equals("Select Category");
        double min = minText.isEmpty() ? 0 : Double.parseDouble(minText);
        double max = maxText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxText);

        String defaultSize = getResources().getStringArray(R.array.types_array_size)[0];
        boolean sizeFlag = !sz.equals(defaultSize);

        filteredList.clear();

        for (PieceClass piece : pieces) {
            boolean matchName = text.isEmpty() || piece.getname().toLowerCase().contains(text);
            boolean matchCategory = !catFlag || piece.getCategory().equalsIgnoreCase(cat);
            boolean matchSize = !sizeFlag || piece.getSize().equalsIgnoreCase(sz);

            double price = 0;
            try { price = Double.parseDouble(piece.getPrice()); } catch (Exception e) {}
            boolean matchPrice = price >= min && price <= max;

            if (matchName && matchCategory && matchSize && matchPrice) {
                filteredList.add(piece);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
        }
        ///myAdapter.notifyDataSetChanged();
        myAdapter.notifyDataSetChanged(); // CHANGE

    }


    private void init(View view) {
        // Get the MainActivity once to call its public navigation methods
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) { // Also check if the view is null
            return; // Exit if the activity or view is not available
        }

        // 🔥 التعديل هنا فقط: استخدام view بدل getView()
        btHomeAllPiecesFragment = view.findViewById(R.id.btnHomeAllPiecesFragment);
        btHomeAllPiecesFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                // This 'if/else' is the key.
                if (currentUser == null) {
                    // If no user is logged in, show the login page.
                    mainActivity.gotoLogInFragment();
                } else {
                    // If a user IS logged in, show the main content page.
                    // This fixes the white screen and the login loop.
                    mainActivity.gotoUserHomePgFragment();
                }
            }
        });

        // 🔥 نفس التعديل هنا
        rvAllPiecesFragment = view.findViewById(R.id.rvAllPiecesFragment);
        //ivProfile = getView().findViewById(R.id.ivProfileCarListMapFragment);

        fbs = FirebaseServices.getInstance();
        fbs.setUserChangeFlag(false);
    /*if (fbs.getAuth().getCurrentUser() == null)
        fbs.setCurrentUser(fbs.getCurrentObjectUser()); */

        pieces = new ArrayList<>();

        rvAllPiecesFragment.setHasFixedSize(true);

        // 🔥 التعديل الأساسي هنا: استبدال LinearLayoutManager بـ StaggeredGridLayoutManager لعرض عمودين متداخلين بشكل احترافي
        StaggeredGridLayoutManager staggeredLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvAllPiecesFragment.setLayoutManager(staggeredLayoutManager);

        pieces = getPieces();

        filteredList = new ArrayList<>();              // CHANGE
        filteredList.addAll(pieces);                   // CHANGE
        myAdapter = new AllPiecesAdapter(getActivity(), filteredList); // CHANGE
        rvAllPiecesFragment.setAdapter(myAdapter);     // CHANGE

        ///myAdapter = new AllPiecesAdapter(getActivity(), pieces);
        ///filteredList = new ArrayList<>();

        myAdapter.setOnItemClickListener(new AllPiecesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PieceClass selectedPiece = filteredList.get(position);

                Bundle args = new Bundle();
                args.putParcelable("pieces", selectedPiece);
                // الآن selectedPiece.getPieceId() لن يكون Null
                args.putString("pieceDocId", selectedPiece.getPieceId());
                args.putString("from", "all");

                PieceDetailsFragment fragment = new PieceDetailsFragment();
                fragment.setArguments(args);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayOutMain, fragment);
                ft.addToBackStack(null); // يفضل إضافتها لكي يستطيع المستخدم العودة للمعرض
                ft.commit();
            }
        });
    }

    public ArrayList<PieceClass> getPieces() {
        ArrayList<PieceClass> piecesList = new ArrayList<>();

        try {
            // إضافة شرط .whereEqualTo("isSold", false) لفلترة اللوحات المباعة
            fbs.getFire().collection("pieces")
                    .whereEqualTo("isSold", false)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                piecesList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    PieceClass piece = document.toObject(PieceClass.class);

                                    // مهم جداً: تعيين الـ ID الخاص بالوثيقة داخل الكائن
                                    piece.setPieceId(document.getId());

                                    piecesList.add(piece);
                                }

                                // تحديث القائمة المفلترة والـ Adapter
                                filteredList.clear();
                                filteredList.addAll(piecesList);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("AllPiecesFragment", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch (Exception e) {
            Log.e("getPieces Error: ", e.getMessage());
        }

        return piecesList;
    }

    private void showNoDataDialogue() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Results");
        builder.setMessage("Try again!");
        builder.show();
    }
}