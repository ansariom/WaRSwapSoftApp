/**
Copyright (c) 2015 Oregon State University
All Rights Reserved.

AUTHOR
  Mitra Ansariola
  
  Department of Botany and Plant Pathology 
  2082 Cordley Hall
  Oregon State University
  Corvallis, OR 97331-2902
  
  E-mail:  megrawm@science.oregonstate.edu 
  http://bpp.oregonstate.edu/

====================================================================

Permission to use, copy, modify, and distribute this software and its
documentation for educational, research and non-profit purposes, without fee,
and without a written agreement is hereby granted, provided that the above
copyright notice, this paragraph and the following three paragraphs appear in
all copies. 

Permission to incorporate this software into commercial products may be obtained
by contacting Oregon State University Office of Technology Transfer.

This software program and documentation are copyrighted by Oregon State
University. The software program and documentation are supplied "as is", without
any accompanying services from Oregon State University. OSU does not warrant
that the operation of the program will be uninterrupted or error-free. The
end-user understands that the program was developed for research purposes and is
advised not to rely exclusively on the program for any reason. 

IN NO EVENT SHALL OREGON STATE UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT,
INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF OREGON
STATE UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. OREGON STATE
UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
AND ANY STATUTORY WARRANTY OF NON-INFRINGEMENT. THE SOFTWARE PROVIDED HEREUNDER
IS ON AN "AS IS" BASIS, AND OREGON STATE UNIVERSITY HAS NO OBLIGATIONS TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
 */

package edu.osu.netmotifs.warswap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.osu.netmotifs.warswap.common.Edge;
import edu.osu.netmotifs.warswap.common.Vertex;

public class GraphDAO {
	private String dbURL = "";
	private String dbShutdownURL = "";

	private final String dbName = "WARSWAPDB";
	private String graphTableName = "graphTable";
	private String newGraphTableName = "newGraphTable";
	private String tempTableName = "tempTable";
	private Connection connection = null;
	private Statement stmt = null;
	private static GraphDAO graphDAO = null;

	private GraphDAO() {
		dbURL = "jdbc:derby:memory:" + dbName + ";create=true";
		dbShutdownURL = "jdbc:derby:memory:" + dbName + ";shutdown=true";
		try {
			createConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static GraphDAO getInstance() {
		if (graphDAO == null) {
			graphDAO = new GraphDAO();
		}
		return graphDAO;
	}

	public void initializeDB() throws Exception {
		createConnection();
		createGraphTable();
		createNewGraphTable();
//		createTempTable();
	}

	public void createConnection() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		connection = DriverManager.getConnection(dbURL);
		System.out.println("Connection Established");
	}

	public void createNewGraphTable() throws SQLException {
		stmt = connection.createStatement();
		int count = stmt.executeUpdate("CREATE TABLE " + newGraphTableName
				+ " (V1 INT, V2 INT, COLOR1 INT," + " COLOR2 INT)");
		stmt.close();
		System.out.println("New GraphTable Created");
	}

	public void createGraphTable() throws SQLException {
		stmt = connection.createStatement();
		int count = stmt.executeUpdate("CREATE TABLE " + graphTableName
				+ " (V1 INT, V2 INT, COLOR1 INT," + " COLOR2 INT)");
		stmt.close();
	}
	public synchronized void createTable(String tableName) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE TABLE " + tableName
				+ " (V1 INT, V2 INT, COLOR1 INT," + " COLOR2 INT)");
		stmt.close();
	}
	
	public synchronized void dropTable(String tableName) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("DROP TABLE " + tableName);
		stmt.close();
	}


	public void insertEdges(List<Edge> edges) throws Exception {
		String sql = "insert into " + newGraphTableName + " values (?,?,?,?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
//		connection.setAutoCommit(false);
		for (Edge edge : edges) {
			preparedStatement.setInt(1, edge.getSourceV().getLabel());
			preparedStatement.setInt(2, edge.getTargetV().getLabel());
			preparedStatement.setInt(3, edge.getSourceV().getColor());
			preparedStatement.setInt(4, edge.getTargetV().getColor());
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
		preparedStatement.close();
//		connection.commit();
//		connection.setAutoCommit(true);

	}

	public void insertTempVs(List<Integer> items) throws SQLException {
		removeAllTemps();
		String sql = "insert into " + tempTableName + " values (?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		connection.setAutoCommit(false);
		for (Integer item : items) {
			preparedStatement.setInt(1, item);
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
		connection.commit();
		connection.setAutoCommit(true);

	}

	public void insertEdge(Vertex srcV, Vertex tgtV, byte color1,
			byte color2) throws SQLException {
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		stmt.execute("insert into " + newGraphTableName + " values ("
				+ srcV.getLabel() + "," + tgtV.getLabel() + "," + color1 + ","
				+ color2 + ")");
		stmt.close();
	}
	
	public void deleteEdge(Vertex srcV, Vertex tgtV, byte color1,
			byte color2) throws SQLException {
		stmt = connection.createStatement();
		stmt.execute("delete from " + newGraphTableName + " where ( V1 = "
				+ srcV.getLabel() + " and V2 =  " + tgtV.getLabel() + " and color1 =  " + color1 + " and color2 = "
				+ color2 + ")");
		stmt.close();
	}

	public void removeAllTemps() throws SQLException {
		stmt = connection.createStatement();
		stmt.execute("delete from " + tempTableName);
		stmt.close();
	}

	public void shutdown() {
		try {
			DriverManager.getConnection(dbShutdownURL);
			connection.close();
		} catch (SQLException e) {
//			e.printStackTrace();
		}
	}

	public synchronized void bulkImport(String inputGraph, String tableName) throws SQLException {
		PreparedStatement ps = connection
				.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
		ps.setString(1, null);
		ps.setString(2, tableName.toUpperCase());
		ps.setString(3, inputGraph);
		ps.setString(4, "\t");
		ps.setString(5, null);
		ps.setString(6, null);
		ps.setInt(7, 0);
		ps.execute();
		ps.close();
	}

	public synchronized void selectGraphsLayer(HashMap<Integer, Vertex> srcVHash,
			HashMap<Integer, Vertex> tgtVHash, byte color1, byte color2,
			HashMap<Integer, Integer> srcIndexHash,
			HashMap<Integer, Integer> tgtIndexHash, String tableName) throws Exception {
		String query = "select v1, v2 from " + tableName
				+ " where color1 = " + color1 + " and color2 = " + color2;
		Statement stmt = connection.createStatement();
		ResultSet results = stmt.executeQuery(query);

		int srcIdx = 0, tgtIdx = 0;
		while (results.next()) {
			int v1 = results.getInt(1);
			int v2 = results.getInt(2);

			Integer srcIndex = srcIndexHash.get(v1);
			if (srcIndex == null) {
				Vertex srcV = new Vertex(v1, color1);
				srcV.incrementOutDeg();
				srcVHash.put(srcIdx, srcV);
				srcIndexHash.put(v1, srcIdx++);
			} else
				srcVHash.get(srcIndex).incrementOutDeg();

			Integer tgtIndex = tgtIndexHash.get(v2);
			if (tgtIndex == null) {
				Vertex tgtV = new Vertex(v2, color2);
				tgtV.incrementInDeg();
				tgtVHash.put(tgtIdx, tgtV);
				tgtIndexHash.put(v2, tgtIdx++);
			} else
				tgtVHash.get(tgtIndex).incrementInDeg();

		}
		results.close();
		stmt.close();
	}

	public void selectAllRandGraphs(StringBuffer buffer) throws Exception {
		stmt = connection.createStatement();
		ResultSet results = stmt
				.executeQuery("select v1,v2 from " + newGraphTableName);
		
		while (results.next()) {
			int v1 = results.getInt(1);
			int v2 = results.getInt(2);
			buffer.append(v1 + "\t" + v2 + "\n");
		}
		results.close();
		stmt.close();
	}
	public void selectAllGraphs() throws Exception {
		stmt = connection.createStatement();
		ResultSet results = stmt
				.executeQuery("select * from " + graphTableName);
		ResultSetMetaData rsmd = results.getMetaData();
		int numberCols = rsmd.getColumnCount();
		for (int i = 1; i <= numberCols; i++) {
			// print Column Names
			System.out.print(rsmd.getColumnLabel(i) + "\t\t");
		}

		System.out
				.println("\n-------------------------------------------------");

		while (results.next()) {
			int v1 = results.getInt(1);
			int v2 = results.getInt(2);
			int col1 = results.getInt(3);
			int col2 = results.getInt(4);
			System.out
					.println(v1 + "\t\t" + v2 + "\t\t" + col1 + "\t\t" + col2);
		}
		results.close();
		stmt.close();
	}

	public int getAllCounts(String tableName) throws Exception {
		Statement stmt = connection.createStatement();
		ResultSet results = stmt.executeQuery("select count(*) from "
				+ tableName);
		ResultSetMetaData rsmd = results.getMetaData();
		results.next();
		int count = results.getInt(1);
		results.close();
		stmt.close();
		return count;
	}
	
	public int getAllCountsNewTable() throws Exception {
		stmt = connection.createStatement();
		ResultSet results = stmt.executeQuery("select count(*) from "
				+ newGraphTableName);
		ResultSetMetaData rsmd = results.getMetaData();
		results.next();
		int count = results.getInt(1);
		results.close();
		stmt.close();
		return count;
	}

	public List<Integer> selectNotTargettingDrawnAndTgtsOfCurSrc(
			Integer srcLabel, int drawnTgt, byte col1, byte col2)
					throws Exception {
		List<Integer> srcList = new ArrayList<Integer>();
		String query = "select distinct v1 from " + newGraphTableName
				+ " where v1 not in (select distinct v1 from "
				+ newGraphTableName + " where v2 in (select distinct v2 from "
				+ newGraphTableName
				+ " where v1 = ?)) and v1 in (select distinct v1 from "
				+ newGraphTableName
				+ " where v1 not in (select distinct v1 from "
				+ newGraphTableName
				+ " where v2 in (?))) and color1=? and color2=?";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, srcLabel);
		preparedStatement.setInt(2, drawnTgt);
		preparedStatement.setInt(3, Integer.valueOf(col1));
		preparedStatement.setInt(4, Integer.valueOf(col2));
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			srcList.add(results.getInt(1));
		}
		results.close();
		preparedStatement.close();
		return srcList;
	}
	public HashMap<Integer, List<Integer>> selectSrcsHavingTgtsOtherThanTgtsOfCurSrc(int curSrc, byte col1, byte col2)
			throws Exception {
		HashMap<Integer, List<Integer>> rHash = new HashMap<Integer, List<Integer>>();
		String query = "select * from " + graphTableName
				+ " where v1 <> ? and color1=? and color2=? and v2 not in (select distinct v2 from "
				+ graphTableName
				+ " where v1 = ?) ";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, curSrc);
		preparedStatement.setInt(2, Integer.valueOf(col1));
		preparedStatement.setInt(3, Integer.valueOf(col2));
		preparedStatement.setInt(4, curSrc);
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			int src = results.getInt(1);
			List<Integer> tList = rHash.get(src);
			if (tList == null)
				tList = new ArrayList<Integer>();
			tList.add(results.getInt(2));
			rHash.put(src, tList);
		}
		results.close();
		preparedStatement.close();
		return rHash;
	}
	
	public HashMap<Integer, Byte> selectSrcsNotTargettingDrawnIdx(int drawnTgt, byte col1, byte col2)
					throws Exception {
		HashMap<Integer, Byte> srcList = new HashMap<Integer, Byte>();
		String query = "select distinct v1 from " + newGraphTableName
				+ " where v1 not in (select distinct v1 from "
				+ newGraphTableName
				+ " where v2 in (?)) and color1=? and color2=?";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, drawnTgt);
		preparedStatement.setInt(2, Integer.valueOf(col1));
		preparedStatement.setInt(3, Integer.valueOf(col2));
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			srcList.put(results.getInt(1), Byte.valueOf("0"));
		}
		results.close();
		preparedStatement.close();
		return srcList;
	}
	
	public HashMap<Integer, Byte> selectSrcsNotHitingTargetsOfCurSrc(
			Integer srcLabel, byte col1, byte col2)
			throws Exception {
		HashMap<Integer, Byte> srcHash = new HashMap<Integer, Byte>();
		String query = "select distinct v1 from " + newGraphTableName
				+ " where v1 not in (select distinct v1 from "
				+ newGraphTableName + " where v2 in (select distinct v2 from "
				+ newGraphTableName
				+ " where v1 = ?)) and color1=? and color2=?";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, srcLabel);
		preparedStatement.setInt(2, Integer.valueOf(col1));
		preparedStatement.setInt(3, Integer.valueOf(col2));
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			srcHash.put(results.getInt(1), Byte.valueOf("0"));
		}
		results.close();
		preparedStatement.close();
		return srcHash;
	}

	public List<Integer> selectSrcsHittingTargetsOfNode(Integer srcLabel,
			byte col1, byte col2) throws Exception {
		List<Integer> srcList = new ArrayList<Integer>();
		String query = "select distinct v1 from "
				+ newGraphTableName + " where v2 in (select distinct v2 from "
				+ newGraphTableName
				+ " where v1 = ?) and color1=? and color2=?";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, srcLabel);
		preparedStatement.setInt(2, Integer.valueOf(col1));
		preparedStatement.setInt(3, Integer.valueOf(col2));
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			srcList.add(results.getInt(1));
		}
		results.close();
		preparedStatement.close();
		return srcList;
	}
	public List<Integer> selectTargetsOfSrc(Integer srcLabel,
			byte col1, byte col2) throws Exception {
		List<Integer> srcList = new ArrayList<Integer>();
		String query = "select distinct v2 from "
				+ newGraphTableName
				+ " where v1 = ? and color1=? and color2=?";
		PreparedStatement preparedStatement = connection
				.prepareStatement(query);
		preparedStatement.setInt(1, srcLabel);
		preparedStatement.setInt(2, Integer.valueOf(col1));
		preparedStatement.setInt(3, Integer.valueOf(col2));
		ResultSet results = preparedStatement.executeQuery();
		while (results.next()) {
			srcList.add(results.getInt(1));
		}
		results.close();
		preparedStatement.close();
		return srcList;
	}

}
