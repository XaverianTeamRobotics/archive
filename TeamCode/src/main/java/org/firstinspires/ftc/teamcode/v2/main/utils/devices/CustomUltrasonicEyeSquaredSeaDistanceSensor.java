package org.firstinspires.ftc.teamcode.v2.main.utils.devices;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;
import com.qualcomm.robotcore.util.TypeConversion;

/**
 * Represents an addressable ultrasonic distance sensor plugged into an I2C port.
 * @author bob of Qualcomm Incorpora- no its muke, actually
 * TODO: We need more documentation of this class. Someone who knows what's going on here, please do that.
 */
@I2cDeviceType()
@DeviceProperties(xmlTag = "TOFSENSORC", name = "CustomUltrasonicDistanceSensor", description = "UltrasonicDistanceSensorButCustom")
public class CustomUltrasonicEyeSquaredSeaDistanceSensor extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    public CustomUltrasonicEyeSquaredSeaDistanceSensor(I2cDeviceSynch i2cDeviceSynch) {
        super(i2cDeviceSynch, true);

        deviceClient.setI2cAddress(I2cAddr.create7bit(112));

        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    public void writeData(final Commands cmd) {
        deviceClient.write(TypeConversion.intToByteArray(cmd.bVal));
    }

    public void writeData(final Commands cmd, short value) {
        deviceClient.write(cmd.bVal, TypeConversion.shortToByteArray(value));
    }

    public short readData(final Commands cmd) {
        return TypeConversion.byteArrayToShort(deviceClient.read(cmd.bVal, cmd.returnedValues));
    }

    public short readData(final int cmd) {
        return TypeConversion.byteArrayToShort(deviceClient.read(cmd, 2));
    }

    @Override
    protected boolean doInitialize() {
        return true;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "I2C Distance Sensor (Ultrasonic)";
    }

    public enum Commands {
        INITIATE_WRITE(224, 0, ReadWrite.WRITE),
        WRITE_RANGE_COMMAND(81, 0, ReadWrite.WRITE),
        READ_LAST(225, 2, ReadWrite.READ);

        public int bVal;
        public int returnedValues;
        public ReadWrite readWrite;

        Commands(int bVal, int returnedValues, ReadWrite readWrite) {
            this.bVal = bVal;
            this.returnedValues = returnedValues;
            this.readWrite = readWrite;
        }

    }

    public enum ReadWrite {
        READ,
        WRITE
    }

}
