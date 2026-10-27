package org.firstinspires.ftc.teamcode.prototyping;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.driveclasses.MecanumDrive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class DriverTest extends TeleOp {

    // Creating the new random object that will be used to generate random numbers
    public Random random = new Random();

    // Setting up the variables that will be used in the setup selection
    public int setup_num;
    public String setup;

    // Setting up the clocks
    public ElapsedTime totalTimeTimer = new ElapsedTime();
    public ElapsedTime positionTimer = new ElapsedTime();
    public ElapsedTime waitTimer = new ElapsedTime();

    // Setting up the variables we will use for the hardware
    public DcMotorEx frontRight;
    public DcMotorEx frontLeft;
    public DcMotorEx backRight;
    public DcMotorEx backLeft;

    // Setting up the pinpoint variables
    public GoBildaPinpointDriver pinpoint;

    public List<String> timestamps = new ArrayList<>();

    // Creating the list for the random elements
    public double[][] cords = { // This creates a list of all the coordinates that the players can go to
            {130, 25},
            {105, 59},
            {130, 83},
            {83.5, 118},
            {83.5, 23.5}
    };
    public String[] setups = {"A", "B", "C"}; // This creates a list of the possible setup letters

    // Making the active coordinate variable
    public double[] coordinate;
    public double[] position;
    public Pose2D tempPose;
    public int totalPositions;
    public boolean newRun = false;
    public MecanumDrive drive;
    public float total;

    @Override
    public void init(){

        // Hardware mapping the wheels
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");

        drive = new MecanumDrive(frontRight, frontLeft, backRight, backLeft);

        // Hardware mapping the odometry and setting it up
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(0.00, 0.00, DistanceUnit.INCH);


        // Getting the random setup for the driver test
        setup_num = random.nextInt(setups.length); // Gets the random number from the size of the setups list
        setup = setups[setup_num]; // Gets the random setup chosen and saves it in the setup variable

    }

    @Override
    public void init_loop(){

        telemetry.addData("Setup ", setup);
        telemetry.update();

    }

    @Override
    public void start(){
        totalTimeTimer.reset();
        positionTimer.reset();
    }

    @Override
    public void loop(){
        // Checking if a new position should be shown
        if(positionTimer.seconds() / 20 == 0 || newRun){
            newRun = false; // Resets the newRun variable
            int num = random.nextInt(6); // Gets the next random number
            coordinate = cords[num]; // Gets the next coordinate for the robot to go to
            telemetry.addData("Go to", coordinate);
        }

        drive.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0 || gamepad1.right_stick_x != 0){
            waitTimer.reset(); // Resets the timer if player moves
        }



        // Checking odometry
        pinpoint.update(); // Updates the odometry's position
        tempPose = pinpoint.getPosition();
        double x = tempPose.getX(DistanceUnit.INCH);
        double y = tempPose.getY(DistanceUnit.INCH);
        position[0] = x;
        position[1] = y;

        // dw it works
        if(inRange(position[0], coordinate[0]-5, coordinate[0]+5) && inRange(position[1], coordinate[1]-5, coordinate[1]+5) && waitTimer.seconds() >= 2){
            totalPositions++;
            timestamps.add(String.valueOf(totalTimeTimer.seconds()));
            newRun = true;
        }

    }

    @Override
    public void stop(){
        double time = totalTimeTimer.seconds();
        telemetry.addData("Average time", avg(timestamps));
        while(!gamepad1.left_bumper){
            telemetry.addData("Total Positions Reached", totalPositions);
            telemetry.addData("Final time: ", time);
            telemetry.update();
        }

    }
    boolean inRange(double target, double min, double max){
        return (target > min && target < max);
    }

    float avg(List<String> nums){
        for (String num : nums){
            float numm = Float.parseFloat(num);
            total += numm;
        }
        return total;
    }
}
