package com.example.pulkit.todo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pulkit on 7/13/2017.
 */

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TodoViewHolder> {

    private Context mContext;
    private ArrayList<Todo> mTodos;
    private TodosClickListener mListener;
    public interface TodosClickListener {
        void onItemClick(View view, int position);
    }
    public RecyclerAdapter(Context context,ArrayList<Todo> todos,TodosClickListener listener){
        mContext = context;
        mTodos = todos;
        mListener = listener;
    }


    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item2,parent,false);
        return new TodoViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        Todo todo = mTodos.get(position);
        holder.titleTextView.setText(todo.title);
        holder.dateTextView.setText(todo.date);
        holder.categoryTextView.setText(mContext.getResources().getStringArray(R.array.spinner_item)[todo.category]);
        if (todo.priority == 2)
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Red));
        else if (todo.priority == 1)
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Green));
        else if (todo.priority == 0)
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }

    @Override
    public int getItemCount() {
        return mTodos.size();
    }
    public static class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView dateTextView;
        TextView categoryTextView;
        LinearLayout linearLayout;
        TodosClickListener mTodosClickListener;

        public TodoViewHolder(View itemView, TodosClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTodosClickListener = listener;
            titleTextView = (TextView) itemView.findViewById(R.id.todotitle);
            dateTextView = (TextView) itemView.findViewById(R.id.tododate);
            categoryTextView = (TextView) itemView.findViewById(R.id.todocategory);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.categorycolor);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (id == R.id.note_layout1) {
                    mTodosClickListener.onItemClick(view, position);
                }
            }
        }
    }
}
