package com.mohammadkiani.sqlitedemo.model;

public class Employee {
    int id;
    String name, department, joiningDate;
    double salary;

    public Employee(int id, String name, String department, String joiningDate, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.joiningDate = joiningDate;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public double getSalary() {
        return salary;
    }
}
