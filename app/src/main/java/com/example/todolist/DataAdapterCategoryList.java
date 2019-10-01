package com.example.todolist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DataAdapterCategoryList extends RecyclerView.Adapter<DataAdapterCategoryList.ViewHolder> {
    private LayoutInflater inflater;
    private List<Category> categoryList;
    private Context context;

    public DataAdapterCategoryList(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public DataAdapterCategoryList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterCategoryList.ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.imageView.setImageResource(category.getIdIcon());
        holder.nameView.setText(category.getName());

        //обработчик кнопки удаления
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activitySecondMenu.startActivityLearn("",fileName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        ImageView imageView;
        ImageButton imageButton;

        ViewHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.text_category);
            imageView = (ImageView) view.findViewById(R.id.image_category);
            imageButton = (ImageButton) view.findViewById(R.id.button_delete_category);
        }
    }
}

