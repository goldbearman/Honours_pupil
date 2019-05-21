package com.sabirovfarit.android.rx.SearchFragment.BottomFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.WordViewModel;

import java.util.List;

public class AddSelectedBottomFragment extends BottomSheetDialogFragment implements AddSelectedBottomFragmentAdapter.Listener {

    public static final String NEW_LIST_BOTTOM_FRAGMENT = "new_list_bottom_fragment";

    RecyclerView recyclerView;
    WordViewModel viewModel;
    AddSelectedBottomFragmentAdapter adapter;

    public static AddSelectedBottomFragment newInstance() {
        return new AddSelectedBottomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bott_fragment_add_selected, container, false);
        Button btnCreateNew = view.findViewById(R.id.btn_create_new);
//        WordColor.assignAttributeColorDrawable(getActivity(),2,btnCreateNew);
        btnCreateNew.setBackground(WordColor.assignColorDrawable(getActivity(),2,R.drawable.all_button,1));
        RxView.clicks(btnCreateNew).subscribe(o -> {
            CreateNewListBottomFragment.newInstance().show(getActivity().getSupportFragmentManager(), NEW_LIST_BOTTOM_FRAGMENT);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
        recyclerView = view.findViewById(R.id.recview_list_of_list);
        updateMyList();
        return view;
    }

    @Override
    public void onStop() {
        adapter.setListener(null);
        super.onStop();
    }

    private void updateMyList() {
        viewModel = ViewModelProviders.of(getActivity()).get(WordViewModel.class);
        viewModel.getInClassFalse().observe(this, wordsLists -> {
            List<Long> idsList = viewModel.getIdsList();
            adapter = new AddSelectedBottomFragmentAdapter(wordsLists,idsList);
            recyclerView.setAdapter(adapter);
            adapter.setListener(this);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
        });

    }
    //При нажатии на элемента адаптера обратным вызовом закрываем фрагмент
    @Override
    public void onClick() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
