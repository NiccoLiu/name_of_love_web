package com.love.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.love.mapper.UserDAO;

@Component
public class UpdateUserStep {
    private static final Logger logger = LoggerFactory.getLogger(UpdateUserStep.class);
    @Resource
    private UserDAO userDAO;

    @Scheduled(cron = "0 0 0 * * ?")
    public void doTask() {
        int num = userDAO.updateStep(null, 0);
        logger.info("update user step num is {}", num);
    }
}
