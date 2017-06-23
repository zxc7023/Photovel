package com.photovel.http;

public class Value {
    //기본
    public static String photovelURL = "http://www.photovel.com";
    //public static String photovelURL = "http://192.168.12.197:8080"; //은디언니꺼

    //content selectALL(GET), insert(POST), update(POST), delete 작업
    public static String contentURL = photovelURL+"/content/photo";
    //content에 따른 photo
    public static String contentPhotoURL = photovelURL+"/upload";
    //mainImage
    public static String mainImageURL = photovelURL+"/main_image";
    //mainImage에 따른 photo
    public static String mainImagePhotoURL = photovelURL+"/app/images/main";

    public static String userLoginURL= photovelURL+"/common/user/email";

    public static String userJoinURL = photovelURL+"/common/user/join";

    public static String userValidityCheckURL = "/common/user/idCheck";
}
