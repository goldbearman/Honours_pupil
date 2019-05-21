package com.sabirovfarit.android.rx.LearningFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.DB.WordsList;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class WordsListGradeLearnAdapter extends RecyclerView.Adapter<WordsListGradeLearnAdapter.ViewHolder> {
    private static final String TAG = "WordsListSearchAdapter";

    List<WordsList> list;
    Context context;

    ListnerAllWords listnerAllWords;

    public interface ListnerAllWords {
        void onClickGrade(long idWordList);
    }

    public void setListnerAllWords(ListnerAllWords listnerAllWords) {
        this.listnerAllWords = listnerAllWords;
    }

    public WordsListGradeLearnAdapter(List<WordsList> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.words_card_learn_drade, parent, false);
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
        private ImageView ivPlus;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }

        public void bindCard(int position) {
            WordsList wordsList = list.get(position);

            tvNameList = cardView.findViewById(R.id.tv_name_list);
            tvNumberWords = cardView.findViewById(R.id.tv_number_words);

            tvNameList.setText(wordsList.getName());

            ivPlus = cardView.findViewById(R.id.iv_plus);
            ivPlus.setOnClickListener(v -> {

                Flowable.fromArray(wordsList.getListId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(list1 -> App.getInstance().getDatabase().wordDao().getWordsByIdList(list1))
                        .map(words -> {
                            //Создаем новый список слов без id для вставки
                            List<Word> listWithoutId = new ArrayList<>();
                            for (Word word : words) {
                                listWithoutId.add(new Word(word.getValue(), word.getDiffLetter(), word.getWordsClass(), true,word.getSoundWId()));
                            }
                            return listWithoutId;
                            //Вставляем слова и получаем списаок их id
                        }).map(words -> App.getInstance().getDatabase().wordDao().addReturnIds(words))
                        .subscribe(list1 -> {
                            //Вставляем новую строку с тем же названием,цветок,но с новым списком id
                            WordsList sameWordList = new WordsList(wordsList.getName(), wordsList.getColor(), list1, false, false);
                            App.getInstance().getDatabase().wordsListDao().add(sameWordList);
                        });
            });
            //Show the numbers of words
            int sizeWords = wordsList.getListId().size();
            tvNumberWords.setText(context.getResources().getQuantityString(R.plurals.plurals_word, sizeWords, sizeWords));

            //Устанавливаем цвет фону wordsList.getColor()
            cardView.setBackground(WordColor.assignColorDrawable(context,wordsList.getColor(), R.drawable.card_view_template, 2));


            cardView.setOnClickListener(v -> {
                //Создаем listner для вызова в LearningFragment
                if (listnerAllWords != null) {
                    listnerAllWords.onClickGrade(wordsList.getId());
                }
            });


        }
    }

}
