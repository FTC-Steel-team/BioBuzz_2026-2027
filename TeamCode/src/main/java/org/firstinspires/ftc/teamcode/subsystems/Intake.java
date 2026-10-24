package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.states.IntakeStates;

public class Intake {

    DcMotorEx intake;
    IntakeStates intakeStates;
    double intakeVel;
    double outtakeVel;
    final double STOP_SPEED = 0;

    public Intake(DcMotorEx intake){

        this.intake = intake;
        intake.setVelocity(STOP_SPEED);
        intakeStates = IntakeStates.IDLE;

        this.intakeVel = 700;
        this.outtakeVel = -700;
    }

    public void In(){
        intakeStates = IntakeStates.INTAKING;
    }

    public void Out(){
        intakeStates = IntakeStates.OUTTAKING;
    }

    public void Run(boolean in, boolean out){
        switch (intakeStates) {
            case INTAKING:
                intake.setVelocity(intakeVel);
            case IDLE:
                intake.setVelocity(STOP_SPEED);
            case OUTTAKING:
                intake.setVelocity(outtakeVel);
        }
    }

}
