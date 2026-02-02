package oop.object_relationships.implement;

public class Vehicle {
    public static void main(String[] args) {
        System.out.println("No Of wheels : " + noOfWheels("bike"));
    }

    public static int noOfWheels(String vehicleName) {
        return VehicleType.getVehicleType(vehicleName).getNoOfWheels();
    }
}
