package com.appttude.h_mal.exchangemap;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener{

    Context context;
    ListView listView;
    TextView textView;
    EditText editText;
    int selection;

    public CustomDialogClass(@NonNull Context context, TextView textView) {
        super(context);
        this.context = context;
        this.textView = textView;
        if (textView.getId() == R.id.currency_one){
            selection = 1;
        }else{
            selection = 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//
//        listView = (ListView) findViewById(R.id.list_view);
//        editText = (EditText) findViewById(R.id.search_text) ;
//
//        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,R.array.currency_arrays,android.R.layout.simple_list_item_1);
//        listView.setAdapter(arrayAdapter);
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                arrayAdapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                textView.setText(adapterView.getItemAtPosition(i).toString());
//                SharedPreferences.Editor editor = pref.edit();
//                if (selection == 1) {
//                    editor.putString(CURRENCY_ONE,adapterView.getItemAtPosition(i).toString());
//                }else{
//                    editor.putString(CURRENCY_TWO,adapterView.getItemAtPosition(i).toString());
//                }
//                editor.apply();
//                currencyOneEditText.setText("");
//                currencyTwoEditText.setText("");
//                String stringURL = UriBuilder(currencyOne.getText().toString().substring(0,3),
//                        currencyTwo.getText().toString().substring(0,3));
//                MyAsyncTask task = new MyAsyncTask();
//                task.execute(stringURL);
//                dismiss();
//            }
//        });
    }

    @Override
    public void onClick(View view) {

    }
}

