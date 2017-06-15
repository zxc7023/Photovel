package com.kitri.photovel.content;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kitri.photovel.R;
import com.kitri.vo.ContentDetail;
import com.kitri.vo.Photo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HARA on 2017-06-07.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private ArrayList<ContentDetail> mDataset;
    private RadioButton lastCheckedRB = null;
    private Context mcontext;
    private ViewHolder holder;
    private int myear, mmonth, mday, position, a = 0;
    PhotoAdapter pa = null;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public ViewHolder getHolder() {
        return holder;
    }
    public void setHolder(ViewHolder holder) {
        this.holder = holder;
    }

    public PhotoAdapter() {
    }

    public PhotoAdapter(ArrayList<ContentDetail> myDataset, Context mycontext) {
        mDataset = myDataset;
        mcontext = mycontext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivphoto;
        public TextView tvLocation, tvDate;
        public Button btnDelete;
        public RadioGroup radioG;
        public RadioButton btnRadio;
        public EditText etContent;

        public ViewHolder(View view) {
            super(view);
            ivphoto = (ImageView) view.findViewById(R.id.ivphoto);
            tvLocation = (TextView) view.findViewById(R.id.tvLocation);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            btnDelete = (Button) view.findViewById(R.id.btnDelete);
            radioG = (RadioGroup) view.findViewById(R.id.radioG);
            btnRadio = (RadioButton) view.findViewById(R.id.btnRadio);
            etContent = (EditText) view.findViewById(R.id.etContent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_photo_list_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        this.holder = holder;
        this.position = position;

        final Date date=mDataset.get(position).getPhoto().getPhoto_date();
        String date2=null;
        try {
            date2 = new SimpleDateFormat("yyyy.MM.dd").format(date);
            String str[] = date2.split("\\.");
            myear = Integer.parseInt(str[0]);
            mmonth = Integer.parseInt(str[1]);
            mday = Integer.parseInt(str[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //사진
        holder.ivphoto.setImageBitmap(mDataset.get(position).getPhoto().getBitmap());

        //날짜
        holder.tvDate.setText(date2);
        holder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pa = new PhotoAdapter();
                pa.setPosition(position);
                pa.setHolder(holder);

                final Date date=mDataset.get(pa.getPosition()).getPhoto().getPhoto_date();
                String date2=null;
                try {
                    date2 = new SimpleDateFormat("yyyy.MM.dd").format(date);
                    String str[] = date2.split("\\.");
                    myear = Integer.parseInt(str[0]);
                    mmonth = Integer.parseInt(str[1]);
                    mday = Integer.parseInt(str[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DatePickerDialog datepicker = new DatePickerDialog(mcontext, dateSetListener, myear, mmonth-1, mday);
                datepicker.show();
            }
        });

        //위치
        //Log.i("ddd address",mDataset.get(position).getPhoto().getAddress()+"");
        holder.tvLocation.setText(mDataset.get(position).getPhoto().getAddress());
        holder.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pa = new PhotoAdapter();
                pa.setPosition(position);
                pa.setHolder(holder);
                Intent intent=new Intent(mcontext, PhotoGoogleMap.class);
                //"주소 미확인"이 아닐시 주소 보내줘서 그 위치 켜지게 만들기
                ((Activity)mcontext).startActivityForResult(intent,2);
            }
        });

        //컨텐츠
        holder.etContent.setTag(position);
        holder.etContent.setText(mDataset.get(position).getDetail_content());
        holder.etContent.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mDataset.get((int)holder.etContent.getTag()).setDetail_content(editable.toString());
            }
        });

        //삭제
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(position);
            }
        });

        //라디오버튼
        holder.radioG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int i=0;
                RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);
                checked_rb.setChecked(true);
                mDataset.get(position).getPhoto().setPhoto_top_flag(i++);
                Log.i("aaa","position : "+position);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                    mDataset.get(position).getPhoto().setPhoto_top_flag(i--);
                    Log.i("aaa","lastCheckedRB : "+lastCheckedRB);
                    Log.i("aaa","1----------- : ");
                }
                Log.i("aaa","2----------- : ");
                lastCheckedRB = checked_rb;
            }
        });
    }

    //구글지도address표시해주기
    public void photoGoogleMapResult(Intent intnet) {
        mDataset.get(pa.getPosition()).getPhoto().setPhoto_latitude(intnet.getDoubleExtra("latitude",0));
        mDataset.get(pa.getPosition()).getPhoto().setPhoto_longitude(intnet.getDoubleExtra("longitude",0));
        mDataset.get(pa.getPosition()).getPhoto().setAddress(intnet.getStringExtra("address"));
        pa.getHolder().tvLocation.setText(mDataset.get(pa.getPosition()).getPhoto().getAddress());
    }

    //datePicker에서 날짜선택하고 확인
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
        private Date date;
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            StringBuilder strBild;
            strBild = new StringBuilder().append(year).append(".");
            if(month <= 9 ){
                strBild.append("0");
            }
            strBild.append(month+1).append(".");
            if(dayOfMonth <= 9 ){
                strBild.append("0");
            }
            strBild.append(dayOfMonth);
            String strDate1 = strBild.toString();

            strBild.append(" ");
            Date temp = new Date();
            strBild.append(new SimpleDateFormat("hh:mm:ss").format(temp));
            String strDate2 = strBild.toString();
            try {
                date = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").parse(strDate2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            pa.getHolder().tvDate.setText(strDate1);
            mDataset.get(pa.getPosition()).getPhoto().setPhoto_date(date);
        }
    };

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    
    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }
}
