package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class InfoService {

    private final Logger logger = LoggerFactory.getLogger(InfoService.class);
    private long timeStartMillis;

    public long getSumByStream() {
        timeStartMillis = System.currentTimeMillis();
        long sum = Stream.iterate(1, a -> a + 1)
               .limit(1_000_000)
               .reduce(0, Integer::sum);
        logger.info("Time to complete method before optimisation: " + (System.currentTimeMillis() - timeStartMillis));
        return sum;
    }

    public long getSumByStreamOptimised() {
        timeStartMillis = System.currentTimeMillis();
        long sum = IntStream.range(1, 1_000_000)
                .parallel()
                .reduce(0, Integer::sum);
        logger.info("Time to complete method after optimisation: " + (System.currentTimeMillis() - timeStartMillis));
        return sum;
    }
}
