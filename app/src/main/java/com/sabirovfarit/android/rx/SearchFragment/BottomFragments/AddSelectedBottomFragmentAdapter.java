package com.sabirovfarit.android.rx.SearchFragment.BottomFragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.LearningFragment.LearningWords.LearningWordsActivity;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.DB.Word;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;
import com.sabirovfarit.android.rx.DB.WordsList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class AddSelectedBottomFragmentAdapter extends RecyclerView.Adapter<AddSelectedBottomFragmentAdapter.ViewHolder> {
    private static final String TAG = "WordsListSearchAdapter";

    List<WordsList> list;
    Context context;
    List<Long> newIdsList;
    private BottomNavigationView bottomNavigationView;

    public Listener listener;

    public interface Listener {
        void onClick();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public AddSelectedBottomFragmentAdapter(List<WordsList> list, List<Long> newIdsList) {
        this.list = list;
        this.newIdsList = newIdsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.words_card_add_new_list, parent, false);
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
        private WordsList wordsList;


        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            // После того как пользователь выбрал слова для изучения, он нажимает добавить в мой список
            // Открывается AddSelectedBottomFragment, где он может разать создать новую подборку и перейдет в CreateNewListBottomFragment
            //Было два варианта:1 - позволить исплозовать слово только в одном списке или 2 - позволить добавлять слово в любой список
            //Выбрал более сложный - 2
            //Если он нажимает на любой пункт RecyclerView, слова добавятся уже к имующимся: в таблицу Word перезаписью этих же слов, но с новым id и в табалицу wordList ячейку listId
            cardView.setOnClickListener(v -> {
                //Получаем список id из таблицы wordList по нажатому элементу, чтобы потом к нему добавить Id новых слов
                List<Long> listInBase = wordsList.getListId();

                // Задача: добавить новые слова к уже имеющимся.  в графе listId таблицы WordsList. При этом сдова нужно отфильтровать в алфавитном порядке.
                // По переданным в адаптер id, получаем слова из БД
                App.getInstance().getDatabase().wordDao().getWordsByIdSingle(newIdsList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(words -> {
                            Log.i(TAG, "ViewHolder: " + Thread.currentThread());
                            //Получаем список слов которые уже содержатся в нашем списке
                            List<Word> wordsByIdList = App.getInstance().getDatabase().wordDao().getWordsByIdList(listInBase);
                            //Создаем список для номеров повторяющихся слов
                            List<Integer> sameWords = new ArrayList<>();
                            //Сохраняем номера повторяющихся слов
                            for (int i = 0; i < words.size(); i++) {
                                for (int j = 0; j < wordsByIdList.size(); j++) {
                                    if (words.get(i).getValue().equals(wordsByIdList.get(j).getValue())) {
                                        sameWords.add(i);
                                        break;
                                    }
                                }
                            }
                            // Удаляем повторяющиеся слова в обратном порядке
                            for (int i = sameWords.size() - 1; i >= 0; i--) {
                                words.remove((int) (sameWords.get(i)));
                            }
                            Log.i(TAG, "newList: " + words);
                            return words;
                        })
                        .map(list1 -> {
                            Log.i(TAG, "map: " + list1);
                            //Создаем новый список с теми же словами без id для вставки
                            List<Word> listWithoutId = new ArrayList<>();
                            for (Word word : list1) {
                                listWithoutId.add(new Word(word.getValue(), word.getDiffLetter(), word.getWordsClass(), true, word.getSoundWId()));
                            }
                            return listWithoutId;
                        }).map(list1 -> {
                    //Вставляем новый список и получаем их id
                    List<Long> idInsertWords = App.getInstance().getDatabase().wordDao().addReturnIds(list1);
                    return idInsertWords;
                }).map(list1 -> {
                    //Добавляем новые id к списку уже имеющихся в графе listId
                    listInBase.addAll(list1);
                    // По полученному списку получаем список всех Word для сортировки
                    List<Word> wordsByIdList = App.getInstance().getDatabase().wordDao().getWordsByIdList(listInBase);
                    return wordsByIdList;
                }).map(list1 -> {
                    Collections.sort(list1, new Comparator<Word>() {
                        @Override
                        public int compare(Word o1, Word o2) {
                            return o1.getValue().compareTo(o2.getValue());  //Сортируем в алфавитном порядке
                        }
                    });
                    return list1;
                }).map(list1 -> {
                    List<Long> longList = new ArrayList<>(); // Создаем список id отсортированных слов
                    for (Word word : list1) {
                        longList.add(word.getId());
                    }
                    return longList;
                }).subscribe(list1 -> {
                    //Добавляем список новых id к wordList в графу listId
                    wordsList.setListId(list1);
                    //Обнавляе wordList
                    App.getInstance().getDatabase().wordsListDao().update(wordsList);
                });

                //Закрываем фрагмент обратным вызовом
                if (listener != null) {
                    listener.onClick();
                }

                // Переходим в список слов AllWordsLearningWordsFragment(переделать через listener!)
                Intent intent = LearningWordsActivity.newIntent(context, wordsList.getId());
                context.startActivity(intent);

            });
        }

        public void bindCard(int position) {
            wordsList = list.get(position);

            tvNameList = cardView.findViewById(R.id.tv_name_list);
            tvNumberWords = cardView.findViewById(R.id.tv_number_words);

            tvNameList.setText(wordsList.getName());

            //Show the numbers of words
            int sizeWords = wordsList.getListId().size();
            tvNumberWords.setText(context.getResources().getQuantityString(R.plurals.plurals_word, sizeWords, sizeWords));

            // Устанавливае цвет фона wordsList.getColor()
            cardView.setBackground(WordColor.assignColorDrawable(context, wordsList.getColor(), R.drawable.card_view_template, 2));
        }
    }

}
