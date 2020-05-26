
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Pager.dodAdapter.detailAdapter;
import org.techtown.smarket_android.smarketClass.specList;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class search_detail_of_detail_fragment extends Fragment {

    RecyclerView.Adapter dodAdapter;

    private List<specList> spec;
    private RecyclerView recyclerView;

    public search_detail_of_detail_fragment() {

    }

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_detail_of_detail_fragment_layout, container, false);
        spec = new ArrayList<>();

        getBundle();

        CreateList(viewGroup);


        return viewGroup;
    }

    private void CreateList(ViewGroup viewGroup) {
        recyclerView = viewGroup.findViewById(R.id.dodRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dodAdapter = new detailAdapter(getContext(), spec);
        recyclerView.setAdapter(dodAdapter);
    }

    private void getBundle() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            List<specList> list = (List<specList>) bundle.getSerializable("spec");
            spec = list;
            Log.d(TAG, "dod: " + spec);
        }
    }
}