package com.sabirovfarit.android.rx.LearningFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.DB.WordsList;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;


public class WordsListLearnAdapter extends RecyclerView.Adapter<WordsListLearnAdapter.ViewHolder> {
    private static final String TAG = "WordsListSearchAdapter";

    List<WordsList> list;
    Context context;

    ListnerAllWords listnerAllWords;

    public interface ListnerAllWords {
        void onClick(long idWordList);
    }

    public void setListnerAllWords(ListnerAllWords listnerAllWords) {
        this.listnerAllWords = listnerAllWords;
    }

    public WordsListLearnAdapter(List<WordsList> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.words_card_learn, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindCard(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView tvNameList;
        private TextView tvNumberWords;
        private ImageView ivDelete;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }

        public void bindCard(int position) {
            WordsList wordsList = list.get(position);

            tvNameList = cardView.findViewById(R.id.tv_name_list);
            tvNumberWords = cardView.findViewById(R.id.tv_number_words);
            ivDelete = cardView.findViewById(R.id.iv_delete);

            // Присваиваем текст
            tvNameList.setText(wordsList.getName());

            //Show the numbers of words
            int sizeWords = wordsList.getListId().size();
            tvNumberWords.setText(context.getResources().getQuantityString(R.plurals.plurals_word, sizeWords, sizeWords));

            ivDelete.setOnClickListener(v -> {

                Flowable.fromCallable(() -> wordsList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(wordsList1 -> {
                            Log.i(TAG, "ivDelete: " + Thread.currentThread());
                            App.getInstance().getDatabase().wordsListDao().delete(wordsList);
                            return wordsList1.getListId();
                        }).subscribe(list1 -> {
                    for (int i = 0; i < list1.size(); i++) {
                        Long id = list1.get(i);
                        App.getInstance().getDatabase().wordDao().deleteWordById(id);
                    }
                });
            });


//            Drawable drawable = context.getResources().getDrawable(R.drawable.card_view_template);
//            drawable.setColorFilter(wordsList.getColor(), PorterDuff.Mode.MULTIPLY);
//            cardView.setBackground(drawable);

            cardView.setBackground(WordColor.assignColorDrawable(context, wordsList.getColor(), R.drawable.card_view_template, 2));

            cardView.setOnClickListener(v -> {
                //Создаем listner для вызова во фрагменте
                if (listnerAllWords != null) {
                    listnerAllWords.onClick(wordsList.getId());
                }
            });


        }
    }

}
