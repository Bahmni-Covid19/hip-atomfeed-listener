package org.bahmni.module.hipfeedintegration.atomfeed.jobs;

public interface FeedJob {
    void process() throws InterruptedException;
}
