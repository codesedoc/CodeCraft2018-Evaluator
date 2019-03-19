package com.huawei;

import com.huawei.structure.CarStatus;
import com.huawei.structure.TrafficControlCenter;
import org.apache.log4j.Logger;



public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args)
    {
        if (args.length != 4) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }
        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start reaWARN No appenders could be found for logger (com.huawei.Main).d input files");
        TrafficControlCenter trafficControlCenter=new TrafficControlCenter(carPath,roadPath,crossPath,answerPath);
        trafficControlCenter.test();
        // TODO: calc

        CarStatus status=CarStatus.WAIT;
        if (status==CarStatus.WAIT)
            logger.info("==相等");
        if (status.equals(CarStatus.WAIT))
            logger.info("equals相等");
        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }
}