package org.techtown.smarket_android.searchItemList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.searchItemList.Request.searchRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class search_list_fragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    JSONArray key;
    String txt;
    private Toolbar toolbar;
    private RecyclerAdapter adapter;
    private List<String> List_item_name = new ArrayList<>();
    private List<String> List_item_value = new ArrayList<>();
    private List<String> List_item_image = new ArrayList<>();
    private List<String> List_item_mall = new ArrayList<>();

    @Nullable
    @Override
    public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_list, container, false);

        CreateList(viewGroup);

        getBundle(viewGroup);

        try {
            getJson();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        setHasOptionsMenu(true);

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
        }
    }

    private void CreateList(ViewGroup viewGroup) {
        recyclerView = viewGroup.findViewById(R.id.search_item_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private Bundle setBundle(View v) {
        Bundle bundle = new Bundle();
        TextView item_name = v.findViewById(R.id.search_list_item_name);
        TextView item_value = v.findViewById(R.id.search_list_item_value);
        ImageView item_image = v.findViewById(R.id.search_list_item_image);
        Drawable d = item_image.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        bundle.putString("item_name", item_name.getText().toString());
        bundle.putString("item_value", item_value.getText().toString());
        bundle.putParcelable("item_image",bitmap);

        return bundle;
    }


    private void getJson() throws UnsupportedEncodingException {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    key = jsonObject.getJSONArray("items");

                    for(int i=0, n=key.length(); i<n; i++){
                        titleJob(i);
                        priceJob(i);
                        ImageJob(i);
                    }

                    for(int i=0, n=List_item_name.size(); i<n; i++){
                        Item item = new Item();
                        item.setList_item_name(List_item_name.get(i));
                        item.setList_item_value(List_item_value.get(i));
                        item.setList_item_image(List_item_image.get(i));
                        adapter.addItem(item);
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        searchRequest searchRequest = new searchRequest(txt, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error + "", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(searchRequest);
    }

    /**
     * 모든 HTML 태그를 제거하고 반환한다.
     *
     * @param html
     * @throws Exception
     */
    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    public void titleJob(int index) throws Exception {
        String title = key.getJSONObject(index).getString("title");
        String res = removeTag(title);
        List_item_name.add(res);
    }

    public void priceJob (int index) throws Exception {
        String price = key.getJSONObject(index).getString("lprice");
        int value = Integer.parseInt(price);
        String lprice = String.format("%,d",value);
        List_item_value.add(lprice+"원");
    }

    public void ImageJob (int index) throws Exception{
        String image = key.getJSONObject(index).getString("image");
        List_item_image.add(image);
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}



//        context = viewGroup.getContext();
//        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.search_item_list);
//        recyclerView.setHasFixedSize(true);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


//                    JSONObject items = key.getJSONObject(0); //원하는 json 결과 인덱스 접근
//                    String itemTitle = items.getString("title"); // 0번 인덱스 객체의 결과값 중 title 선택
//                    test.setText(itemTitle);


//        recyclerView = viewGroup.findViewById(R.id.search_item_list);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(viewGroup.getContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        adapter = new RecyclerAdapter();
//        get_Dataset();
//        recyclerView.setAdapter(adapter);