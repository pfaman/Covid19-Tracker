package com.hellosolver.covid19tracker;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    public static Retrofit retrofit=null;
    public static APIInterface getAPIInterface(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(APIInterface.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(APIInterface.class);
    }
}
