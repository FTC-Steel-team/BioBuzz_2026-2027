package org.firstinspires.ftc.teamcode.prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class IntakeTest extends OpMode {

    // Creating the enum that will be used to see what direction the intake should be moving
    private enum IntakeMode{
        In,
        Out,
        Off
    }

    // Assigning important things that we will use
    final double Stop_speed = 0.0; // Making a stop speed for the launchers also makes the code easier to read
    double intake_speed = 1000; // Making the initial velocity that the user will later change
    double outtake_speed = -1000; // Making the initial velocity that the user will later change
    IntakeMode Intake_mode = IntakeMode.Off; // Creating the state machine for our intake mode

    DcMotorEx Intake_a;
    DcMotorEx Intake_b;

    @Override
    public void init(){

        DcMotorEx Intake_a = hardwareMap.get(DcMotorEx.class, "Intake_a");
        DcMotorEx Intake_b = hardwareMap.get(DcMotorEx.class, "Intake_b");

        Intake_a.setVelocity(Stop_speed);
        Intake_b.setVelocity(Stop_speed);

    }

    @Override
    public void init_loop(){

        // Letting the user know that we are waiting for the Intake Mode
        telemetry.addData("Choose Intake Mode", "x for In, b for Out");

        // Checking User Input
        if(gamepad1.x){ // <-- This checks if x has been pressed
            Intake_mode = IntakeMode.In; // Assigns the Mode as In
        }
        if(gamepad1.b){ // <-- This checks if b has been pressed.
            Intake_mode = IntakeMode.Out; // Assigns the Mode as Out
        }

        // Letting the user know either what type of launcher they have chosen or letting them know that they have not chosen one yet
        telemetry.addData("Chosen launcher type", Intake_mode);
        telemetry.update();
    }

    @Override
    public void loop(){

        switch(Intake_mode) {

            // Code if Intaking
            case In:

                // Changes to Outtake
                if (gamepad1.y) {
                    Intake_mode = IntakeMode.Out;
                }

                // Updates the velocity that the Intake is moving
                if (gamepad1.x) {
                    intake_speed ++;
                }

                if (gamepad1.b) {
                    intake_speed --;
                }

                if (gamepad1.a) {
                    Intake_mode = IntakeMode.Off;
                }

                //sets intakes to the current speed
                Intake_a.setVelocity(intake_speed);
                Intake_b.setVelocity(intake_speed);

                // Giving the user all the information about the robot and the launchers right now
                telemetry.addData("Mode", "IN (x to increase velocity, b to decrease velocity)");
                telemetry.addData("Current Mode", Intake_mode);
                telemetry.addData("Current Velocity For Launcher", Intake_a.getVelocity());
                telemetry.addData("Target Velocity For Launcher", intake_speed);
                telemetry.update();

                //Code if Outtaking
            case Out:

                // Changes to Intake
                if (gamepad1.y) {
                    Intake_mode = IntakeMode.In;
                }

                // Updates the velocity that the Intake is moving
                if (gamepad1.x) {
                    outtake_speed ++;
                }

                if (gamepad1.b) {
                    outtake_speed --;
                }

                if (gamepad1.a) {
                    Intake_mode = IntakeMode.Off;
                }

                //sets intakes to the current speed
                Intake_a.setVelocity(outtake_speed);
                Intake_b.setVelocity(outtake_speed);

                // Giving the user all the information about the robot and the launchers right now
                telemetry.addData("Mode", "OUT (x to increase velocity, b to decrease velocity)");
                telemetry.addData("Current Mode", Intake_mode);
                telemetry.addData("Current Velocity For Intake", Intake_a.getVelocity());
                telemetry.addData("Target Velocity For Intake", outtake_speed);
                telemetry.update();

            //code when off
            case Off:

                Intake_a.setVelocity(Stop_speed);
                Intake_b.setVelocity(Stop_speed);

                if (gamepad1.a) {
                    Intake_mode = IntakeMode.In;
                }

                if (gamepad1.b) {
                    Intake_mode = IntakeMode.Out;
                }

                // Giving the user all the information about the robot and the launchers right now
                telemetry.addData("Mode", "Off (A for IN, B for OUT)");
                telemetry.addData("Current Mode", Intake_mode);
                telemetry.addData("Current Velocity For Intake", Intake_a.getVelocity());
                telemetry.addData("Target Velocity For Intake", Stop_speed);
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