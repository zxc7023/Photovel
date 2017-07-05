package com.photovel.http;

public class Value {

    //public static String photovelURL = "http://www.photovel.com"; //서버님꺼
    public static String photovelURL = "http://192.168.12.197:8080"; //은디님꺼
    //public static String photovelURL = "http://192.168.35.27:8080"; //은디집님꺼 삭제 ㄴㄴ
    //public static String photovelURL = "http://192.168.12.44:8888"; //준기님꺼
    //public static String photovelURL = "http://192.168.1.2:8888"; //준기집님꺼 삭제 ㄴㄴ
    //public static String photovelURL = "http://192.168.12.22:8080"; //하라님꺼
    //public static String photovelURL ="http://192.168.1.2:8888"; //준기집용
    //public static String photovelURL ="http://192.168.12.23:8888"; //kitre
    //public static String photovelURL = "http://172.30.1.3:8080"; //하라집님꺼 삭제 ㄴㄴ

    //content selectALL(GET), insert(POST), update(POST), delete 작업
    public static String contentURL = photovelURL+"/content/photo";
    //content에 따른 photo
    public static String contentPhotoURL = photovelURL+"/upload";
    //mainImage
    public static String mainImageURL = photovelURL+"/main_image";
    //mainImage에 따른 photo
    public static String mainImagePhotoURL = photovelURL+"/app/images/main";

    public static String userLoginURL= photovelURL+"/common/user/email";

    public static String userLoginWithFBURL= photovelURL+"/common/user/facebook";

    public static String userJoinURL = photovelURL+"/common/user/join";

    public static String userValidityCheckURL = photovelURL + "/common/user/idCheck";

    public static String userCompareURL = photovelURL + "/common/user";

    public static String userLogoutURL = photovelURL + "/common/user/logout";

    public static String userPushTokenUpdateURL = photovelURL + "/common/user/push/update";

    public static String usergetPushTokenURL = photovelURL + "/common/user/push/getPushToken";
}
