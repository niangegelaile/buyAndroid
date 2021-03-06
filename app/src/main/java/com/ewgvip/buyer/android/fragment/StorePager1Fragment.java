package com.ewgvip.buyer.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewgvip.buyer.android.R;
import com.ewgvip.buyer.android.activity.BaseActivity;
import com.ewgvip.buyer.android.adapter.StorePager1Adapter;
import com.ewgvip.buyer.android.utils.CommonUtil;
import com.ewgvip.buyer.android.utils.MySingleRequestQueue;
import com.ewgvip.buyer.android.utils.ScrollListener;
import com.ewgvip.buyer.android.volley.NormalPostRequest;
import com.ewgvip.buyer.android.volley.Request;
import com.ewgvip.buyer.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺首页
 */
public class StorePager1Fragment extends Fragment {
    private BaseActivity mActivity;
    private View rootView;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private List list_store = new ArrayList();
    private StorePager1Adapter storePager1Adapter;
    private ScrollListener scrollListener;

    public static StorePager1Fragment getInstance(String store_id) {
        StorePager1Fragment fragment = new StorePager1Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("store_id", store_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.rootView = null;
        this.recyclerView = null;
        this.mLayoutManager = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store_pager1, container, false);
        mActivity = (BaseActivity) getActivity();

        Bundle bundle = getArguments();
        String store_id = bundle.getString("store_id");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        storePager1Adapter = new StorePager1Adapter(mActivity, list_store);
        recyclerView.setAdapter(storePager1Adapter);
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            boolean result = false;
            if (scrollListener != null) {
                result = scrollListener.setOnTouchListener(view, motionEvent, mLayoutManager);
                return result;
            } else {
                return result;
            }
        });

        Map paramap = mActivity.getParaMap();
        paramap.put("store_id", store_id);
        RequestQueue mRequestQueue = MySingleRequestQueue.getInstance(getActivity()).getRequestQueue();
        Request<JSONObject> request = new NormalPostRequest(mActivity, CommonUtil.getAddress(mActivity) + "/app/store.htm",
                result -> {
                    try {
                        JSONArray jsonArray = result.getJSONArray("goods_data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objOuter = jsonArray.getJSONObject(i);
                            String title = objOuter.getString("name");
                            Map map = new HashMap();
                            map.put("title", title);
                            list_store.add(map);
                            JSONArray jsonArray1 = objOuter.getJSONArray("data");
                            for (int j = 0; j < jsonArray1.length(); j = j + 2) {
                                Map line_map = new HashMap();
                                JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                Map map1 = new HashMap();
                                map1.put("id", jsonObject.getInt("id"));
                                map1.put("goods_main_photo", jsonObject.get("goods_main_photo"));
                                map1.put("goods_current_price", jsonObject.get("goods_current_price"));
                                map1.put("goods_name", jsonObject.get("goods_name"));
                                line_map.put("goods1", map1);

                                if (jsonArray1.length() > j + 1) {
                                    jsonObject = jsonArray1.getJSONObject(j + 1);
                                    Map map2 = new HashMap();
                                    map2.put("id", jsonObject.getInt("id"));
                                    map2.put("goods_main_photo", jsonObject.get("goods_main_photo"));
                                    map2.put("goods_current_price", jsonObject.get("goods_current_price"));
                                    map2.put("goods_name", jsonObject.get("goods_name"));
                                    line_map.put("goods2", map2);
                                }

                                list_store.add(line_map);
                            }
                            storePager1Adapter.notifyDataSetChanged();
                        }

                        if(list_store.size()==0) {
                            rootView.findViewById(R.id.nodata).setVisibility(View.VISIBLE);
                            rootView.findViewById(R.id.nodata_refresh).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            rootView.findViewById(R.id.nodata).setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                    }
                }, error -> {
                    rootView.findViewById(R.id.nodata).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.nodata_refresh).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }, paramap);
        mRequestQueue.add(request);


        return rootView;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        if (null != scrollListener) {
            this.scrollListener = scrollListener;
        }
    }
}
