import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @Author: Nguyễn Duy Công
 * @subject: Tối ưu hóa
 * @class: VRP
 */

public class VRP {

    private static ArrayList<Customer> listCustomer = new ArrayList<Customer>();
    private static int numCustomer;
    private static int capacity;

    /**
     * read data from source file
     *
     * @param pathFile path to file
     */
    public static void readData(String pathFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathFile));

            //NAME, COMMENT, TYPE
            for (int i = 0; i < 3; i++) {
                br.readLine();
            }

            //DIMENSION
            String line = br.readLine();
            String[] splitLine = line.split("\t");
            numCustomer = Integer.valueOf(splitLine[1]);

            //EDGE_WEIGHT_TYPE
            br.readLine();

            //CAPACITY
            line = br.readLine();
            splitLine = line.split("\t");
            capacity = Integer.valueOf(splitLine[1]);

            //NODE_COORD_SECTION
            br.readLine();

            for (int i = 0; i < numCustomer; i++) {
                line = br.readLine();
                splitLine = line.split("\t");
                listCustomer.add(new Customer(new Point(Integer.valueOf(splitLine[1]), Integer.valueOf(splitLine[2])), i));
            }


            //DEMAND_SECTION
            br.readLine();

            for (int i = 0; i < numCustomer; i++) {
                line = br.readLine();
                splitLine = line.split("\t");
                listCustomer.get(i).setWeight(Integer.valueOf(splitLine[1]));
            }

            //DEPOT_SECTION, 1, -1, EOF
            for (int i = 0; i < 4; i++) {
                br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * calculator distance among two point
     * @param c1 customer 1
     * @param c2 customer 2
     * @return distance
     */
    public static double distanceCalculator(Customer c1, Customer c2) {
        double _distance = 0.0;

        if (c1 == null || c2 == null) return 0.0;

        _distance = Math.sqrt(Math.pow(c1.getLocate().getX() - c2.getLocate().getX(), 2) + Math.pow(c1.getLocate().getY() - c2.getLocate().getY(), 2));

        return _distance;
    }

    /**
     * find point nearliest with current point
     * @param c stater customer
     * @return best customer
     */

    public static Customer nearliestDistance(Customer c, ArrayList<Customer> listCus) {
        Customer result = null;

        double distBetter = Double.MAX_VALUE;
        for (int i = 0; i < listCus.size(); i++) {

            if (!listCus.get(i).isRouted()) {
                double tmp = distanceCalculator(listCus.get(i), c);
                if (tmp < distBetter) {
                    distBetter = tmp;
                    result = listCus.get(i);
                }
            }

        }

        return result;
    }

    /**
     * local Search in Vehicle
     * -đổi chỗ các vị trị trong cùng xe
     * @param v vehicle
     */
    public static void localSearchInVehicle (Vehicle v) {
        ArrayList<Customer> betterCostList = new ArrayList<>();
        betterCostList.addAll(v.route);

        double betterCost = v.getLocalCost();

        for (int i = 1; i < v.getRoute().size() - 1; i++) {
            for (int j = 1; j < v.getRoute().size() - 1; j++) {

                ArrayList<Customer> tempBetterList = v.getRoute();

                if (i == j) continue;
                Collections.swap(tempBetterList, i , j);
                double tempCost = 0;

                for (int k = 0; k < tempBetterList.size() - 1; k++) {
                    double dist = distanceCalculator(tempBetterList.get(k), tempBetterList.get(k + 1));
                    tempCost += dist;
                }

                if (tempCost < betterCost) {
                    betterCostList.clear();
                    betterCostList.addAll(tempBetterList);
                    betterCost = tempCost;
                }
            }
        }

        v.setRoute(betterCostList);
        v.setLocalCost(betterCost);
    }

    /**
     * local search out of vehicle
     * -đổi chỗ các vị trí trong 2 xe với nhau
     * @param vehicles list vehicle
     */
    public static void localSearchOutVehicle (ArrayList<Vehicle> vehicles) {

        //lấy 2 xe ngẫu nhiên khác nhau
        Random random = new Random();
        int veh1 = random.nextInt(vehicles.size()-2) + 1;
        int veh2 = random.nextInt(vehicles.size() - 2) + 1;
        while (veh2 == veh1) {
            veh2 = random.nextInt(vehicles.size() - 2) + 1;
        }

        //đổi chỗ 2 đơn hàng trong 2 xe cho nhau
        for (int i = 1; i < vehicles.get(veh1).getRoute().size() - 1; i++) {
            for (int j = 1; j < vehicles.get(veh2).getRoute().size() - 1; j++) {

                Customer temp1 = vehicles.get(veh1).getRoute().get(i); //lấy 1 phần tử ở xe 1
                Customer temp2 = vehicles.get(veh2).getRoute().get(j); //lấy 1 phần tử ở xe 2

                int tempCap1 = vehicles.get(veh1).getVehCap() - temp1.getWeight() + temp2.getWeight();
                int tempCap2 = vehicles.get(veh2).getVehCap() - temp2.getWeight() + temp1.getWeight();

                double tempCost1 = 0.0;
                double tempCost2 = 0.0;

                if (tempCap1 <= capacity && tempCap2 <= capacity) {

                    //tính độ dài quãng đường khi đổi chỗ 2 đơn hàng
                    for (int k1 = 0; k1 < vehicles.get(veh1).getRoute().size() - 1; k1++) {
                        if (temp1 == vehicles.get(veh1).getRoute().get(k1)) {
                            tempCost1 += distanceCalculator(temp2, vehicles.get(veh1).getRoute().get(k1 + 1));
                            continue;
                        }
                        tempCost1 += distanceCalculator(vehicles.get(veh1).getRoute().get(k1), vehicles.get(veh1).getRoute().get(k1 + 1));
                    }

                    for (int k2 = 0; k2 < vehicles.get(veh2).getRoute().size() - 1; k2++) {
                        if (temp2 == vehicles.get(veh2).getRoute().get(k2)) {
                            tempCost2 += distanceCalculator(temp1, vehicles.get(veh2).getRoute().get(k2 + 1));
                            continue;
                        }
                        tempCost2 += distanceCalculator(vehicles.get(veh2).getRoute().get(k2), vehicles.get(veh2).getRoute().get(k2 + 1));

                    }

                    double totalCost = vehicles.get(veh1).getLocalCost() + vehicles.get(veh2).getLocalCost();
                    if (tempCost1 + tempCost2 < totalCost) {
                        vehicles.get(veh1).getRoute().set(i, temp2);
                        vehicles.get(veh1).setLocalCost(tempCost1);
                        vehicles.get(veh1).setVehCap(tempCap1);

                        vehicles.get(veh2).getRoute().set(j, temp1);
                        vehicles.get(veh2).setLocalCost(tempCost2);
                        vehicles.get(veh2).setVehCap(tempCap2);
                    }
                }
            }
        }
    }

    /**
     * fuction delete a customer in list of customer
     * @param ans customer need delete
     * @param list list have this customer
     */
    public static void deleteElement(Customer ans, ArrayList<Customer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(ans)) {
                list.remove(i);
                break;
            }

        }
    }

    public static void main(String[] args) {

        ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();

        readData("E:\\DaiHoc\\Ki1_Nam2\\toi uu hoa\\fileVRP\\instances\\X-n242-k48.vrp");

        if (!listCustomer.isEmpty()) {

            /**
             * tìm lời giải ban đầu:
             * - tìm điểm gần nhất chưa xét đến với điểm cần xét
             * - nếu tổng trọng lượng của điểm tìm được với trọng lượng của xe hiện tại chưa vượt quá capacity
             *    +thêm điểm đó vào danh sách khách hàng của xe
             * - nếu lớn hơn thì tìm điểm khác
             */
            listCustomer.get(0).setRouted(true);

            while (true) {

                ArrayList<Customer> cusListTemp = new ArrayList<Customer>();
                Vehicle vehicle = new Vehicle();
                Customer pointStart = listCustomer.get(0);
                int countCap = 0;

                cusListTemp.clear();
                cusListTemp.addAll(listCustomer);
                vehicle.addCustomer(pointStart);
                Customer ans = nearliestDistance(pointStart, listCustomer);

                if (ans == null)
                    break;

                while (true) {
                    ans = nearliestDistance(pointStart, cusListTemp);

                    if (ans == null) break;

                    if (countCap + ans.getWeight() > capacity) {
                        deleteElement(ans, cusListTemp);
                        continue;
                    }

                    countCap += ans.getWeight();
                    vehicle.addCustomer(ans);
                    vehicle.setVehCap(countCap);
                    vehicle.setLocalCost(vehicle.getLocalCost()+ distanceCalculator(ans, pointStart));
                    deleteElement(ans, cusListTemp);
                    listCustomer.get(ans.getId()).setRouted(true);
                    pointStart = ans;
                }

                vehicle.addCustomer(listCustomer.get(0));
                vehicle.setLocalCost(vehicle.getLocalCost() + distanceCalculator(pointStart, listCustomer.get(0)));
                vehicles.add(vehicle);
            }

            /**
             * hiển thị ra màn hình
             */
            int globalCost = 0;

            int numofcustomer = 0;
            for (int i = 0; i < vehicles.size(); i++) {
                System.out.print("#Route" + i + ": " + vehicles.get(i).getVehCap()+ ": ");
                vehicles.get(i).printVehicle();
                System.out.println();
                globalCost += vehicles.get(i).getLocalCost();
                numofcustomer += vehicles.get(i).getRoute().size();
            }

            numofcustomer -= vehicles.size()*2 - 1;
            System.out.println("Num of customer: " + numofcustomer);
            System.out.println("Total Cost: " + globalCost);
            System.out.println("Capacity: " + capacity);

            System.out.println("============================================================");

            /**
             * local search
             * kết hợp cả local Search trong xe và các xe với nhau
             * để tìm ra lời giản tối ưu hơn lời giải hiện tại
             */
            for (int i = 0; i < 300000; i++) {
                for (Vehicle v : vehicles) {
                    localSearchInVehicle(v);
                }

                localSearchOutVehicle(vehicles);
            }

            /**
             * hiển thị ra màn hình
             */
            globalCost = 0;
            numofcustomer = 0;
            for (int i = 0; i < vehicles.size(); i++) {
                System.out.print("#Route" + i + ": " + vehicles.get(i).getVehCap()+ ": ");
                vehicles.get(i).printVehicle();
                System.out.println();
                globalCost += vehicles.get(i).getLocalCost();
                numofcustomer += vehicles.get(i).getRoute().size();
            }

            numofcustomer -= vehicles.size()*2 - 1;
            System.out.println("Num of customer: " + numofcustomer);
            System.out.println("Total Cost: " + globalCost);
            System.out.println("Capacity: " + capacity);

        }
    }
}

