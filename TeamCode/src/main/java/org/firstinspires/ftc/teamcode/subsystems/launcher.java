package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class launcher{

    //making servos/motors the servo/motor class
    DcMotorEx flywheel_a;
    DcMotorEx flywheel_b;

    Servo feeder;
    Servo hood;

    double target_speed; // Target speed for flywheels
    double flywheel_tol; // tolerance for flywheel speed in %
    double feeder_position; // current position of feeder

    int queue; // Number of balls waiting to be shot

    public launcher(){

        // sets motors and servos to ports
        flywheel_a = hardwareMap.get(DcMotorEx.class, "flywheel_a");
        flywheel_b = hardwareMap.get(DcMotorEx.class, "flywheel_b");
        feeder = hardwareMap.get(Servo.class, "feeder");
        hood = hardwareMap.get(Servo.class, "hood");

        //sets flywheels to off
        flywheel_a.setVelocity(0);
        flywheel_b.setVelocity(0);

        //sets variables on lines 18-20
        target_speed = 700;
        feeder_position = 0;
        flywheel_tol = 5;
    }

    public void On(){ //Turns Flywheels On
        flywheel_a.setVelocity(target_speed);
        flywheel_b.setVelocity(target_speed);
    }

    public void Off(){ //Turns Flywheels Off
        flywheel_a.setVelocity(0);
        flywheel_b.setVelocity(0);
    }

    public int speed_per(){ //returns current speed vs target speed as a %

        double current = flywheel_b.getVelocity() + flywheel_a.getVelocity();
        return (int) ((current/(target_speed*2))*100);
    }
    public void shot(){ //launches scoring element

        if (queue > 1){
            feeder.setPosition(feeder_position+90);
            feeder_position += 90;
            queue --;
        }

    }

    public void queue_shot(){ //adds to queue

        queue ++;
    }
}