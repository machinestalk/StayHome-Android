package com.machinestalk.stayhome.service.body;

public class UserStatusBody {

    private int cough;
    private int headache;
    private int fever;
    private int shortBreath;
    private int noSuffer;

    public void setCough(int cough) {
        this.cough = cough;
    }

    public void setFever(int fever) {
        this.fever = fever;
    }

    public void setHeadache(int headache) {
        this.headache = headache;
    }

    public void setNoSuffer(int noSuffer) {
        this.noSuffer = noSuffer;
    }

    public void setShortBreath(int shortBreath) {
        this.shortBreath = shortBreath;
    }
}
