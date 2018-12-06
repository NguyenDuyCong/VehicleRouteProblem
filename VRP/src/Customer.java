/**
 * @Author: Nguyễn Duy Công
 * @subject: Tối ưu hóa
 * @class: Customer
 */

public class Customer {

    private Point locate;  //tọa độ
    private int weight;  //trọng lượng đơn hàng
    private int id; //vị trí của khách hàng
    private boolean routed;

    public Customer () { }

    public Customer(Point locate, int weight, int id) {
        this.locate = locate;
        this.weight = weight;
        this.id = id;
        routed = false;
    }
    public Customer(Point locate, int id){
        this.locate = locate;
        this.id = id;
        routed = false;
    }

    public Point getLocate() {
        return locate;
    }

    public void setLocate(Point locate) {
        this.locate = locate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRouted() {
        return routed;
    }

    public void setRouted(boolean routed) {
        this.routed = routed;
    }
}
