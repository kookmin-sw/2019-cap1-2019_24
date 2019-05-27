package com.example.WITTYPHOTOS;

import java.util.ArrayList;

public class ImageTagInfo {

    public static final int TAG_TYPE_LOCAL = 0;
    public static final int TAG_TYPE_REMOTE = 1;

    public String fileName;
    public int id;
    public String filePath;
    public String created;
    public ArrayList<String> tags;
    public int tagType;
}
