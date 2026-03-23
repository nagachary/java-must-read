package com.naga;

public enum VehicleType {

    CAR("car", 4),
    AUTO("auto", 3),
    BIKE("bike", 2),
    NONE("none", 0);

    private final String vehicleName;
    private final int noOfWheels;

    VehicleType(String name, int noOfWheels) {
        this.vehicleName = name;
        this.noOfWheels = noOfWheels;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public int getNoOfWheels() {
        return noOfWheels;
    }

    public static VehicleType getVehicleType(String name) {
        for (VehicleType type : VehicleType.values()) {
            if (type.vehicleName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return NONE;
    }
}
