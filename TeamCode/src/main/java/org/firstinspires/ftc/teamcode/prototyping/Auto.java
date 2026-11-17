package org.firstinspires.ftc.teamcode.prototyping;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import static com.pedropathing.api.Paths.line;

@Autonomous(name = "Auto")
public class Auto extends OpMode {
    static final double LAUNCH_VELOCITY = 1125;
    static final double VELOCITY_TOLERANCE = 50;
    static final double FEEDER_POWER = 1.0;
    static final double FEED_TIME_SECONDS = 3.0; // long enough to shoot everything we start with

    // Blue side poses in inches. Red gets flipped in mirror()
    // TEMP: these are guesses, measure the real ones on the field
    static final Pose LAUNCH_PARK_START = new Pose(56, 8, Math.toRadians(90));
    static final Pose FLOWER_START = new Pose(36, 30, Math.toRadians(90));
    static final Pose LAUNCH_POSE = new Pose(60, 84, Math.toRadians(135));
    static final Pose PARK_LOW = new Pose(100, 30, Math.toRadians(90));
    static final Pose PARK_HIGH = new Pose(118, 48, Math.toRadians(90));

    enum AutoState {DRIVE_TO_LAUNCH, SPIN_UP, FEEDING, PARKING, DONE}

    Follower follower;
    DcMotorEx leftFlywheel, rightFlywheel;
    CRServo leftFeeder, rightFeeder;
    ElapsedTime feedTimer = new ElapsedTime();
    AutoState autoState = AutoState.DRIVE_TO_LAUNCH;

    boolean red = false;
    boolean startAtFlower = false;
    boolean parkHigh = false;

    Path toLaunch, toPark;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        leftFlywheel = hardwareMap.get(DcMotorEx.class, "left_flywheel");
        rightFlywheel = hardwareMap.get(DcMotorEx.class, "right_flywheel");
        leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");

        rightFlywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void init_loop() {
        // Pick the auto setup before the match starts
        if (gamepad1.x) red = false;
        if (gamepad1.b) red = true;
        if (gamepad1.a) startAtFlower = false;
        if (gamepad1.y) startAtFlower = true;
        if (gamepad1.dpad_down) parkHigh = false;
        if (gamepad1.dpad_up) parkHigh = true;

        telemetry.addData("Alliance (X blue / B red)", red ? "RED" : "BLUE");
        telemetry.addData("Start (A launch park / Y flower)", startAtFlower ? "FLOWER" : "LAUNCH PARK");
        telemetry.addData("Park (dpad down low / up high)", parkHigh ? "UPPER CORNER" : "LOWER CORNER");
        telemetry.update();
    }

    @Override
    public void start() {
        Pose start = mirror(startAtFlower ? FLOWER_START : LAUNCH_PARK_START);
        Pose launch = mirror(LAUNCH_POSE);
        Pose park = mirror(parkHigh ? PARK_HIGH : PARK_LOW);

        toLaunch = line(start, launch).linear(start, launch);
        toPark = line(launch, park).linear(launch, park);

        follower.setPose(start);
        follower.follow(toLaunch);
    }

    @Override
    public void loop() {
        follower.update();

        switch (autoState) {
            case DRIVE_TO_LAUNCH:
                if (!follower.following()) {
                    leftFlywheel.setVelocity(LAUNCH_VELOCITY);
                    rightFlywheel.setVelocity(LAUNCH_VELOCITY);
                    autoState = AutoState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                if (leftFlywheel.getVelocity() > LAUNCH_VELOCITY - VELOCITY_TOLERANCE
                        && rightFlywheel.getVelocity() > LAUNCH_VELOCITY - VELOCITY_TOLERANCE) {
                    leftFeeder.setPower(FEEDER_POWER);
                    rightFeeder.setPower(FEEDER_POWER);
                    feedTimer.reset();
                    autoState = AutoState.FEEDING;
                }
                break;
            case FEEDING:
                if (feedTimer.seconds() > FEED_TIME_SECONDS) {
                    leftFeeder.setPower(0);
                    rightFeeder.setPower(0);
                    leftFlywheel.setVelocity(0);
                    rightFlywheel.setVelocity(0);
                    follower.follow(toPark);
                    autoState = AutoState.PARKING;
                }
                break;
            case PARKING:
                if (!follower.following()) autoState = AutoState.DONE;
                break;
            case DONE:
                break;
        }

        telemetry.addData("State", autoState);
        telemetry.addData("Pose", follower.pose());
        telemetry.update();
    }

    // Flips a blue pose to the red side of the field
    Pose mirror(Pose pose) {
        if (!red) return pose;
        return new Pose(144 - pose.x(), pose.y(), Math.PI - pose.heading());
    }
}