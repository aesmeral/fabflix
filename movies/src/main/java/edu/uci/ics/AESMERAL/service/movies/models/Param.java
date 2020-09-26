package edu.uci.ics.AESMERAL.service.movies.models;

public class Param {
    private Integer type;
    private Object param;
    private int location;

    private Param(Integer type, Object param, int location)
    {
        this.type = type;
        this.param = param;
        this.location = location;
    }

    public static Param create(Integer type, Object param, int location)
    {
        return new Param(type,param,location);
    }
    public Integer getType() {
        return type;
    }

    public Object getParam() {
        return param;
    }

    public int getLocation() {
        return location;
    }

}
