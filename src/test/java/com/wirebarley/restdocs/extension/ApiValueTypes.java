package com.wirebarley.restdocs.extension;

public enum ApiValueTypes {
    LONG,
    INTEGER,
    STRING,
    DOUBLE,
    BOOLEAN,
    DATETIME_STRING
    ;

    public String toArray() {
        return "[" + this + "]";
    }
}
