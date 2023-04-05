package com.example.shared_calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Adapter_friends_list extends RecyclerView.Adapter<Adapter_friends_list.ViewHolder> {
    public ArrayList<Sign_up_item> MemoList;
    public Adapter_friends_list(ArrayList<Sign_up_item> data){
        MemoList = data;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);

    }

    // OnitemClickListener 참조 변수 선언
    private OnItemClickListener mListener=null;

    // OnitemClickListener 전달 메소드
    public void setOnitemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater -> XML에 정의된 Resource를 View 객체로 반환해주는 역할
        //inflat -> xml에 표기된 레이아웃들을 메모리에 객체화시키는 행동
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_recyclerview, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder Holder, int position) {
        Sign_up_item item = MemoList.get(position);
        Log.e("CalanderAdapter",String.valueOf(position));  //포지션값 들고오는거
        Log.e("CalanderAdapter",item.getID());            //인덱스값 들고온다

        Holder.friends_ID.setText("ID:"+item.getID());
        Holder.friends_NAME.setText("NAME:"+item.getName());


        Bitmap bm = BitmapFactory.decodeFile(item.getImage());
        if(bm == null){
            Holder.friend_image.setImageResource(R.drawable.ic_launcher_foreground);
        }else{
            Holder.friend_image.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
        }

    }

    @Override
    public int getItemCount() {
        return MemoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView friends_ID, friends_NAME;
        ImageView friend_image;


        public ViewHolder(View itemView) {
            super(itemView);

            friends_ID = (TextView) itemView.findViewById(R.id.friend_ID);
            friends_NAME = (TextView)itemView.findViewById(R.id.friend_name);
            friend_image = (ImageView)itemView.findViewById(R.id.friend_image);

            friend_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onItemClick(view,position);
                        }
                    }
                }
            });
            friends_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onItemClick(view,position);
                        }
                    }
                }
            });

            friends_NAME.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onItemClick(view,position);
                        }
                    }
                }
            });
        }
    }

}

