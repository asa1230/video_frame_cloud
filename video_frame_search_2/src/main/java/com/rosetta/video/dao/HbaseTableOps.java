package com.rosetta.video.dao;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HbaseTableOps {

	private static final Logger logger = LoggerFactory.getLogger(HbaseTableOps.class);

	@Autowired
	private HbaseConnPool hbaseConnPool;

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @param columnName
	 * @param value
	 */
	public void put(String tableName,
					String rowKey,
					String columnFamily,
					String columnName,
					String value) {
		HTableInterface tableInterface = null;
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Put put = new Put(rowKey.getBytes());
			put.add(columnFamily.getBytes(), columnName.getBytes(), value.getBytes());
			tableInterface.put(put);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void put(String tableName,
					String rowKey,
					String columnFamily,
					Map<String, String> columnMap) {
		HTableInterface tableInterface = null;
		try {
			if (columnMap == null || columnMap.size() == 0) return;
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Put put = new Put(rowKey.getBytes());
			for(Map.Entry<String, String> entry : columnMap.entrySet()) {
				if (entry.getValue() == null) continue;
				put.add(columnFamily.getBytes(),
						entry.getKey().getBytes(),
						entry.getValue().getBytes());
			}
			tableInterface.put(put);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取hbase表中指定rowkey的某列簇下的某些列
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily 一般为d列簇
	 * @param columnNameList 列为空怎取到所有列，查询大宽表时不要为空
	 * @return
	 */
	public Map<String, String> get(String tableName,
								   String rowKey,
								   String columnFamily,
								   List<String> columnNameList) {
		HTableInterface tableInterface = null;
		Map<String, String> map = new HashMap<>();
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Get get = new Get(rowKey.getBytes());
			byte[] familybytes=columnFamily.getBytes();
			for (String column : columnNameList) {
				if (column == null) continue;
				get.addColumn(familybytes, column.getBytes());
			}
			Result result = tableInterface.get(get);
			if (result.getRow() == null) return map;
//			String rowkey = new String(result.getRow());
//			map.put("rowkey", rowkey);
			for (Cell kv : result.listCells()) {
				map.put(new String(CellUtil.cloneQualifier(kv)),
						new String(CellUtil.cloneValue(kv)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
		
	}

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @return
	 */
	public Map<String, String> get(String tableName,
								   String rowKey,
								   String columnFamily) {
		HTableInterface tableInterface = null;
		Map<String, String> map = new HashMap<>();
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Get get = new Get(rowKey.getBytes());
			get.addFamily(columnFamily.getBytes());
			Result result = tableInterface.get(get);
			if (result.getRow() == null) return map;
			for (Cell kv : result.listCells()) {
				map.put(new String(CellUtil.cloneQualifier(kv)),
						new String(CellUtil.cloneValue(kv)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;

	}

	public List<Map<String, String>> get(String tableName,
								   List<String> keyList,
								   String columnFamily) {
		HTableInterface tableInterface = null;
		List<Map<String, String>> resList = new ArrayList<>();
		if (keyList == null || keyList.size() == 0) return resList;
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			List<Get> getList = new ArrayList<>();
			for (String rowKey : keyList) {
				Get get = new Get(rowKey.getBytes());
				get.addFamily(columnFamily.getBytes());
				getList.add(get);
			}
			Result[] resArray = tableInterface.get(getList);
			// 对返回的结果集进行操作
			for (Result result : resArray){
				if (result.listCells() == null) continue;
				Map<String, String> resMap = new HashMap<>();
				// add rowkey
				resMap.put("ROWKEY", new String(result.getRow()));
				// add column
				for (Cell kv : result.listCells()) {
					resMap.put(new String(CellUtil.cloneQualifier(kv)),
							new String(CellUtil.cloneValue(kv)));
				}
				resList.add(resMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) tableInterface.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resList;

	}

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @param columnName
	 * @return
	 */
	public String get(String tableName,
					  String rowKey,
					  String columnFamily,
					  String columnName) {
		HTableInterface tableInterface = null;
		try {
			if (columnName == null || columnName.length() == 0) {
				return null;
			}
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Get get = new Get(rowKey.getBytes());
			get.addColumn(columnFamily.getBytes(),
					columnName.getBytes());
			Result result = tableInterface.get(get);

			if (result == null
					|| result.listCells() == null
					|| result.listCells().size() == 0) return null;
			String columnValue = null;
			for (Cell kv : result.listCells()) {
				if (columnName.equals(new String(CellUtil.cloneQualifier(kv)))) {
					columnValue = new String(CellUtil.cloneValue(kv));
					break;
				}
			}
			return columnValue;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 * @param columnName
	 */
	public void delete(String tableName,
					   String rowKey,
					   String columnFamily,
					   String columnName) {
		HTableInterface tableInterface = null;
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Delete delete = new Delete(rowKey.getBytes());
			delete.deleteColumn(columnFamily.getBytes(), columnName.getBytes());
			tableInterface.delete(delete);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param tableName
	 * @param rowKey
	 * @param columnFamily
	 */
	public void delete(String tableName,
					   String rowKey,
					   String columnFamily) {
		HTableInterface tableInterface = null;
		try {
			tableInterface = hbaseConnPool.getHbaseTable(tableName);
			Delete delete = new Delete(rowKey.getBytes());
			delete.deleteFamily(columnFamily.getBytes());
			tableInterface.delete(delete);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (tableInterface != null) {
					tableInterface.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
