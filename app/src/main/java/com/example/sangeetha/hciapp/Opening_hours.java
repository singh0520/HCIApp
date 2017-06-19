package com.example.sangeetha.hciapp;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sangeetha on 4/10/16.
 */
public class Opening_hours implements Serializable{

    @Key
    public List<String> weekday_text;

}
