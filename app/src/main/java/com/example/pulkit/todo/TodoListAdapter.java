package com.example.pulkit.todo;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class TodoListAdapter extends ArrayAdapter<Todo> implements Filterable {
    ArrayList<Todo> TodoArrayList;
    Context context;
    ArrayList<Todo> filteredList;
    ListFilter listFilter;


    public TodoListAdapter(@NonNull Context context, ArrayList<Todo> TodoArrayList) {
        super(context, 0);
        this.context = context;
        this.TodoArrayList = TodoArrayList;
        this.filteredList = TodoArrayList;

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Todo getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class TodoViewHolder {

        TextView nameTextView;
        TextView dateTextView;
        TextView categoryTextView;
        LinearLayout linearLayout;


        TodoViewHolder(TextView nameTextView, TextView dateTextView, TextView categoryTextView, LinearLayout linearLayout) {
            this.nameTextView = nameTextView;
            this.dateTextView = dateTextView;
            this.categoryTextView = categoryTextView;
            this.linearLayout = linearLayout;
        }

    }

    public void resetData() {
        filteredList = TodoArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item1, parent, false);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.todotitle);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.tododate);
            TextView categoryTextView = (TextView) convertView.findViewById(R.id.todocategory);
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.categorycolor);
            TodoViewHolder todoViewHolder = new TodoViewHolder(nameTextView, dateTextView, categoryTextView, linearLayout);
            convertView.setTag(todoViewHolder);
        }

        Todo e = getItem(position);//to differentiate between filtered list and todo list
        TodoViewHolder expenseViewHolder = (TodoViewHolder) convertView.getTag();
        assert e != null;
        expenseViewHolder.nameTextView.setText(e.title);
        expenseViewHolder.dateTextView.setText(e.date);
        expenseViewHolder.categoryTextView.setText(context.getResources().getStringArray(R.array.spinner_item)[e.category]);
        if (e.priority == 2)
            expenseViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Red));
        else if (e.priority == 1)
            expenseViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Green));
        else if (e.priority == 0)
            expenseViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Todo> tempList = new ArrayList<>();
                for (Todo user : TodoArrayList) {
                    if (user.title.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                        Log.i("Data1234",user.title+"=="+constraint.toString());
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;

            } else {
                filterResults.count = TodoArrayList.size();
                filterResults.values = TodoArrayList;
            }
            return filterResults;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Todo>) results.values;
            filteredList.toString();
            Log.i("Data1234",filteredList.toString());

            notifyDataSetChanged();
        }
    }

}
