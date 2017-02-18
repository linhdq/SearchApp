package com.example.linhdq.searchapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.linhdq.searchapp.R;
import com.example.linhdq.searchapp.model.QuestionModel;

import java.util.List;

/**
 * Created by LinhDQ on 11/16/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {
    private List<QuestionModel> list;
    private Context context;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter(List<QuestionModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_on_list, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        QuestionModel model = list.get(position);
        if (model != null) {
            holder.txtQuestionContent.setText(model.getQuesContent());
            holder.txtAnswer.setText(model.getAnswer());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void swap(List<QuestionModel> datas){
        list = datas;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        //view
        private TextView txtQuestionContent;
        private TextView txtAnswer;

        public ItemViewHolder(View itemView) {
            super(itemView);
            //init
            txtQuestionContent = (TextView) itemView.findViewById(R.id.txt_question_content);
            txtAnswer = (TextView) itemView.findViewById(R.id.txt_answer);
        }
    }
}
