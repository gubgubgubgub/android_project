package com.example.shared_calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter_memo extends RecyclerView.Adapter<Adapter_memo.ViewHolder> {

    public ArrayList MemoList;


    public Adapter_memo(ArrayList data){
        MemoList = data;
    }
    public interface OnItemClickListener{
        void onItemClick(View v, int position); //뷰와 포지션값
        void onChangeClick(View v, int position); //수정
        void onDeleteClick(View v, int position);//삭제
    }

    String get_list(int position){
        String get_memo;

        get_memo = (String) MemoList.get(position);
        Log.d("find error adapta",get_memo );

        return get_memo;
    }

    // OnitemClickListener 참조 변수 선언
    private OnItemClickListener mListener=null;

    // OnitemClickListener 전달 메소드
    public void setOnitemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    //list안에 들어갈 아이템들에 대한 정의를 하는 부분이다.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_data;
        Button change_button, delete_button;


        public ViewHolder(View itemView) {
            super(itemView);
            text_data = itemView.findViewById(R.id.text_data);
            change_button = itemView.findViewById(R.id.change_button);
            delete_button = itemView.findViewById(R.id.delete_button);

            text_data.setOnClickListener(new View.OnClickListener() {
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

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onDeleteClick(view,position);
                        }
                    }
                }
            });

            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onChangeClick(view,position);
                        }
                    }
                }
            });


        }

    }

    @NonNull
    @Override
    //1번에서 만든 viewholder를 생성하는 부분이다
    //viewholder 를 만드는 부분
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater -> XML에 정의된 Resource를 View 객체로 반환해주는 역할
        //inflat -> xml에 표기된 레이아웃들을 메모리에 객체화시키는 행동
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_recyclerview, parent, false);
        ViewHolder holder = new ViewHolder(view);
        Log.e("CalanderAdapter","ViewHolder ");

        return holder;
    }


    @Override
    //view홀더가 재활용할때 호출되는 메소드
    //position은 해당 viewholder의 위치 index를 나타냄 // 2번째

    public void onBindViewHolder(@NonNull ViewHolder Holder, int position) {

        Log.e("CalanderAdapter",String.valueOf(position));  //포지션값 들고오는거
       // Log.e("CalanderAdapter",item.getMemo());            //인덱스값 들고온다

        Holder.text_data.setText(MemoList.get(position).toString());

    }

    @Override
    //어뎁터가 가지고있는 갯수를 지정해주면 된다 1번째
    public int getItemCount() {
        return MemoList.size();
    }
}
