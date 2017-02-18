package com.example.linhdq.searchapp.dbcontext;

import android.util.Log;

import com.example.linhdq.searchapp.model.QuestionModel;
import com.example.linhdq.searchapp.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import io.realm.Realm;

/**
 * Created by LinhDQ on 11/16/16.
 */

public class DBContext {
    private Realm realm;

    public DBContext() {
        this.realm = Realm.getDefaultInstance();
    }

    private static DBContext inst;

    public static DBContext getInst() {
        if (inst == null) {
            inst = new DBContext();
        }
        return inst;
    }

    public int getNumberQuestionFromSubject(String subjectCode) {
        realm = Realm.getDefaultInstance();
        return (int) realm.where(QuestionModel.class).equalTo("subjectCode", StringUtils.unAccent(subjectCode))
                .count();
    }

    public void addQuestion(QuestionModel model) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(model);
        realm.commitTransaction();
    }

    public List<QuestionModel> getAllQuestionFormSubject(String subjectCode) {
        realm = Realm.getDefaultInstance();
        return realm.where(QuestionModel.class).equalTo("subjectCode", StringUtils.unAccent(subjectCode))
                .findAll();
    }

    public List<QuestionModel> getQuestionContainkeyFromSubject(String subjectCode, String key) {
        return realm.where(QuestionModel.class).equalTo("subjectCode", StringUtils.unAccent(subjectCode))
                .contains("quesContent", StringUtils.unAccent(key)).findAll();
    }

    public List<String> getAllSubject() {
        realm = Realm.getDefaultInstance();
        List<QuestionModel> list = realm.where(QuestionModel.class).distinct("subjectCode").sort("subjectCode");
        List<String> listSubject = new ArrayList<>();
        Log.d("sumber_of_subject", String.valueOf(list.size()));
        for (QuestionModel q : list) {
            listSubject.add(q.getSubjectCode());
        }
        return listSubject;
    }

    public boolean checkSubjectExist(String subjectCode) {
        realm = Realm.getDefaultInstance();
        if (realm.where(QuestionModel.class).equalTo("subjectCode", StringUtils.unAccent(subjectCode))
                .findFirst() != null) {
            return true;
        }
        return false;
    }
}
