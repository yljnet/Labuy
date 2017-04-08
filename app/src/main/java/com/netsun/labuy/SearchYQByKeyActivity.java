package com.netsun.labuy;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netsun.labuy.db.SearchHistory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.crud.DataSupport.findAll;

public class SearchYQByKeyActivity extends AppCompatActivity {
    AutoCompleteTextView keyEditView;
    ImageView backImage;
    TextView searchTexit;
    ListView searchHistoryListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> historyList;
    ImageView clearHistoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yq_by_key);
        backImage = (ImageView) findViewById(R.id.id_key_back_button);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //模拟返回键
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Instrumentation instrumentation = new Instrumentation();
                        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);//不能在主线程使用
                        finish();
                    }
                }).start();
            }
        });
        searchTexit = (TextView) findViewById(R.id.id_key_button_search);
        searchTexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSearch();
            }
        });
        keyEditView = (AutoCompleteTextView) findViewById(R.id.key_edit_text);
        keyEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                toSearch();
                return true;
            }
        });
        historyList = getHistoryList();
        searchHistoryListView = (ListView) findViewById(R.id.id_search_history_list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList);
        searchHistoryListView.setAdapter(adapter);
        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String message = historyList.get(i);
                Intent intent = new Intent(SearchYQByKeyActivity.this, MainActivity.class);
                intent.putExtra("work", "search_qy_by_key");
                intent.putExtra("name", message);
                startActivity(intent);
            }
        });
        clearHistoryImage = (ImageView) findViewById(R.id.id_clear_history_image);
        clearHistoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataSupport.deleteAll(SearchHistory.class) == historyList.size()) {
                    historyList.clear();
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    private void toSearch() {
        String produceName = keyEditView.getText().toString();
        Intent intent = new Intent(SearchYQByKeyActivity.this, MainActivity.class);
        SearchHistory history = new SearchHistory();
//        history.id = SearchHistory.count(SearchHistory.class) + 1;
        history.setMessage(produceName);
        history.save();
        intent.putExtra("work", "search_qy_by_key");
        intent.putExtra("name", produceName);
        startActivity(intent);
    }

    private ArrayList<String> getHistoryList() {
        ArrayList<String> list = new ArrayList<String>();
        List<SearchHistory> histories = findAll(SearchHistory.class);
        if (histories.size() > 0) {
            for (SearchHistory history : histories) {
                list.add(history.getMessage());
            }
        }
        return list;
    }
}
