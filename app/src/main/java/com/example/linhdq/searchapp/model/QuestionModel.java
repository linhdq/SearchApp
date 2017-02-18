package com.example.linhdq.searchapp.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by LinhDQ on 11/16/16.
 */

public class QuestionModel extends RealmObject {
    @Index
    private String subjectCode;
    private String quesContent;
    private String answer;

    public static QuestionModel create(String subjectCode, String quesContent, String answer) {
        QuestionModel model = new QuestionModel();
        model.setSubjectCode(subjectCode);
        model.setQuesContent(quesContent);
        model.setAnswer(answer);
        return model;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getQuesContent() {
        return quesContent;
    }

    public void setQuesContent(String quesContent) {
        this.quesContent = quesContent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
