package com.example.linhdq.searchapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linhdq.searchapp.adapter.SpinnerAdapter;
import com.example.linhdq.searchapp.model.QuestionModel;
import com.example.linhdq.searchapp.R;
import com.example.linhdq.searchapp.adapter.RecyclerViewAdapter;
import com.example.linhdq.searchapp.util.StringUtils;
import com.example.linhdq.searchapp.dbcontext.DBContext;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //
    private static final String SHARED_PREFERENCE = "fu_golden_key";
    private static final String SUBJECT_CODE = "subject_code";
    //view
    private RecyclerView recyclerView;
    private EditText edtKey;
    private Button btnClear;
    private ImageView btnSetting;
    private Spinner spinnerSubject;
    private TextView txtNumberQues;
    private Button btnGetMore;
    private Button btnOk;
    private TextView txtPath;
    private Button btnBrowse;
    private EditText edtSubjectName;
    private ImageView imvCorrect;
    private TextView txtMessage;
    private TextView txtMessageFile;
    private Button btnCancel;
    private Button btnLoadData;
    //dialog
    private ProgressDialog dialog;
    private Dialog dialogSetting;
    private Dialog dialogGetmore;
    //adapter
    private RecyclerViewAdapter adapter;
    private SpinnerAdapter spinnerApdater;
    //database
    private DBContext dbContext;
    //
    private List<QuestionModel> list;
    private List<String> listSubject;
    //
    private String filePath;
    private String fileName;
    private String fileUri;
    private boolean isFileAvailable;
    private boolean isAccept;
    //animation
    private Animation animationIn;
    private Animation animationOut;
    //
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        //permission
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            init();
            //
            addListener();
        }
        //set animation
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccept) {
            //
            getDataFromSharedPreference();
            //
            list = dbContext.getAllQuestionFormSubject(fileName);
            reloadData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccept) {
            //save sharedPreference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SUBJECT_CODE, fileName);
            editor.commit();
            //
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void init() {
        //view
        edtKey = (EditText) findViewById(R.id.edt_key);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnSetting = (ImageView) findViewById(R.id.btn_setting);
        recyclerView = (RecyclerView) findViewById(R.id.list_question);
        //dialog setting
        dialogSetting = new Dialog(this);
        dialogSetting.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogSetting.setContentView(R.layout.dialog_setting);
        dialogSetting.setCanceledOnTouchOutside(false);
        //dialog get more
        dialogGetmore = new Dialog(this);
        dialogGetmore.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogGetmore.setContentView(R.layout.dialog_get_more);
        dialogGetmore.setCanceledOnTouchOutside(false);
        //
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading file. Please wait...");
        dialog.setIndeterminate(false);
        dialog.setMax(0);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        //widgets in dialog setting
        spinnerSubject = (Spinner) dialogSetting.findViewById(R.id.spinner_subject);
        txtNumberQues = (TextView) dialogSetting.findViewById(R.id.txt_number_ques);
        btnGetMore = (Button) dialogSetting.findViewById(R.id.btn_get_more);
        btnOk = (Button) dialogSetting.findViewById(R.id.btn_ok);
        //widgets in dialog get more
        txtPath = (TextView) dialogGetmore.findViewById(R.id.txt_path);
        btnBrowse = (Button) dialogGetmore.findViewById(R.id.btn_browse);
        edtSubjectName = (EditText) dialogGetmore.findViewById(R.id.edt_subject_name);
        imvCorrect = (ImageView) dialogGetmore.findViewById(R.id.imv_correct);
        txtMessage = (TextView) dialogGetmore.findViewById(R.id.txt_message);
        txtMessageFile = (TextView) dialogGetmore.findViewById(R.id.txt_message_file);
        btnCancel = (Button) dialogGetmore.findViewById(R.id.btn_cancel);
        btnLoadData = (Button) dialogGetmore.findViewById(R.id.btn_load_data);
        //
        listSubject = new ArrayList<>();
        spinnerApdater = new SpinnerAdapter(this, R.layout.spinner_item, listSubject);
        //
        spinnerSubject.setAdapter(spinnerApdater);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        adapter = new RecyclerViewAdapter(list, this);
        recyclerView.setAdapter(adapter);
        //database
        dbContext = DBContext.getInst();
        //animation
        animationIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        animationOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        //
        sharedPreferences = this.getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        isAccept = true;
    }

    private void reloadData() {
        adapter.swap(list);
        if (list.size() != 0) {
            recyclerView.scrollToPosition(0);
        }
    }

    private void addListener() {
        btnClear.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnGetMore.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnLoadData.setOnClickListener(this);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
        edtKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = edtKey.getText().toString().toLowerCase().trim();
                list = dbContext.getQuestionContainkeyFromSubject(fileName, key);
                reloadData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSubjectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    imvCorrect.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.GONE);
                    btnLoadData.setEnabled(false);
                    return;
                }

                if (!dbContext.checkSubjectExist(s.toString().toLowerCase().trim())) {
                    imvCorrect.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.GONE);
                    if (isFileAvailable) {
                        btnLoadData.setEnabled(true);
                    }
                    Log.d("status", "fail");
                } else {
                    Log.d("status", "true");
                    imvCorrect.setVisibility(View.GONE);
                    txtMessage.setVisibility(View.VISIBLE);
                    btnLoadData.setEnabled(false);
                }
            }
        });
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtNumberQues.setText(String.format("%d questions", dbContext
                        .getNumberQuestionFromSubject(listSubject.get(position))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getDataFromSharedPreference() {
        fileName = sharedPreferences.getString(SUBJECT_CODE, "Unknow");
    }

    private List<QuestionModel> getDataFromFile() {
        List<QuestionModel> modelList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            File file = new File(fileUri);
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] question = line.split("\\|");
                if (question.length == 2) {
                    modelList.add(QuestionModel.create(fileName, StringUtils.unAccent(question[0].toLowerCase()),
                            question[1].toLowerCase()));
                }

            }
        } catch (IOException e) {
            Log.d("main_activity", "fail");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return modelList;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(), 0);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //for samsung
//        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
//        intent.putExtra("CONTENT_TYPE", "*/*");
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File"),
                    404);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getPath(Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    404);
        } else {
            init();
            //
            addListener();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 404) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
                //
                addListener();
            } else {
                moveTaskToBack(true);
                System.exit(1);
                this.finish();
            }
        }
    }

    private void resetWidgetInGetMore() {
        txtPath.setText("");
        edtSubjectName.setText("");
        txtMessageFile.setVisibility(View.GONE);
        txtMessage.setVisibility(View.GONE);
        imvCorrect.setVisibility(View.GONE);
        btnLoadData.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 404:
                try {
                    if (resultCode == RESULT_OK) {
                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        Log.d("File Uri:", uri.toString());
                        // Get the path
                        String path = getPath(uri);
                        fileUri = path;
                        filePath = path.substring(path.lastIndexOf("/") + 1);
                        txtPath.setText(filePath);
                        edtSubjectName.setText(filePath.substring(0, filePath.lastIndexOf(".")));
                        if (!filePath.substring(filePath.lastIndexOf(".")).equalsIgnoreCase(".txt")) {
                            txtMessageFile.setVisibility(View.VISIBLE);
                            btnLoadData.setEnabled(false);
                        } else {
                            txtMessageFile.setVisibility(View.GONE);
                            if (!dbContext.checkSubjectExist(edtSubjectName.getText()
                                    .toString().toLowerCase())) {
                                isFileAvailable = true;
                                btnLoadData.setEnabled(true);
                            }
                        }
                        Log.d("File Path: ", path);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Invalid file!",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                edtKey.setText("");
                list = dbContext.getAllQuestionFormSubject(fileName);
                reloadData();
                break;
            case R.id.btn_setting:
                listSubject.clear();
                listSubject.addAll(dbContext.getAllSubject());
                spinnerApdater.notifyDataSetChanged();
                if (listSubject.indexOf(fileName) >= 0) {
                    spinnerSubject.setSelection(listSubject.indexOf(fileName));
                }
                v.startAnimation(animationIn);
                animationOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dialogSetting.show();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animationOut);
                break;
            case R.id.btn_get_more:
                dialogSetting.dismiss();
                dialogGetmore.show();
                resetWidgetInGetMore();
                break;
            case R.id.btn_ok:
                dialogSetting.dismiss();
                if (listSubject.size() != 0) {
                    fileName = listSubject.get(spinnerSubject.getSelectedItemPosition());
                    list = dbContext.getAllQuestionFormSubject(fileName);
                    reloadData();
                }
                break;
            case R.id.btn_browse:
                showFileChooser();
                break;
            case R.id.btn_cancel:
                dialogGetmore.dismiss();
                //show dialog setting
                listSubject.clear();
                listSubject.addAll(dbContext.getAllSubject());
                spinnerApdater.notifyDataSetChanged();
                dialogSetting.show();
                break;
            case R.id.btn_load_data:
                dialogGetmore.dismiss();
                fileName = edtSubjectName.getText().toString().trim().toLowerCase();
                LoadData loadData = new LoadData();
                loadData.execute();
                break;
            default:
                break;
        }
    }

    private class LoadData extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getDataFromFile();
            publishProgress(list.size() * (-1));
            int i = 0;
            for (QuestionModel q : list) {
                DBContext.getInst().addQuestion(q);
                publishProgress(i + 1);
                i++;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
            if (values[0] < 0) {
                dialog.setMax(values[0] * (-1));
            } else {
                dialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dialog.setProgress(0);
            reloadData();
            listSubject.clear();
            listSubject.addAll(dbContext.getAllSubject());
            spinnerApdater.notifyDataSetChanged();
            if (listSubject.indexOf(fileName) >= 0) {
                spinnerSubject.setSelection(listSubject.indexOf(fileName));
            }
            dialogSetting.show();
        }
    }
}
