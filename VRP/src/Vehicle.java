import java.util.ArrayList;

/**
 * @Author: Nguyễn Duy Công
 * @subject: Tối ưu hóa
 * @class: Vehicle
 */

public class Vehicle {
    public ArrayList<Customer> route = new ArrayList<Customer>();
    public double localCost;
    public int vehCap;

    public Vehicle () {
        route.clear();
        localCost = 0;
        vehCap = 0;
    }

    public void addCustomer (Customer cus) {
        route.add(cus);
    }



    public void printVehicle () {
        System.out.print(this.getRoute().get(0).getId());
        for (int i = 1; i < this.getRoute().size(); i++) {
            System.out.print("->" + this.getRoute().get(i).getId());
        }
        System.out.print("\t| " + Math.round(this.getLocalCost()));
    }

    public ArrayList<Customer> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Customer> route) {
        this.route = route;
    }

    public double getLocalCost() {
        return localCost;
    }

    public void setLocalCost(double localCost) {
        this.localCost = localCost;
    }

    public int getVehCap() {
        return vehCap;
    }

    public void setVehCap(int vehCap) {
        this.vehCap = vehCap;
    }
}
