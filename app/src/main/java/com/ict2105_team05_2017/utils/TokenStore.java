package com.ict2105_team05_2017.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ict2105_team05_2017.model.Token;

/**
 * Created by Macharia on 2/22/2017.
 */

public class TokenStore {
    public static final String SP_NAME = "tokenDetails";
    private SharedPreferences tokenLocalDatabase;
    public TokenStore(Context context) {
        this.tokenLocalDatabase = context.getSharedPreferences(SP_NAME, 0);

    }

    public void setTokenStore(Token response) {
        SharedPreferences.Editor spEditor = tokenLocalDatabase.edit();

        spEditor.putString("token", response.getToken());

        spEditor.apply();

    }

    public Token getUserToken() {

        String token = tokenLocalDatabase.getString("token", "");
        Boolean success = tokenLocalDatabase.getBoolean("success", false);

        Token response = new Token( token);
        return response;
    }

    public boolean getToken() {
        if (tokenLocalDatabase.getBoolean("hasToken", false) == true) {
            return true;
        } else {
            return false;
        }
    }

    public void setToken(boolean hasToken) {
        SharedPreferences.Editor spEditor = tokenLocalDatabase.edit();
        spEditor.putBoolean("hasToken", hasToken);
        spEditor.apply();
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = tokenLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
