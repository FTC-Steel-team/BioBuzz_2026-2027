package org.firstinspires.ftc.teamcode.prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DriverTest extends OpMode {

    // Creating the new random object that will be used to generate random numbers
    public Random random = new Random();

    // Setting up the variables that will be used in the setup selection
    public int setup_num;
    public String setup;

    // Setting up the clock I believe it starts as soon as it is created, so I will reset it later
    public ElapsedTime totaltimetimer = new ElapsedTime();

    // Setting up the variables we will use for the hardware
    public DcMotorEx frontRight;
    public DcMotorEx frontLeft;
    public DcMotorEx backRight;
    public DcMotorEx backLeft;
    public DcMotorEx launcherWheelOne;
    public DcMotorEx launcherWheelTwo;



    @Override
    public void init(){

        // Hardware mapping the wheels
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");

        // Hardware mapping the launcher
        launcherWheelOne = hardwareMap.get(DcMotorEx.class, "launcherWheelOne");
        launcherWheelTwo = hardwareMap.get(DcMotorEx.class, "launcherWheelTwo");

        // Creating the list of random coordinates and the list of random setups
        List<String> setups = new ArrayList<>(); // Not fully sure how this works, but it makes a new list (=
        setups.add("A");
        setups.add("B");
        setups.add("C");

        // Getting the random setup for the driver test
        setup_num = random.nextInt(setups.size()); // Gets the random number from the size of the setups list
        setup = setups.get(setup_num); // Gets the random setup chosen and saves it in the setup variable

    }

    @Override
    public void init_loop(){

        telemetry.addData("Setup ", setup);
        telemetry.update();

    }

    @Override
    public void start(){
        totaltimetimer.reset();
    }

    @Override
    public void loop(){


    }

    @Override
    public void stop(){
        double time = totaltimetimer.seconds();
        while(!gamepad1.left_bumper){
            telemetry.addData("Final time: ", time);
        }

    }

}
