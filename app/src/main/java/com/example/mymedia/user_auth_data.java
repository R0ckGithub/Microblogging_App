package com.example.mymedia;

public class user_auth_data {
    String authuser,real_name;
    String authuser_url;
    String bio;
    int followers_cnt=0;
    int following_cnt=0;

    public user_auth_data(String authuser, String authuser_url, String bio, int followers_cnt, int following_cnt) {
        this.authuser = authuser;
        this.authuser_url = authuser_url;
        this.bio = bio;
        this.followers_cnt = followers_cnt;
        this.following_cnt = following_cnt;
    }

    public String getAuthuser() {
        return authuser;
    }

    public void setAuthuser(String authuser) {
        this.authuser = authuser;
    }

    public String getAuthuser_url() {
        return authuser_url;
    }

    public void setAuthuser_url(String authuser_url) {
        this.authuser_url = authuser_url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getFollowers_cnt() {
        return followers_cnt;
    }

    public void setFollowers_cnt(int followers_cnt) {
        this.followers_cnt = followers_cnt;
    }

    public int getFollowing_cnt() {
        return following_cnt;
    }

    public void setFollowing_cnt(int following_cnt) {
        this.following_cnt = following_cnt;
    }

    public user_auth_data(){}
    public user_auth_data(String authuser, String authuser_url) {
        this.authuser = authuser;
        this.authuser_url = authuser_url;
    }
}
