package com.favoreme.favore.api;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Backend {
    private static Backend ref = new Backend();
    private static Context mContext;
    private String mUrl = "http://192.168.43.119:3000";
    private OkHttpClient client;
    private Response mResponse =  null;
    private static Favore favore;
    public Backend(){}
    private String TAG = "Backend";


    public static Backend get(Context context){
        mContext = context;
        favore = Favore.get(context);
        return ref;
    }
    public  Call Signup(String email, String pass) throws IOException {
         client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "username="+email+"&password="+pass);
        Request request = new Request.Builder()
                .url(mUrl+"/signup")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .build();
        Call call = client.newCall(request);
        return call;
    }
    public String test(){
        client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "username=email+&password=pass");
        Request request = new Request.Builder()
                .url("https://httpbin.org/post")
                .post(body)

                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: OKHTTP"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: OKHTTP"+response);
            }
        });
        return null;
    }

    public Call Signin(String email,String pass) throws IOException{

         client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "username="+email+"&password="+pass);
        Log.i(TAG, "Signin: "+mUrl+"/signin");
        Request request = new Request.Builder()
                .url(mUrl+"/signin")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .build();
        Call call = client.newCall(request);
        return call;
    }

    public Call WritePost(double lon,double lat,String postText,long uid,long date) throws IOException{
         client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\n\t\"location\":{\"type\":\"point\",\"coordinates\":["+lon+","+lat+"]},\n\t\"favors\":0,\n\t\"postText\":\""+postText+"\",\n\t\"age\":0,\n\t\"isImage\":false,\n\t\"posterId\":"+uid+",\n\t\"date\":"+date+"\n}");
        Request request = new Request.Builder()
                .url(mUrl+"/user/posttext")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Authorization", "Bearer "+favore.getKey())
                .build();
        Call call = client.newCall(request);
        favore.toasty(favore.getKey());
        return call;
    }

    public Call WriteImagePost(double lon,double lat,long uid,long date,String path) throws IOException{
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("location","\"location\":{\"type\":\"point\",\"coordinates\":["+lon+","+lat+"]}")
                .addFormDataPart("favors","0")
                .addFormDataPart("imagePath",path)
                .addFormDataPart("age","0")
                .addFormDataPart("isImage","true")
                .addFormDataPart("date",date+"")
                .addFormDataPart("posterId",uid+"")
                .addFormDataPart("file",uid+date+".png",RequestBody.create(MediaType.parse("image/png"), new File(path)))
                .build();
        Request request = new Request.Builder()
                .url(mUrl+"/user/postimg/")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Authorization", "Bearer "+favore.getKey())
                .build();
        Call call = client.newCall(request);
        return call;
    }


    public String RemovePost(long id) throws IOException {
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/removePost/"+id)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                favore.toasty("Unable to remove the post");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResponse = response;
            }
        });
        return mResponse.body().string();
    }

    public Call FetchPosts(double lon,double lat) throws IOException{
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/fetchPosts?lon="+lon+"&lat="+lat+"&uid"+favore.getOwner().getUid())
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        Call call = client.newCall(request);
        return call;
    }


    public Call GetUserDetails(long uid) throws IOException {
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/getUserDetails/"+uid)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        Call call = client.newCall(request);
        return call;
    }
    public Call EditUser(int id,String fname, String lname,String dname,String email,String phone,String bio,String age,String path) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fname",fname+"")
                .addFormDataPart("lname",lname+"")
                .addFormDataPart("dname",dname+"")
                .addFormDataPart("phone",phone+"")
                .addFormDataPart("age",age+"")
                .addFormDataPart("bio",bio+"")
                .addFormDataPart("id",id+"")
                .addFormDataPart("file",id+".png",RequestBody.create(MediaType.parse("image/png"), new File(path)))
                .build();
        Request request = new Request.Builder()
                .url(mUrl+"/user/edituser/")
                .post(body)
                .addHeader("Authorization", "Bearer "+favore.getKey())
                .build();
        Call call = client.newCall(request);
        return call;
    }

    public String AddFriend(long usr,long frnd) throws IOException {
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/addfriend/?usr="+usr+"&frnd="+frnd)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                favore.toasty("Unable to add as friend");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResponse = response;
            }
        });
        return mResponse.body().string();
    }

    public String RemoveFriend(long usr,long frnd) throws IOException {
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/removefriend/?usr="+usr+"&frnd="+frnd)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                favore.toasty("Unable to remove him as friend");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResponse = response;
            }
        });
        return mResponse.body().string();
    }

    public String GetFriendList(long usr) throws IOException{
         client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl+"/user/getFriendList/"+usr)
                .get()
                .addHeader("Cache-Control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                favore.toasty("Unable to fetch friend list");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResponse = response;
            }
        });
        return mResponse.body().string();
    }

}
