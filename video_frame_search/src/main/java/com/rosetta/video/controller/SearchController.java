package com.rosetta.video.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rosetta.video.service.ImportHbaseService;
import com.rosetta.video.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;
    @Autowired
    private ImportHbaseService importHbaseService;

    /**
     * 缓存中搜索
     * @param url
     * @return
     */
    @PostMapping("/cache")
    public String search(String url) {
        log.info("request path : " + url);
        String s = searchService.searchByCache(url);
        return s;
    }

    /**
     * 库中搜索
     * @param url
     * @return
     */
    @PostMapping("/hbase")
    public String query(String url) {
        log.info("request hbase path : " + url);
        String res = searchService.searchByHbase(url);
        return res;
    }

    /**
     * 搜索，不含则新建入库
     * @param url
     * @return
     */
    @PostMapping("/query")
    public String queryAndSave(String url) {
        log.info("search -- query -- check -- insert -- param : " + url);
        String flag = "0";
        try {
            String res = searchService.searchByHbase(url);
            if (StringUtils.isNotBlank(res)) {
                JSONArray objects = JSONArray.parseArray(res);
                if (objects.size() == 0) {
                    flag = "1";
                    try {
                        importHbaseService.insertToHbase(url);
                        log.info("url -- : " + url + " has been import to hbase ...");
                    } catch (Exception e) {
                        flag = "0";
                        log.error("url -- import to hbase error : " + url + " .................",e);
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0 ; i < objects.size() ; i++) {
                        JSONObject object = (JSONObject) objects.get(i);
                        String path = object.getString("src");
                        if (i == (objects.size() -1) ) {
                            sb.append(path);
                        } else {
                            sb.append(path).append(",");
                        }
                    }
                    flag = sb.toString();
                }
            }
        } catch (Exception e) {
            log.error("queryAndSave error : " , e);
        }
        return flag;
    }

    @GetMapping("/test")
    public String test(){
        return "001";
    }

}
