package com.photovel.http;

public class Value {
    //기본
    public static String photovelURL = "http://www.photovel.com";
    //content selectALL(GET), insert(POST), update(POST), delete 작업
    public static String contentURL = "http://www.photovel.com/content/photo";  //http://192.168.12.197:8080/content/photo/
    //content에 따른 photo
    public static String contentPhotoURL = "http://www.photovel.com/upload";    //http://192.168.12.197:8080/upload
    //mainImage
    public static String mainImageURL = "http://www.photovel.com/main_image";
    //mainImage에 따른 photo
    public static String mainImagePhotoURL = "http://www.photovel.com/app/image/main";
}
