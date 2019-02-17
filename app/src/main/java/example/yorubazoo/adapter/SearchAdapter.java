package example.yorubazoo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.yorubazoo.R;
import example.yorubazoo.models.AnimalData;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private List<AnimalData> AnimalDataList = new ArrayList<>();
    private Callback callback;
    private Context context;

    public SearchAdapter(Context context, List<AnimalData> AnimalData, Callback callback){
        this.context = context;
        this.AnimalDataList = AnimalData;
        this.callback = callback;
    }
    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_adapter,parent,false);
        return new SearchAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        AnimalData AnimalData = AnimalDataList.get(position);
        String content = AnimalData.getAnimal();
        holder.textView.setText(content);
    }

    @Override
    public int getItemCount() {
        return AnimalDataList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.animal);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.itemClick(getAdapterPosition());
        }

    }
    public interface Callback{
        //call an interface
        //declare the interface inside the needed class
        //implement the method within the interface into the needed function/method
        //add the declaration to the constructor of the class to be able to access it in another class
        void itemClick(int position);
    }

    public void setAnimalDataList(List<AnimalData> AnimalDataList) {
        this.AnimalDataList = AnimalDataList;
    }

}
