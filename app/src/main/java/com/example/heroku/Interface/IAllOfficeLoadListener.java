package com.example.heroku.Interface;

import java.util.List;

public interface IAllOfficeLoadListener {
    void onAllOfficeLoadSuccess(List<String> areaNameList);
    void onAllOfficeLoadFailed(String message);
}
