package org.firstinspires.ftc.teamcode.prototyping;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.driveclasses.MecanumDrive;
import org.firstinspires.ftc.teamcode.production.StarterbotTeleop;

@TeleOp(name = "StarterBot Teleop", group = "StarterBot")
//@Disabled
public class StarterbotAuto extends OpMode {

    // The vast majority of this code will be ripped straight out of the StarterbotTeleop
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private MecanumDrive mecanumDrive = null;

    private ElapsedTime driveTimer = new ElapsedTime();
    private double DRIVE_TIME = 2.5; // aproximation

    private DcMotorEx launcher = null;

    private final int LAUNCHER_TARGET_VELOCITY = 1250;
    public final int LAUNCHER_MIN_VELOCITY = 1200;

    private enum LauncherStates {
        IDLE,
        SPIN_UP,
        LAUNCHING,
        DONE,
    }
    private LauncherStates launcherState = LauncherStates.IDLE;

    private ElapsedTime shotTimer = new ElapsedTime();
    private final double SHOT_TIME = 4.5;


    private DcMotor intake = null;
    private CRServo leftIntakeServo = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;

    private enum AutoStates { // So this auto just launches, moves at the specific angle and then stops in the parking zone.
        LAUNCH,
        LAUNCHING,
        MOVE,
        REST
    }

    private AutoStates autoState = AutoStates.LAUNCH;

    @Override
    public void init() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");
        leftIntakeServo = hardwareMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.get(CRServo.class, "right_intake_servo");

        leftFrontDrive.setDirection(StarterbotTeleop.driveDirections[1]);
        rightFrontDrive.setDirection(StarterbotTeleop.driveDirections[0]);
        leftBackDrive.setDirection(StarterbotTeleop.driveDirections[3]);
        rightBackDrive.setDirection(StarterbotTeleop.driveDirections[2]);

        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);
    }

    @Override
    public void loop() {
        switch (autoState) {
            case LAUNCH:
                launch(true);
                autoState = AutoStates.LAUNCHING;
            break;
            case LAUNCHING:
                if (launch(false)) {
                    driveTimer.reset();
                    autoState = AutoStates.MOVE;
                }
            break;
            case MOVE:
                mecanumDrive.drive(1,0.4633431085,0); // This is the approximate values required to drive from our starting point, straight into the park zone
                if (driveTimer.seconds() > DRIVE_TIME) {
                    autoState = AutoStates.REST;
                }
            break;
            case REST:
                rightFrontDrive.setPower(0);
                leftFrontDrive.setPower(0);
                rightBackDrive.setPower(0);
                leftBackDrive.setPower(0);


        }
    }

    @Override
    public void stop() {

        rightFrontDrive.setPower(0);
        leftFrontDrive.setPower(0);
        rightBackDrive.setPower(0);
        leftBackDrive.setPower(0);


    }

    private boolean launch(boolean shotRequested) {
        switch (launcherState){
            case IDLE:
                if (shotRequested) launcherState = LauncherStates.SPIN_UP;
            break;
            case SPIN_UP:
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                    shotTimer.reset();
                    launcherState = LauncherStates.LAUNCHING;
                }
            break;
            case LAUNCHING:
                windmillServo.setPower(1);
                if (shotTimer.seconds() > SHOT_TIME) launcherState = LauncherStates.DONE;
            break;
            case DONE:
                windmillServo.setPower(0);
                launcherState = LauncherStates.IDLE;
                return true;
        }
        return false;
    }
}
