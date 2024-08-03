package com.minibox.minideveloper.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.minibox.minideveloper.View.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minibox.minideveloper.Adapter.SectionAdapter;
import com.minibox.minideveloper.BaseClass.BaseFragment;
import com.minibox.minideveloper.R;

public class MoreFragment extends BaseFragment {

    public static MoreFragment newInstance(){
        return new MoreFragment();
    }

    private RecyclerView recyclerView;
    private final int[] img = {R.drawable.ic_fish,R.drawable.ic_article,
            R.drawable.ic_lua,R.drawable.ic_java,R.drawable.ic_html};

    private final String[] content = {"鱼塘","文章","Lua","Java","Html"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        recyclerView = view.findViewById(R.id.fragment_more_recycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();

    }

    private void initData() {
        GridLayoutManager manager = new GridLayoutManager(getActivity(),4);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        SectionAdapter sectionAdapter = new SectionAdapter(getActivity());
        sectionAdapter.SetData(img,content);
        recyclerView.setAdapter(sectionAdapter);
    }

}
