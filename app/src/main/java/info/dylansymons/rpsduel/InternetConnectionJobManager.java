package info.dylansymons.rpsduel;

import java.util.ArrayList;

public class InternetConnectionJobManager {
    private static InternetConnectionJobManager singleton;
    private ArrayList<InternetConnectionJob> jobs;

    private InternetConnectionJobManager() {
        jobs = new ArrayList<>();
    }

    static InternetConnectionJobManager getManager() {
        if (singleton == null) {
            singleton = new InternetConnectionJobManager();
        }
        return singleton;
    }

    public void addJob(InternetConnectionJob job) {
        jobs.add(job);
    }

    public void executeAll() {
        if (jobs != null && !jobs.isEmpty()) {
            InternetConnectionJob currentJobs[] = jobs.toArray(new InternetConnectionJob[jobs.size()]);
            jobs.clear();
            for (InternetConnectionJob job : currentJobs) {
                job.execute();
            }
        }
    }
}
