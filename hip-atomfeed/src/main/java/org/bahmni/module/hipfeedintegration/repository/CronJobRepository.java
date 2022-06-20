package org.bahmni.module.hipfeedintegration.repository;

import org.bahmni.module.hipfeedintegration.model.QuartzCronScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepository extends JpaRepository<QuartzCronScheduler, Integer> {
}
