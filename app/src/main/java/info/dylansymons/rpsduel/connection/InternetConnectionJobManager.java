package info.dylansymons.rpsduel.connection;

import java.util.ArrayList;

/**
 * Manages {@link InternetConnectionJob} instances that need to be executed when the internet
 * connection state changes. Jobs added here through {@link #addJob(InternetConnectionJob)} will be
 * executed when {@link #executeAll()} is called.
 *
 * @see ConnectionChangeReceiver
 */
public class InternetConnectionJobManager {
    private static InternetConnectionJobManager singleton;
    private ArrayList<InternetConnectionJob> jobs;

    private InternetConnectionJobManager() {
        jobs = new ArrayList<>();
    }

    /**
     * @return the singleton instance of this class
     */
    public static InternetConnectionJobManager getManager() {
        if (singleton == null) {
            singleton = new InternetConnectionJobManager();
        }
        return singleton;
    }

    /**
     * Sets an {@link InternetConnectionJob} to be executed when {@link #executeAll()}} is called.
     * The job will be removed after being executed
     * @param job the {@link InternetConnectionJob} to be executed in the future
     */
    public void addJob(InternetConnectionJob job) {
        jobs.add(job);
    }

    /**
     * Executes all of the jobs that have been added to the manager since the last time this method
     * was called. Each job will be removed from the manager after being executed.
     */
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
