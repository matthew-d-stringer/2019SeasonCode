package path;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import utilPackage.TrapezoidalMp;

public class ProfileHolderTest{
    @Test
    public void test(){
        TrapezoidalMp mp = new TrapezoidalMp(new TrapezoidalMp.constraints(100, 1, 1));
        assertTrue(mp.getEndTime() > 2);
        ProfileHolder holder = new ProfileHolder(mp, 0.01);
        holder.setTimeSeg(0.01);
        holder.generate();
        // for(double dist = 0; dist <= mp.getConstraints().setpoint; dist += 1){
        //     System.out.println("Dist: "+dist+"\tVel: "+holder.calculateVel(dist));
        // }
    }
}