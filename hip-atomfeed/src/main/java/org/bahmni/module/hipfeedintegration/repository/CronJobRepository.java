package org.bahmni.module.hipfeedintegration.repository;

import org.bahmni.module.hipfeedintegration.model.QuartzCronScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CronJobRepository extends JpaRepository<QuartzCronScheduler, Integer> {
}
