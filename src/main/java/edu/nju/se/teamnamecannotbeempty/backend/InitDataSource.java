package edu.nju.se.teamnamecannotbeempty.backend;

import edu.nju.se.teamnamecannotbeempty.backend.dao.PaperDao;
import edu.nju.se.teamnamecannotbeempty.backend.data.FromCSVOpenCSVImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class InitDataSource implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private FromCSVOpenCSVImpl fromCSV;
    @Autowired
    private PaperDao paperDao;
    @Autowired
    @Qualifier("useCSVDataSource")
    private Boolean runMe;

    private static Logger logger = LoggerFactory.getLogger(InitDataSource.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null && runMe) {
            String name = "/datasource/ase13_15_16_17_19.csv";
            InputStream ase_csv = getClass().getResourceAsStream(name);
            paperDao.saveAll(fromCSV.convert(ase_csv));
            logger.info("Done Saving data from " + name);

            name = "/datasource/icse15_16_17_18_19.csv";
            InputStream icse_csv = getClass().getResourceAsStream(name);
            paperDao.saveAll(fromCSV.convert(icse_csv));
            logger.info("Done Saving data from " + name);
        }
    }
}