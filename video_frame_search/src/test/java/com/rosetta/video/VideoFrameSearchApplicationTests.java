package com.rosetta.video;

import com.rosetta.video.dao.HbaseConnPool;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoFrameSearchApplicationTests {

    @Autowired
    private HbaseConnPool hbaseConnPool;

    @Test
    public void contextLoads() {
        String tableName = "video_frame_video";
        long rowCount = 0;
        try {
            HTable table = hbaseConnPool.getHbaseTable(tableName);
            Scan scan = new Scan();
            scan.setFilter(new FirstKeyOnlyFilter());
            ResultScanner scanner = table.getScanner(scan);
            for (Result result :
                    scanner) {
                rowCount += result.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("row count : " + rowCount);
    }

}

