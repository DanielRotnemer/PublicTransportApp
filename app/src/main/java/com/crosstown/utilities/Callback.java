package com.crosstown.utilities;

import org.json.JSONException;

public interface Callback<T> {
    void callback(T arg);
}
