package com.example.todolist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class DataAdapterCategoryList extends RecyclerView.Adapter<DataAdapterCategoryList.ViewHolder> {
    private LayoutInflater inflater;
    private List<Category> categoryList;
    private List<Category> deleteCategoryList = new LinkedList<>();
    private Context context;
    private Database db = new Database(context);
    private String noCatName;
    private String noCat;

    public DataAdapterCategoryList(Context context, List<Category> categoryList, String noCatName, String noCat) {
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.noCatName = noCatName;
        this.noCat = noCat;
    }

    @Override
    public DataAdapterCategoryList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapterCategoryList.ViewHolder holder, int position) {
        final Category category = categoryList.get(position);

        holder.imageView.setImageResource(R.drawable.label);
        holder.nameView.setText((category.getName().equals(noCatName)) ? noCat : category.getName());
        holder.nameView.setTextSize(18);

        //обработчик кнопки удаления
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryList.add(category);
                categoryList.remove(category);
                DataAdapterCategoryList.this.notifyDataSetChanged();

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

    //проверяет доступ в интернет
    public boolean CheckConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            categoryList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public List<Category> listDeleteCategory() {
        return deleteCategoryList;
    }

}

