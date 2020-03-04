package com.groupfive.satapp.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.groupfive.satapp.data.repositories.UserSatAppRepository;
import com.groupfive.satapp.models.auth.AuthLoginUser;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.internal.connection.Exchange;

public class UserViewModel extends AndroidViewModel {

    private UserSatAppRepository userSatAppRepository;
    private MutableLiveData<AuthLoginUser> user;
    private MutableLiveData<List<AuthLoginUser>> allUser;
    private MutableLiveData<List<AuthLoginUser>> usersValidated;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userSatAppRepository = new UserSatAppRepository();
    }

    public MutableLiveData<AuthLoginUser> getUser(){
        user = userSatAppRepository.getUser();
        return user;
    }

    public void AllUser(){
        allUser = userSatAppRepository.getAllUsers();
    }

    public MutableLiveData<List<AuthLoginUser>> getAllUser(){
        return allUser;
    }

    public void UsersValidated(){
        usersValidated = userSatAppRepository.getUsersValidate();
    }

    public MutableLiveData<List<AuthLoginUser>> getUsersValidated(){
        return usersValidated;
    }

    public MutableLiveData<ResponseBody> getPicture(String id){
        return userSatAppRepository.getPicture(id);
    }

    public void putValidated(String id){
        userSatAppRepository.putValidated(id);
    }
}