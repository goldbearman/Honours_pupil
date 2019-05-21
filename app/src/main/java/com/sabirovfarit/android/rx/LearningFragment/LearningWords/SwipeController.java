package com.sabirovfarit.android.rx.LearningFragment.LearningWords;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.sabirovfarit.android.rx.DB.App;
import com.sabirovfarit.android.rx.DB.AppDB;
import com.sabirovfarit.android.rx.R;
import com.sabirovfarit.android.rx.UsefulClass.WordColor;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class SwipeController extends ItemTouchHelper.Callback {

    private static final String TAG = "SwipeController";

    Context context;
    long idWordList;
    private Paint p = new Paint();
    private int numbersDirection;

    public SwipeController(long idWordList, int numbersDirection) {
        this.idWordList = idWordList;
        this.numbersDirection = numbersDirection;

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        context = recyclerView.getContext();
        // Если numbersDirection(количество направлений свайпа) равен одному, будет работать только свайп влево(удаление)
        if (numbersDirection == 1) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        } else
            return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        AppDB database = App.getInstance().getDatabase();
        if (direction == ItemTouchHelper.LEFT) {
            // При движении влево удаляем свайпнутый элемент
            database.wordsListDao().getWordListByIdSingle(idWordList)
                    .subscribeOn(Schedulers.io())
                    .map(wordsList -> {
                        // Получаем список id из поля listId
                        List<Long> listId = wordsList.getListId();
                        // Получаем позицию свайпнутого элемента
                        int position = viewHolder.getAdapterPosition();
                        Log.i(TAG, "onSwiped: " + position);
                        // Получаем Id свайпнутого элемента
                        Long idWord = listId.get(position);
                        // Удаляем из списка свайпнутый элемент
                        listId.remove(position);
                        // Добавляем списак без этого элемента обратоно в wordsList
                        wordsList.setListId(listId);
                        // Вставляем обратно. При вставке должен заменить: OnConflictStrategy.REPLACE
                        database.wordsListDao().insert(wordsList);
                        return idWord;
                    }).observeOn(Schedulers.newThread())
                    .subscribe(aLong -> database.wordDao().deleteWordById(aLong));
        } else {
            // При свайпу вправа делаем параметр mLearned false
            database.wordsListDao().getWordListByIdSingle(idWordList)
                    .subscribeOn(Schedulers.io())
                    .map(wordsList -> {
                        // Получаем список id из поля listId
                        List<Long> listId = wordsList.getListId();
                        // Получаем позицию свайпнутого элемента
                        int position = viewHolder.getAdapterPosition();
                        Log.i(TAG, "onSwiped: " + position);
                        // Получаем Id свайпнутого элемента
                        Long idWord = listId.get(position);
                        return idWord;
                    }).observeOn(Schedulers.newThread())
                    .subscribe(aLong -> database.wordDao().updatemStage1234andmLearnedById(aLong, false));
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            // Получаем цвет темы colorPrimary
            TypedArray b = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
            int attributeResourceId = b.getResourceId(0, 0);


            if (dX > 0) {
                p.setColor(context.getResources().getColor(WordColor.getAttributeColor(context, 1,true)));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_cached_white_24dp);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white_24dp);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
