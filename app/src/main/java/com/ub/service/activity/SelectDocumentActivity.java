package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel2.dialog.AddFileFromFavoriteDialog;

public class SelectDocumentActivity extends Activity implements View.OnClickListener, com.kloudsync.techexcel2.dialog.AddFileFromDocumentDialog.OnDocSelectedListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener {
    TextView selectTeamText;
    TextView selectFavoriteText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_document);
        selectFavoriteText = findViewById(R.id.txt_select_favorite);
        selectTeamText = findViewById(R.id.txt_select_team);
        selectTeamText.setOnClickListener(this);
        selectFavoriteText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_select_favorite:
                openFavoriteDocument();
                break;
            case R.id.txt_select_team:
                openTeamDocument();
                break;
        }
    }
  AddFileFromDocumentDialog AddFileFromDocumentDialog;

    private void openTeamDocument() {

        if (AddFileFromDocumentDialog != null) {
            AddFileFromDocumentDialog.dismiss();
        }
        AddFileFromDocumentDialog = new AddFileFromDocumentDialog(this);
        AddFileFromDocumentDialog.setOnSpaceSelectedListener(this);
        AddFileFromDocumentDialog.show();
    }

    private void openFavoriteDocument(){
        addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(this);
        addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(this);
        addFileFromFavoriteDialog.show();
    }

    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;

    @Override
    public void onDocSelected(String docId) {

    }

    @Override
    public void onFavoriteDocSelected(String docId) {

    }
}
