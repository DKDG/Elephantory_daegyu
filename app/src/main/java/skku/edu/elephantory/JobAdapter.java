package skku.edu.elephantory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

        ArrayList<Job> items = new ArrayList<Job>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.job_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    // 현재 인덱스에 맞는 Job 객체를 찾아 뷰홀더에 객체 설정
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Job item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Job item) {
        items.add(item);
    }

    public void setItems(ArrayList<Job> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        TextView textView3;
        TextView textView4;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textViewJobID);
            textView2 = itemView.findViewById(R.id.textViewJobName);
            textView3 = itemView.findViewById(R.id.textViewUser);
            textView4 = itemView.findViewById(R.id.textViewJobElapsedTime);
        }

        public void setItem(Job item) {
            textView.setText(item.job_id);
            textView2.setText(item.name);
            textView3.setText(item.user);
            textView4.setText(item.elapsed_time);
        }

    }

}