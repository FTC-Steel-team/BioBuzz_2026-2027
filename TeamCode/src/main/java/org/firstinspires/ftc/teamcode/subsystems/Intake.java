package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.states.IntakeStates;

public class Intake {

    DcMotorEx intake;
    IntakeStates intakeState; //I changed the name here from plural to singular for clarity
    double intakeVel;
    double outtakeVel;
    final double STOP_SPEED = 0;

    public Intake(DcMotorEx intake){

        this.intake = intake;
        intake.setVelocity(STOP_SPEED);
        intakeState = IntakeStates.IDLE;

        this.intakeVel = 700;
        this.outtakeVel = -700;
    }

    public void In(){ // Remember: functions are camelCase,
        intakeState = IntakeStates.INTAKING;
    }

    public void Out(){
        intakeState = IntakeStates.OUTTAKING;
    }

    public void Run(boolean in, boolean out){
        // you could use .setPower() instead of .setVelocity(), because this doesn't require fine-tuned adjustable control
        switch (intakeState) {
            case INTAKING:
                intake.setVelocity(intakeVel); //unlike the launcher, the intake and outtake speeds probably don't need to change.
            case IDLE:
                intake.setVelocity(STOP_SPEED);
            case OUTTAKING:
                intake.setVelocity(outtakeVel);
        }
    }

}
