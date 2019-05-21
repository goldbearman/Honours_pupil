package com.sabirovfarit.android.rx.LearningFragment.LearningWords;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;

import java.util.List;

public class FiveWordsAdapter extends RecyclerView.Adapter<FiveWordsAdapter.FiveWordsHolder> {

    List<Word> list;
    Context context;

    public FiveWordsAdapter(List<Word> list) {
        this.list = list;
    }

    @Override
    public FiveWordsHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        this.context = viewGroup.getContext();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_words_item_layout, viewGroup, false);

        return new FiveWordsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiveWordsHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FiveWordsHolder extends RecyclerView.ViewHolder {

        TextView tvValue;
        TextView tvLearningOrNot;

        public FiveWordsHolder(View itemView) {
            super(itemView);
        }

        public void bind(int position) {
            Word word = list.get(position);
            tvValue = itemView.findViewById(R.id.tv_value_item);
            tvLearningOrNot = itemView.findViewById(R.id.tv_learned_or_not);
            tvValue.setText(word.getValue());
            tvLearningOrNot.setVisibility(View.INVISIBLE);
        }
    }

}
