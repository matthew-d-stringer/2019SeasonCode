package path;

import utilPackage.TrapezoidalMp;
import utilPackage.Units;
import utilPackage.Util;

public class ProfileHolder{
    double distanceSeg = 12*Units.Length.inches;
    int totalSegs = 0;
    double timeSeg = 1*Units.Time.seconds;
    double totalDist; 
    TrapezoidalMp mp;
    double minimumVel = 2*Units.Length.feet;

    boolean reverse = false;

    double[] holder;

    public ProfileHolder(TrapezoidalMp motionProfile){
        totalDist = motionProfile.getConstraints().setpoint;
        totalSegs = calcIndex(totalDist);
        mp = motionProfile;
        holder = new double[totalSegs];
    }
    public ProfileHolder(TrapezoidalMp motionProfile, double distanceSeg){
        totalDist = motionProfile.getConstraints().setpoint;
        this.distanceSeg = distanceSeg;
        totalSegs = calcIndex(totalDist);
        mp = motionProfile;
        holder = new double[totalSegs];
    }

    public void setReverse(boolean reverse){
        this.reverse = reverse;
    }

    public void setTimeSeg(double timeSeg){
        this.timeSeg = timeSeg;
    }

    public void generate(){
        for(double time = 0; time < mp.getEndTime(); time += timeSeg){
            double[] vals = mp.Calculate(time);
            int index = calcIndex(vals[0]);
            if(index >= totalSegs)
                break;
            if(Math.abs(vals[1]) < minimumVel)
                holder[index] = minimumVel * Util.checkSign(vals[1]);
            else
                holder[index] = vals[1];
            if(reverse)
                holder[index] *= -1;
        }
    }

    public double calculateVel(double distance){
        int index = calcIndex(distance);
        if(index >= totalSegs){
            return 0;
        }
        return holder[index];
    }

    public void setMinimumVel(double vel){
        minimumVel = vel;
    }

    public Runnable getGenerateRunnable(){
        return ()->{generate();};
    }

    public Thread getGeneratThread(){
        return new Thread(getGenerateRunnable());
    }

    private int calcIndex(double distance){
        return (int)Math.floor(distance/distanceSeg);
    }
}