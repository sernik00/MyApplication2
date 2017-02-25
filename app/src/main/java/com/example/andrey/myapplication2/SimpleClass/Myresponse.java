package com.example.andrey.myapplication2.SimpleClass;

/**
 * Created by 1 on 02.02.2017.
 */

public class Myresponse
    {
        public enum MyStatus { Ok, Badrequest }
        public String Message;
        public MyStatus Status;
        public int Code;

        public Myresponse(int Code, String message, MyStatus status){
            this.Code=Code;
            this.Message=message;
            this.Status=status;
        }

        public static Myresponse GetBadRequest(int Code, String message)
        {
            return new Myresponse (Code, message, MyStatus.Badrequest);
        }
        public static Myresponse GetOk(String message)
        {
            return new Myresponse (0,message,  MyStatus.Ok );
        }
    }