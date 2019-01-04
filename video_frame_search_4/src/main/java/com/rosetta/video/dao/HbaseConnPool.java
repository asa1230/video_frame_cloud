package com.rosetta.video.dao;

import com.rosetta.video.property.HbaseProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;

@Repository
public class HbaseConnPool {

	private static final Logger logger = LoggerFactory.getLogger(HbaseConnPool.class);

	private HbaseProperties hbaseProperties;
	private Configuration hcfg;
	private ArrayList<HConnection> hconnList = new ArrayList<>();

	@Autowired
	public HbaseConnPool(HbaseProperties hbaseProperties) {
		this.hbaseProperties = hbaseProperties;
	}

	/**
	 * initialize connections
	 */
	@PostConstruct
	private void initialize() {
		try {
			hcfg = HBaseConfiguration.create();
			hcfg.set("hbase.zookeeper.quorum", hbaseProperties.getQuorum());
			hcfg.set("hbase.zookeeper.property.clientPort", hbaseProperties.getPort());
			int poolSize = hbaseProperties.getPoolsize();
			for (int i = 0; i < poolSize; i++) {
				hcfg.setInt("hbase.client.instance.id", i);
                HConnection connection = HConnectionManager.createConnection(hcfg);
                hconnList.add(connection);
			}
			logger.info("Hbase connection pool init finish.");
			logger.info("Hbase connection pool size = " + hbaseProperties.getPoolsize());
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Hbase connection pool init fails.");
			System.exit(1);
		}
	}

    /**
     * destroy connections
     */
    @PreDestroy
    private void destory() {
        try {
            for (int i = 0; i < hconnList.size(); i++){
                if(!hconnList.get(i).isClosed()){
                    hconnList.get(i).close();
                }
            }
            logger.info("Hbase connection pool destroy finish.");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Hbase connection pool destroy fails.");
        }

    }

	/**
	 *
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public HTable getHbaseTable(String tableName) throws Exception {
		return (HTable)getConn().getTable(tableName);
	}

	/**
	 *
	 * @return
	 */
	public HConnection getConn() {
		int id = (int)(System.currentTimeMillis() % hconnList.size());
		return hconnList.get(id);
	}


    /*******************************************************************************************************************
     * back up
     */

    //	protected void finalize() {
//		destory();
//		logger.info("HbaseConnPool gc");
//	}

}
