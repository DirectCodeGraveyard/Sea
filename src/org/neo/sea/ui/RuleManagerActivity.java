package org.neo.sea.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import org.neo.sea.R;
import org.neo.sea.rule.Rule;
import org.neo.sea.rule.RuleDatabase;

import java.util.List;

public class RuleManagerActivity extends Activity {
    private ListView listView;
    private RuleDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rule_manager);

        listView = (ListView) findViewById(R.id.rule_manager_rules);

        db = new RuleDatabase(getApplicationContext());
        List<Rule> rules = db.getRules();
    }
}
