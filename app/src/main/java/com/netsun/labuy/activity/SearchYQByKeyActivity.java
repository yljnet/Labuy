package com.netsun.labuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.netsun.labuy.R;
import com.netsun.labuy.adapter.SearchHistoryAdapter;
import com.netsun.labuy.db.SearchHistory;
import com.netsun.labuy.utils.PublicFunc;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SearchYQByKeyActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView titleTV;
    EditText keyEditView;
    Button searchButton;
    RadioGroup radioGroup;
    ListView searchHistoryListView;
    SearchHistoryAdapter adapter;
    List<SearchHistory> historyList;
    ImageView clearHistoryImage;
    String mode = PublicFunc.DEVICER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_yq_by_key);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTV = (TextView) findViewById(R.id.tool_bar_title_text);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        searchButton = (Button) findViewById(R.id.id_key_button_search);
        keyEditView = (EditText) findViewById(R.id.key_edit_text);
        titleTV.setText("关键字搜索");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rbId = radioGroup.getCheckedRadioButtonId();
                switch (rbId) {
                    case R.id.devicer_rb:
                        mode = PublicFunc.DEVICER;
                        break;
                    case R.id.part_rb:
                        mode = PublicFunc.PART;
                        break;
                    case R.id.method_rb:
                        mode = PublicFunc.METHOD;
                        break;
                    default:
                        break;
                }
            }
        });
        searchHistoryListView = (ListView) findViewById(R.id.id_search_history_list_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFunc.closeKeyBoard(SearchYQByKeyActivity.this);
                finish();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSearch();
            }
        });

        keyEditView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        setEditTextInhibitInputSpace(keyEditView);
        keyEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                toSearch();
                PublicFunc.closeKeyBoard(SearchYQByKeyActivity.this);
                return true;
            }
        });
        historyList = DataSupport.findAll(SearchHistory.class);
        adapter = new SearchHistoryAdapter(this, historyList);
        searchHistoryListView.setAdapter(adapter);
        searchHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchHistory item = historyList.get(i);
                Intent intent = new Intent(getBaseContext(), SearchResultActivity.class);
                intent.putExtra("mode", item.getMode());
                intent.putExtra("type", PublicFunc.SEARCH_BY_KEY);
                intent.putExtra("name", item.getMessage());
                startActivity(intent);
                finish();
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
        if (produceName.isEmpty()) return;
        SearchHistory history = new SearchHistory();
        history.setMessage(produceName);
        history.setMode(mode);
        history.save();
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("mode", mode);
        intent.putExtra("type", PublicFunc.SEARCH_BY_KEY);
        intent.putExtra("name", produceName);
        startActivity(intent);
        finish();
    }

    private void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) return "";
                else
                    return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    private void updateList() {
        historyList.clear();
        historyList.addAll(DataSupport.findAll(SearchHistory.class));
        adapter.notifyDataSetChanged();
    }
}
