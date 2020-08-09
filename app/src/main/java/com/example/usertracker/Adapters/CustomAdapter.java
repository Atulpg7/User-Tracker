package com.example.usertracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usertracker.Models.TagDetails;
import com.example.usertracker.R;

import org.w3c.dom.Text;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    List<TagDetails> list;

    public CustomAdapter(Context context, List<TagDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View  view= LayoutInflater.from(context).inflate(R.layout.users_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        TagDetails tag = list.get(position);

        holder.sn_no.setText(String.valueOf(tag.getS_no()));
        holder.username.setText(tag.getName());
        holder.tag_id.setText(tag.getTag_id());
        holder.time_stamp.setText(tag.getInsertTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sn_no,username,tag_id,time_stamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sn_no = itemView.findViewById(R.id.sn_no);
            username = itemView.findViewById(R.id.username);
            tag_id = itemView.findViewById(R.id.tag_id);
            time_stamp = itemView.findViewById(R.id.timestamp);
        }
    }
}
