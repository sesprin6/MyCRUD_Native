package com.mycrud.library;

public class Obj_Position
{
    String id;
    String position;

    public Obj_Position(String id, String position)
    {
        this.id = id;
        this.position = position;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return position;
    }
}
