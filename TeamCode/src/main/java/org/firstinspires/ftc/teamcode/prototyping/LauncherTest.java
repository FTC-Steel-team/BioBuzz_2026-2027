package org.firstinspires.ftc.teamcode.prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class LauncherTest extends OpMode {

    // Creating the enum that will be used to see what code should run based on what launchers the user wants to test
    private enum LauncherType{
        SINGLE,
        DUAL
    }

    // Assigning important things that we will use
    final double STOP_SPEED = 0.0; // Making a stop speed for the launchers also makes the code easier to read
    double velocity = 1000; // Making the initial velocity that the user will later change
    LauncherType launcher_type = LauncherType.SINGLE; // Creating the state machine for our Launcher Type

    DcMotorEx towards_goal_launcher = hardwareMap.get(DcMotorEx.class, "towards_goal_launcher"); // Creating the towards goal launcher
    DcMotorEx away_from_goal_launcher = hardwareMap.get(DcMotorEx.class, "away_from_goal_launcher"); // Creating the away from goal launcher
    DcMotorEx running_motor = towards_goal_launcher; // Setting the running motor for when the state is set to SINGLE


    @Override
    public void init(){

        // Ensures that when the robot starts the launchers are not already running
        towards_goal_launcher.setVelocity(STOP_SPEED);
        away_from_goal_launcher.setVelocity(STOP_SPEED);

    }

    @Override
    public void init_loop(){

        // Letting the user know that we are waiting for the launcher type
        telemetry.addData("Choose launcher type", "x for single, b for dual");

        // Assigning the launcher type based on user input

        if(gamepad1.x){ // <-- This checks if x has been pressed
            launcher_type = LauncherType.SINGLE; // Assigns the type as SINGLE meaning only one launcher will be running
        }
        if(gamepad1.b){ // <-- This checks if b has been pressed.
            launcher_type = LauncherType.DUAL; // Assigns the type as DUAL meaning both launchers will be running
        }

        // Letting the user know either what type of launcher they have chosen or letting them know that they have not chosen one yet
        telemetry.addData("Chosen launcher type", launcher_type);
        telemetry.update();
    }

    @Override
    public void loop(){

        switch(launcher_type) {

            // Code to run if only one launcher is running
            case SINGLE:

                // Changes which launcher is running based of user input
                if (gamepad1.left_bumper) {
                    running_motor = towards_goal_launcher;
                } else if (gamepad1.right_bumper) {
                    running_motor = away_from_goal_launcher;
                }

                // Changes from SINGLE to DUAL if the user wants to try both launchers
                if(gamepad1.y){
                    launcher_type = LauncherType.DUAL;
                }

                // Updates the velocity that the launchers are running at based on user input
                if(gamepad1.x){
                    velocity++;
                }

                if(gamepad1.b){
                    velocity--;
                }

                // Updates the launcher's velocity with the new velocity (NOTE: the velocity may stay the same it doesn't have to change)
                running_motor.setVelocity(velocity);

                // Making sure that the motor the user wants to test is the only one that is running
                if(running_motor == away_from_goal_launcher){
                    towards_goal_launcher.setVelocity(STOP_SPEED);
                } else {
                    away_from_goal_launcher.setVelocity(STOP_SPEED);
                }



                // Giving the user all the information about the robot and the launchers right now
                telemetry.addData("Mode", "SINGLE (you can switch the launcher with LB and RB)");
                telemetry.addData("Current Launcher", running_motor.getDeviceName());
                telemetry.addData("Current Velocity", velocity);
                telemetry.update();

            // Code to run if both launchers are running
            case DUAL:

                // Changes from DUAL to SINGLE if the user wants to try only one launcher
                if(gamepad1.y){
                    launcher_type = LauncherType.SINGLE;
                }

                // Updates the velocity that the launchers are running at based on user input
                if(gamepad1.x){
                    velocity++;
                }

                if(gamepad1.b){
                    velocity--;
                }

                // Updates both launchers velocities with the new velocity (NOTE: the velocity may stay the same it doesn't have to change)
                towards_goal_launcher.setVelocity(velocity);
                away_from_goal_launcher.setVelocity(velocity);

                // Giving the user all the information about the robot and the launchers right now
                telemetry.addData("Mode", "DUAL (Both launchers should be running)");
                telemetry.addData("Current Velocity", velocity);
                telemetry.update();

        }
    }

    @Override
    public void stop(){

        // Ensuring that when the robot stops the launchers are not still running
        towards_goal_launcher.setVelocity(STOP_SPEED);
        away_from_goal_launcher.setVelocity(STOP_SPEED);

    }
}
