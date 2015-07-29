package warswap;

import edu.osu.netmotifs.warswap.common.LoadLogger;

public class TestLogger {
	public static void main(String[] args) {
		LoadLogger.setLogger1("/home/mitra/workspace/uni-workspace/warswap/d1.txt", "DEBUG", null);
		LoadLogger.rLogger.debug("ddddddd");
	}
}
