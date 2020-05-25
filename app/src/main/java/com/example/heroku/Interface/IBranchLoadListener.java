package com.example.heroku.Interface;

import com.example.heroku.models.ModelOffice;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<ModelOffice> officeList);
    void onBranchLoadFailed(String message);
}
