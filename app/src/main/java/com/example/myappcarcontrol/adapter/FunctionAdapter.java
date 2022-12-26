package com.example.myappcarcontrol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappcarcontrol.OnChooseFunction;
import com.example.myappcarcontrol.R;
import com.example.myappcarcontrol.model.Function;

import java.util.ArrayList;

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Function> mFunction;
    private int row_index = -1;
    OnChooseFunction onChooseFunction;

    public FunctionAdapter(Context mContext, ArrayList<Function> mFunction, OnChooseFunction onChooseFunction) {
        this.mContext = mContext;
        this.mFunction = mFunction;
        this.onChooseFunction = onChooseFunction;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFunctionName;
        private ImageView mImageFunction;
        LinearLayout ln_layout_function;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFunctionName = (itemView).findViewById(R.id.btn_functionDK);
            ln_layout_function = itemView.findViewById(R.id.item_function);
            mImageFunction = itemView.findViewById(R.id.image_function);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rcv_function, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Function function = mFunction.get(position);
        holder.mFunctionName.setText(function.getName());
        holder.mImageFunction.setImageResource(function.getImage());
        holder.ln_layout_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                //mFunction.get(position).setChoosed(true);
                onChooseFunction.onChoose(mFunction.get(position));
            }
        });
        if (row_index == position) {
            holder.ln_layout_function.setBackgroundResource(R.drawable.linear_function_choosed);
        } else {
            holder.ln_layout_function.setBackgroundResource(R.drawable.ln_function_no_choosed);
        }
    }

    @Override
    public int getItemCount() {
        return mFunction.size();
    }

}
