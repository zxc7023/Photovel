package com.photovel.http;

public class Value {
    //기본
    public static String photovelURL = "http://www.photovel.com";

    //content selectALL(GET), insert(POST), update(POST), delete 작업
    public static String contentURL = "http://192.168.12.197:8080/content/photo";  //http://192.168.12.197:8080/content/photo/
    //content에 따른 photo
    public static String contentPhotoURL = "http://192.168.12.197:8080/upload";    //http://192.168.12.197:8080/upload
    //mainImage
    public static String mainImageURL = "http://192.168.12.197:8080/main_image";
    //mainImage에 따른 photo
    public static String mainImagePhotoURL = "http://192.168.12.197:8080/app/images/main";
}
