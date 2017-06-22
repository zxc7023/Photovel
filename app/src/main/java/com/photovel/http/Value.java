package com.photovel.http;

public class Value {
    //기본
    public static String photovelURL = "http://www.photovel.com";

    //content selectALL(GET), insert(POST), update(POST), delete 작업
    //public static String contentURL = "http://192.168.12.197:8080/content/photo/";//은디언니꼬
    public static String contentURL = "http://www.photovel.com/content/photo";

    //content에 따른 photo
    //public static String contentPhotoURL = "http://192.168.12.197:8080/upload";
    public static String contentPhotoURL = "http://www.photovel.com/upload";

    //mainImage
    //public static String mainImageURL = "http://192.168.12.197:8080/main_image";
    public static String mainImageURL = "http://www.photovel.com/main_images";

    //mainImage에 따른 photo
    //public static String mainImagePhotoURL = "http://192.168.12.197:8080/app/images/main";
    public static String mainImagePhotoURL = "http://www.photovel.com/app/images/main";
}
