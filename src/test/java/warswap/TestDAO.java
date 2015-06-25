package warswap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.osu.netmotifs.warswap.GraphDAO;
import edu.osu.netmotifs.warswap.common.Edge;
import edu.osu.netmotifs.warswap.common.Vertex;

public class TestDAO {

	public static void main(String[] args) {
		// testBulkImport();
		// testBatchInsert();
		long t1 = System.currentTimeMillis();
		List<Integer> l = new ArrayList<Integer>();
	}

	private static void testBulkImport() {
		GraphDAO graphDAO = GraphDAO.getInstance();
		try {
			graphDAO.createConnection();
			graphDAO.createGraphTable();
			graphDAO.bulkImport("data/dronet.fanmod", "sample");
			graphDAO.getAllCounts("sample");
			// graphDAO.selectAllGraphs();
			// graphDAO.shutdown();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testBatchInsert() {
		List<Vertex> list = new ArrayList<Vertex>();
		for (int i = 0; i < 5; i++) {
			Vertex vertex1 = new Vertex(i, Byte.valueOf("0"));
			list.add(vertex1);
			Vertex vertex2 = new Vertex(i + 5, Byte.valueOf("1"));
			list.add(vertex2);
		}
		List<Edge> eList = new ArrayList<Edge>();
		for (int i = 0; i < 4; i++) {
			Edge edge = new Edge(list.get(i), list.get(i + 5));
			eList.add(edge);
		}

		GraphDAO graphDAO = GraphDAO.getInstance();
		try {
			graphDAO.createConnection();
			graphDAO.createTable("sampleTable");
//			graphDAO.initializeDB();
			graphDAO.insertEdges(eList);
			graphDAO.selectAllGraphs();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
