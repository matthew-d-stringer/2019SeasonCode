/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";

	private final SendableChooser<String> m_chooser = new SendableChooser<>();
	
	TalonSRX gripper;

  public Robot() {
  }

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto modes", m_chooser);
    gripper = new TalonSRX(40);
  }

  @Override
  public void autonomous() {
    String autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + autoSelected);
  }

  @Override
  public void operatorControl() {
    gripper.set(ControlMode.PercentOutput, 0.5);
  }

  @Override
  protected void disabled() {
    gripper.set(ControlMode.Disabled, 0);
  }

  /**
   * Runs during test mode.
   */
  @Override
  public void test() {
  }
}
