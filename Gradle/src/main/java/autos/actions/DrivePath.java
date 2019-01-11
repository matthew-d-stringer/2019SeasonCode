package autos.actions;

import drive.DriveOutput;
import drive.PositionTracker;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import path.DriveFollower;
import path.ProfileHolder;
import path.SplineSegmentFiller;
import path.TrajectoryList;
import utilPackage.TrapezoidalMp;
import utilPackage.Units;

public class DrivePath extends Action{
    TrajectoryList segment;
    DriveFollower follower;
    boolean isDone = false;
    double driveThresh = 0.5*Units.Length.feet;
    TrapezoidalMp mp; 
    TrapezoidalMp.constraints constraints; 
    ProfileHolder pHolder; 
    boolean reverse = false;

    public static DrivePath createFromFileOnRoboRio(String fileName, String path, 
    TrapezoidalMp.constraints constraints){
        String filePath = "/home/lvuser/deploy/Autos/"+fileName+".json";
        SplineSegmentFiller filler = new SplineSegmentFiller(filePath, path);
        TrajectoryList segment = filler.generate();
        return new DrivePath(segment, constraints);
    }

    public DrivePath(TrajectoryList segment, TrapezoidalMp.constraints constraints){
        follower = new DriveFollower();
        this.segment = segment;
        this.constraints = constraints;
    }

    public DrivePath(TrajectoryList segment, TrapezoidalMp.constraints constraints, double driveThresh){
        follower = new DriveFollower();
        this.segment = segment;
        this.constraints = constraints;
        this.driveThresh = driveThresh;
    }

    public void setlookAhead(double lookAhead){
        follower.setLookAhead(lookAhead);
    }

    public void setReverse(boolean reverse){
        this.reverse = reverse;
    }

    public void setThresh(double thresh){
        driveThresh = thresh;
    }

    public void setMp(TrapezoidalMp mp) {
        this.mp = mp;
    }

    @Override
    public void start() {
        isDone = false;
        constraints.setpoint = segment.getTotalDistance();
        mp = new TrapezoidalMp(constraints);
        pHolder = new ProfileHolder(mp);
        pHolder.setTimeSeg(0.05);
        pHolder.generate();
        // double totalDist = segment.getTotalDistance();
        // for(double dist = 0; dist < totalDist; dist += totalDist/10){
        //     System.out.println("Dist: "+dist+"\tVel: "+pHolder.calculateVel(dist));
        // }
    }

    @Override
    public void update() {
        double vel = pHolder.calculateVel(segment.getDistOnPath());
        if(reverse)
            vel *= -1;
        follower.update(segment, vel);
        SmartDashboard.putNumber("Current Trajectory ID", segment.getCurrentID());
        isDone = segment.isDone(PositionTracker.getInstance().getPosition().getPos(), driveThresh);
        SmartDashboard.putBoolean("DrivePath done?", isDone);
    }

    @Override
    public void done() {
        DriveOutput.getInstance().set(DriveOutput.Modes.Velocity, 0, 0);
    }

    @Override
    public boolean isFinished() {
        return isDone;
        // return true;
    }
}