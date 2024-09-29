package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.TimeTurn;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name="Odyssea Drive No Distance", group = "Linear Opmode")
public class PowerPlayTeleop extends LinearOpMode {
    private FtcDashboard dash = FtcDashboard.getInstance();
    private List<Action> runningActions = new ArrayList<>();

    private enum TurnState {
        STRAIGHT,
        LEFT,
        RIGHT,
        BACKWARDS
    }

    TurnState turnState;

    private double LeftXInput;
    private double LeftYInput;
    private double RightXInput;

    @Override
    public void runOpMode() throws InterruptedException {
        turnState = TurnState.STRAIGHT;

        GamepadEx gamepadEx1 = new GamepadEx(gamepad1);
        GamepadEx gamepadEx2 = new GamepadEx(gamepad2);

        // initialize all the subsystems: 1. drivetrain,  2 intake+slide
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        IntakeSlideSubsystem4 intakeSlide = new IntakeSlideSubsystem4();
        intakeSlide.init(hardwareMap);

        // by default , use Drive Control #1

        double leftStickMultiplierX, leftStickMultiplierY, rightStickMultiplierX;
        GamepadHelper leftStickX = new GamepadHelper();
        leftStickX.init();
        GamepadHelper leftStickY = new GamepadHelper();
        leftStickY.init();
        GamepadHelper rightStickX = new GamepadHelper();
        rightStickX.init();

        waitForStart();

        while (opModeIsActive()) {
            TelemetryPacket packet = new TelemetryPacket();

            // drivebase control loop
            leftStickMultiplierX = leftStickX.getGamepadStickRampingMultiplier(gamepad1.left_stick_x);
            leftStickMultiplierY = leftStickY.getGamepadStickRampingMultiplier(gamepad1.left_stick_y);
            rightStickMultiplierX = rightStickX.getGamepadStickRampingMultiplier(gamepad1.right_stick_x);

            drive.updatePoseEstimate();

            // keeps controls the same if robot is rotated 90 degrees in any direction
            switch (turnState) {
                case STRAIGHT:
                    LeftXInput = gamepad1.left_stick_x * leftStickMultiplierX * intakeSlide.dropOffMultiplier;
                    LeftYInput = gamepad1.left_stick_y * leftStickMultiplierY * intakeSlide.dropOffMultiplier;
                    RightXInput = gamepad1.right_stick_x * rightStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_LEFT)) {
//                        drive.turn(Math.toRadians(90));
//                        turnState = TurnState.LEFT;
//                    } else if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_RIGHT)) {
//                        drive.turn(Math.toRadians(-90));
//                        turnState = TurnState.RIGHT;
//                    }
                    break;
//                case LEFT:
//                    LeftXInput = -gamepad1.left_stick_y * leftStickMultiplierY * intakeSlide.dropOffMultiplier;
//                    LeftYInput = gamepad1.left_stick_x * leftStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    RightXInput = gamepad1.right_stick_x * rightStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_LEFT)) {
//                        drive.turn(Math.toRadians(90));
//                        turnState = TurnState.BACKWARDS;
//                    } else if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_RIGHT)) {
//                        drive.turn(Math.toRadians(-90));
//                        turnState = TurnState.STRAIGHT;
//                    }
//                    break;
//                case RIGHT:
//                    LeftXInput = gamepad1.left_stick_y * leftStickMultiplierY * intakeSlide.dropOffMultiplier;
//                    LeftYInput = -gamepad1.left_stick_x * leftStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    RightXInput = gamepad1.right_stick_x * rightStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_LEFT)) {
//                        drive.turn(Math.toRadians(90));
//                        turnState = TurnState.STRAIGHT;
//                    } else if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_RIGHT)) {
//                        drive.turn(Math.toRadians(-90));
//                        turnState = TurnState.BACKWARDS;
//                    }
//                    break;
//                case BACKWARDS:
//                    LeftXInput = -gamepad1.left_stick_x * leftStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    LeftYInput = -gamepad1.left_stick_y * leftStickMultiplierY * intakeSlide.dropOffMultiplier;
//                    RightXInput = gamepad1.right_stick_x * rightStickMultiplierX * intakeSlide.dropOffMultiplier;
//                    if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_LEFT)) {
//                        drive.turn(Math.toRadians(90));
//                        turnState = TurnState.RIGHT;
//                    } else if (gamepadEx1.wasJustReleased(GamepadKeys.Button.DPAD_RIGHT)) {
//                        drive.turn(Math.toRadians(-90));
//                        turnState = TurnState.LEFT;
//                    }
//                    break;
            }

            drive.setDrivePowers(
                    new PoseVelocity2d(
                            new Vector2d(
                                -LeftYInput,
                                -LeftXInput),
                            -RightXInput
                    )
            );

            intakeSlide.run(gamepadEx1, gamepadEx2);
            intakeSlide.runIntake(gamepadEx1);

            // update running actions
            List<Action> newActions = new ArrayList<>();
            for (Action action : runningActions) {
                action.preview(packet.fieldOverlay());
                if (action.run(packet)) {
                    newActions.add(action);
                }
            }
            runningActions = newActions;

            dash.sendTelemetryPacket(packet);

            telemetry.addData("Current State 1", intakeSlide.getCurrentState());
            telemetry.addData(intakeSlide.getCurrentCaption(), intakeSlide.getCurrentStatus());
            telemetry.addData("Current Control", intakeSlide);
            telemetry.addData("Rotation", turnState.name());

            telemetry.addData("Servo Position", intakeSlide.getServoPosition());
            telemetry.addData("Intake State", intakeSlide.getIntakeState());
            // publish all the telemetry at once
            telemetry.update();
        }
    }

}
