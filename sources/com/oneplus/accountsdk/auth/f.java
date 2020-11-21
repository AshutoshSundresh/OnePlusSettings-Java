package com.oneplus.accountsdk.auth;

final class f {
    static String a(String str) {
        return "1000".equals(str) ? "Successfully obtained network data" : "2000".equals(str) ? "Get cached data successfully" : "1001".equals(str) ? "Account not logged in" : "1002".equals(str) ? "Account login failed" : "1003".equals(str) ? "operation failed" : "2001".equals(str) ? "network anomaly" : "3040".equals(str) ? "Login status has expired" : "3013".equals(str) ? "Account exception" : "3014".equals(str) ? "Cancel operation" : "2003".equals(str) ? "Missing android.permission.GET_ACCOUNTS permission" : "2004".equals(str) ? "OnePlus Account app is not exist" : "2005".equals(str) ? "This is a reserved method. And this version is not supported" : "5000".equals(str) ? "Bind success" : "6000".equals(str) ? "Get token success" : "operation failed";
    }
}
