package com.ict2105_team05_2017.model;

/**
 * Created by Macharia on 2/22/2017.
 */

public class Token {
  private   String token;


  public Token(String token) {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }



  @Override
  public String toString() {
    return "Token{" +
            "token='" + token + '\'' +

            '}';
  }
}
