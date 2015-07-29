package warswap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TestNIO {
	public static void main(String[] args) {
//		readNIOFile();
		readNIOFileMemMapped();
	}

	private static void readNIOFileMemMapped() {
		String filePath = "/home/mitra/workspace/uni-workspace/warswap/data/small20.igraph.edges.txt";
		try {
			RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
		    FileChannel inChannel = aFile.getChannel();
		    MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
		    buffer.load();	
		    char c = '0';
		    StringBuffer lineStr = new StringBuffer();
	    	for (int i = 0; i < buffer.limit(); i++)
			{
	    		while ((c = (char) buffer.get()) != '\n') {
	    			lineStr.append(c);
	    			i++;
				}
	    		System.out.println(lineStr);
	    		lineStr.setLength(0);
			}
		    buffer.clear(); // do something with the data and clear/compact it.
		    inChannel.close();
		    aFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readNIOFile() {
		String filePath = "/home/mitra/workspace/uni-workspace/warswap/data/small20.igraph.edges.txt";
		try {
			RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
			FileChannel inChannel = aFile.getChannel();
			long fileSize = inChannel.size();
			ByteBuffer buffer = ByteBuffer.allocate(200);
			while (inChannel.read(buffer) > 0) {
				buffer.flip();
				for (int i = 0; i < buffer.limit(); i++) {
					System.out.print((char) buffer.get());
				}
				buffer.clear(); // do something with the data and clear/compact
								// it.
			}
			inChannel.close();
			aFile.close();
		} catch (IOException exc) {
			System.out.println(exc);
			System.exit(1);
		}
	}
}
