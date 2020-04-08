package org.techtown.smarket_android.searchItemList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smarket_android.R;

import java.util.Arrays;
import java.util.List;

public class search_list_fragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private RecyclerAdapter adapter;

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_list, container, false);

//        CreateList(viewGroup);

//        recyclerView = viewGroup.findViewById(R.id.search_item_list);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        adapter = new RecyclerAdapter();
//        get_Dataset();
//        recyclerView.setAdapter(adapter);


        getBundle(viewGroup);
        try {
            getJson();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        CreateList(viewGroup);

        adapter.setOnRecyclerClickListener(new RecyclerAdapter.OnRecyclerClickListener() {
            @Override
            public void OnRecyclerClickListener(View v, int position) {
                searchdetail_fragment searchdetailFragment = new searchdetail_fragment();
                Bundle bundle = setBundle(v);
                searchdetailFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_layout, searchdetailFragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return viewGroup;
    }

    private void getBundle(ViewGroup viewGroup){
        if(getArguments()!=null){
            txt = getArguments().getString("searchName");
            test = viewGroup.findViewById(R.id.test_text);
            test.setText(txt);
        }
    }

//    private void get_Dataset(final ViewGroup viewGroup) {
//          items.add(new Item(getResources().getDrawable(R.drawable.premierball), "참깨라면", "1000원"));
////        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "진라면", "2000원"));
////        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "프라포치노", "3200원"));
////        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "아이스커피", "4000원"));
//
////        List<String> item_name = Arrays.asList("국화", "사막", "수국", "해파리", "코알라", "등대", "펭귄");
////        List<String> item_value = Arrays.asList("1000","1100","1200","1300","1400","1500","1600");
////        List<Integer> itemImage = Arrays.asList(R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,
////                R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,R.drawable.premierball);
////
////        for(int i=0; i<item_name.size(); i++){
////            Item item = new Item();
////            item.setItem_name(item_name.get(i));
////            item.setItem_value(item_value.get(i));
////            item.setItem_image(itemImage.get(i));
////            adapter.addItem(item);
////        }
//

////        adapter.notifyDataSetChanged();

    private void CreateList(ViewGroup viewGroup) {
//        context = viewGroup.getContext();
//        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.search_item_list);
//        recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = viewGroup.findViewById(R.id.search_item_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

        get_Dataset();

        return viewGroup;
    }


    private void getJson() throws UnsupportedEncodingException {

    private void get_Dataset() {
        //        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "참깨라면", "1000원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "진라면", "2000원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "프라포치노", "3200원"));
//        items.add(new Item(getResources().getDrawable(R.drawable.premierball), "아이스커피", "4000원"));

        List<String> item_name = Arrays.asList("국화", "사막", "수국", "해파리", "코알라", "등대", "펭귄");
        List<String> item_value = Arrays.asList("1000","1100","1200","1300","1400","1500","1600");
        List<Integer> itemImage = Arrays.asList(R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,
                R.drawable.premierball,R.drawable.premierball,R.drawable.premierball,R.drawable.premierball);

        for(int i=0; i<item_name.size(); i++){
            Item item = new Item();
            item.setItem_name(item_name.get(i));
            item.setItem_value(item_value.get(i));
            item.setItem_image(itemImage.get(i));
            adapter.addItem(item);
        }

        adapter.notifyDataSetChanged();
    }

//    private void CreateList(ViewGroup viewGroup) {
//        context = viewGroup.getContext();
//        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.search_item_list);
//        recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//    }

//    private Bundle setBundle(View v) {
//        Bundle bundle = new Bundle();
//        TextView item_name = v.findViewById(R.id.item_name);
//        TextView item_value = v.findViewById(R.id.item_value);
//
//        if (getArguments() != null) {
//            bundle.putString("item_name", item_name.getText().toString());
//            bundle.putString("item_value", item_value.getText().toString());
//        }
//        return bundle;
//    }
}
