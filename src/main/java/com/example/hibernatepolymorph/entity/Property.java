package com.example.hibernatepolymorph.entity;

public interface Property<T> {

    String getName();

    T getValue();
}