package com.eorionsolution.demo.bpms.creditcardapplicationdemo.processes;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.processEngine;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;

@EnableRuleMigrationSupport
public class BaseProcessEngineTestCase {
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule("camunda.cfg.xml");


    public void waitForJobExecutorToProcessAllJobs(long maxMillisToWait) {
        JobExecutor jobExecutor = ((ProcessEngineConfigurationImpl)(processEngine().getProcessEngineConfiguration())).getJobExecutor();
        jobExecutor.start();
        long intervalMillis = 1000;

        int jobExecutorWaitTime = jobExecutor.getWaitTimeInMillis() * 2;
        if(maxMillisToWait < jobExecutorWaitTime) {
            maxMillisToWait = jobExecutorWaitTime;
        }

        try {
            Timer timer = new Timer();
            InterruptTask task = new InterruptTask(Thread.currentThread());
            timer.schedule(task, maxMillisToWait);
            boolean areJobsAvailable = true;
            try {
                while (areJobsAvailable && !task.isTimeLimitExceeded()) {
                    Thread.sleep(intervalMillis);
                    try {
                        areJobsAvailable = areJobsAvailable();
                    } catch(Throwable t) {
                        // Ignore, possible that exception occurs due to locking/updating of table on MSSQL when
                        // isolation level doesn't allow READ of the table
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                timer.cancel();
            }
            if (areJobsAvailable) {
                throw new ProcessEngineException("time limit of " + maxMillisToWait + " was exceeded");
            }

        } finally {
            jobExecutor.shutdown();
        }
    }

    public boolean areJobsAvailable() {
        List<Job> list = managementService().createJobQuery().list();
        for (Job job : list) {
            if (!job.isSuspended() && job.getRetries() > 0 && (job.getDuedate() == null || ClockUtil.getCurrentTime().after(job.getDuedate()))) {
                return true;
            }
        }
        return false;
    }

    private static class InterruptTask extends TimerTask {
        protected boolean timeLimitExceeded = false;
        protected Thread thread;
        public InterruptTask(Thread thread) {
            this.thread = thread;
        }
        public boolean isTimeLimitExceeded() {
            return timeLimitExceeded;
        }
        @Override
        public void run() {
            timeLimitExceeded = true;
            thread.interrupt();
        }
    }
}
