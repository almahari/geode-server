package org.example;

public class Parent {
    private Long id;
    private String name;
    private Double income;

    public Parent() {}

    public Parent(Long id, String name, Double income) {
        this.id = id;
        this.name = name;
        this.income = income;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Double getIncome() {
        return this.income;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "Parent [Id=" + id + ", name=" + name + ", income=" + income + "]";
    }
}