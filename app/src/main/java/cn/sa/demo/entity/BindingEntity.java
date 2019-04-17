package cn.sa.demo.entity;


public class BindingEntity {

    private String type;

    public BindingEntity(String type) {
        this.type = type;
    }

    public void setName(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }

}
