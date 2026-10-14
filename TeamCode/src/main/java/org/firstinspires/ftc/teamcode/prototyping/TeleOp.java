package org.firstinspires.ftc.teamcode.prototyping;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOp extends OpMode {
    static final boolean USE_BLOCKER = false;
    static final double SLOW_MODE = 0.4;
    static final double INTAKE_POWER = 1.0;
    static final double FEEDER_POWER = 1.0;
    static final double LAUNCH_VELOCITY = 1125;
    static final double CLOSE_VELOCITY = 900;
    static final double VELOCITY_TOLERANCE = 50;
    static final double BLOCKER_CLOSED = 0.0;
    static final double BLOCKER_OPEN = 0.5;
    static final double FEED_TIME_SECONDS = 0.4;
    static final int MAX_QUEUED_SHOTS = 3;

    enum LaunchState {IDLE, SPIN_UP, FEEDING}

    DcMotor leftFront, rightFront, leftBack, rightBack, leftIntake, rightIntake;
    DcMotorEx leftFlywheel, rightFlywheel;
    CRServo leftFeeder, rightFeeder;
    Servo blocker;
    GoBildaPinpointDriver pinpoint;
    double targetVelocity = 0;
    LaunchState launchState = LaunchState.IDLE;
    int shotsQueued = 0;
    ElapsedTime feedTimer = new ElapsedTime();

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFront = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBack = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBack = hardwareMap.get(DcMotor.class, "right_back_drive");
        leftIntake = hardwareMap.get(DcMotor.class, "left_intake");
        rightIntake = hardwareMap.get(DcMotor.class, "right_intake");
        leftFlywheel = hardwareMap.get(DcMotorEx.class, "left_flywheel");
        rightFlywheel = hardwareMap.get(DcMotorEx.class, "right_flywheel");
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");


        if (USE_BLOCKER) {
            blocker = hardwareMap.get(Servo.class, "blocker");
        } else {
            leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
            rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");
            leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFlywheel.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.setOffsets(0, -139.7, DistanceUnit.MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();

        feed(false);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;
        double scale = gamepad1.left_bumper ? SLOW_MODE : 1.0;
        double denom = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        leftFront.setPower((y + x + rx) / denom * scale);
        leftBack.setPower((y - x + rx) / denom * scale);
        rightFront.setPower((y - x - rx) / denom * scale);
        rightBack.setPower((y + x - rx) / denom * scale);

        double intake = (gamepad2.right_trigger - gamepad2.left_trigger) * INTAKE_POWER;

        leftIntake.setPower(intake);
        rightIntake.setPower(intake);

        if (gamepad2.y) targetVelocity = LAUNCH_VELOCITY;
        if (gamepad2.x) targetVelocity = CLOSE_VELOCITY;
        if (gamepad2.b) {
            targetVelocity = 0;
            shotsQueued = 0;
        }
        if (gamepad2.rightBumperWasPressed() && shotsQueued < MAX_QUEUED_SHOTS) shotsQueued++;
        launch();
        leftFlywheel.setVelocity(targetVelocity);
        rightFlywheel.setVelocity(targetVelocity);
        pinpoint.update();
        Pose2D pos = pinpoint.getPosition();

        telemetry.addData("X (in)", "%.1f", pos.getX(DistanceUnit.INCH));
        telemetry.addData("Y (in)", "%.1f", pos.getY(DistanceUnit.INCH));
        telemetry.addData("Heading (deg)", "%.1f", pos.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Launch state", launchState);
        telemetry.addData("Shots queued", shotsQueued);
        telemetry.addData("Target velocity", targetVelocity);
        telemetry.addData("Left flywheel", leftFlywheel.getVelocity());
        telemetry.addData("Right flywheel", rightFlywheel.getVelocity());
        telemetry.update();

    }

    @Override
    public void stop() {
        leftFront.setPower(0);
        rightFront.setPower(0);
        leftBack.setPower(0);
        rightBack.setPower(0);
        leftIntake.setPower(0);
        rightIntake.setPower(0);
        leftFlywheel.setVelocity(0);
        rightFlywheel.setVelocity(0);
        feed(false);
    }

    void launch() {
        switch (launchState) {
            case IDLE:
                if (shotsQueued > 0) {
                    if (targetVelocity == 0) targetVelocity = LAUNCH_VELOCITY;
                    launchState = LaunchState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                double minVelocity = targetVelocity - VELOCITY_TOLERANCE;
                if (shotsQueued == 0) {
                    launchState = LaunchState.IDLE;
                } else if (leftFlywheel.getVelocity() >= minVelocity && rightFlywheel.getVelocity() >= minVelocity) {
                    feed(true);
                    feedTimer.reset();
                    launchState = LaunchState.FEEDING;
                }
                break;
            case FEEDING:
                if (feedTimer.seconds() > FEED_TIME_SECONDS) {
                    feed(false);
                    shotsQueued--;
                    launchState = LaunchState.IDLE;
                }
                break;
        }
    }

    void feed(boolean on) {
        if (USE_BLOCKER) {
            blocker.setPosition(on ? BLOCKER_OPEN : BLOCKER_CLOSED);
        } else {
            leftFeeder.setPower(on ? FEEDER_POWER : 0);
            rightFeeder.setPower(on ? FEEDER_POWER : 0);
        }
    }
}











