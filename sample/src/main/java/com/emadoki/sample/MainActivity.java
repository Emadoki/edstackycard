package com.emadoki.sample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.emadoki.edstackycard.EdStackyCard;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private EdStackyCard edStackyCard;
    private TextView txtStatus;
    private Button btnRefresh, btnClear, btnAdd;
    private ArrayList<MyObject> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = new ArrayList<MyObject>();
        list.addAll(generateList());
        edStackyCard = (EdStackyCard) findViewById(R.id.edStackyCard);
        edStackyCard.setAdapter(new MyAdapter(this));

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        ClickListener clickListener = new ClickListener();
        btnRefresh.setOnClickListener(clickListener);
        btnClear.setOnClickListener(clickListener);
        btnAdd.setOnClickListener(clickListener);

        edStackyCard.setOnInteractListener(new EdStackyCard.OnInteractListener()
        {
            @Override
            public void click(View view, int position)
            {
                txtStatus.setText("click " + position);
            }

            @Override
            public void dismiss(View view, int position)
            {
                txtStatus.setText("dismiss " + position);
            }
        });
    }

    private ArrayList<MyObject> generateList()
    {
        ArrayList<MyObject> temp = new ArrayList<MyObject>();
        temp.add(new MyObject("Test 0", Color.parseColor("#F44336")));
        temp.add(new MyObject("Test 1", Color.parseColor("#E91E63")));
        temp.add(new MyObject("Test 2", Color.parseColor("#9C27B0")));
        temp.add(new MyObject("Test 3", Color.parseColor("#3F51B5")));
        temp.add(new MyObject("Test 4", Color.parseColor("#2196F3")));
        temp.add(new MyObject("Test 5", Color.parseColor("#4CAF50")));
        temp.add(new MyObject("Test 6", Color.parseColor("#CDDC39")));
        temp.add(new MyObject("Test 7", Color.parseColor("#FFEB3B")));
        temp.add(new MyObject("Test 8", Color.parseColor("#FFC107")));
        temp.add(new MyObject("Test 9", Color.parseColor("#FF9800")));

        return temp;
    }

    private class ClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId()){
                case R.id.btnRefresh:
                    //edStackyCard.getAdapter().notifyDataSetChanged();
                    break;
                case R.id.btnClear:
                    list.clear();
                    edStackyCard.getAdapter().notifyDataSetChanged();
                    break;
                case R.id.btnAdd:
                    list.clear();
                    list.addAll(generateList());
                    edStackyCard.getAdapter().notifyDataSetChanged();
                    break;
            }
        }
    }

    private class MyAdapter extends ArrayAdapter<String>
    {
        public MyAdapter(Context context)
        {
            super(context, 0);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            MyHolder myHolder;
            if (view == null)
            {
                view = getLayoutInflater().inflate(R.layout.item_card, parent, false);
                view.setTag(myHolder = new MyHolder(view));
            }
            else
            {
                myHolder = (MyHolder) view.getTag();
            }

            MyObject object = list.get(position);
            myHolder.textView.setText(object.text);
            myHolder.textView.setBackgroundColor(object.color);
            return view;
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        private class MyHolder
        {
            public TextView textView;

            public MyHolder(View view)
            {
                textView = (TextView) view.findViewById(R.id.textView);
            }
        }
    }

    private class MyObject
    {
        public String text;
        public int color;
        public MyObject(String text, int color)
        {
            this.text = text;
            this.color = color;
        }
    }
}
