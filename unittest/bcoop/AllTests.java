/**
 * <p>Title: AllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.xml.DOMConfigurator;

import bcoop.backup.BackupAllTests;
import bcoop.backup.fileselection.FileSelectionTests;
import bcoop.backup.scheduler.SchedulerTests;
import bcoop.block.BlockAllTests;
import bcoop.blocktracker.BlockTrackerAllTests;
import bcoop.bootfile.BootFileAllTests;
import bcoop.client.shell.command.ShellCommandsTests;
import bcoop.crypto.CryptoTest;
import bcoop.identity.IdentityAllTests;
import bcoop.network.NetworkAllTests;
import bcoop.network.p2p.P2PAllTests;
import bcoop.network.p2p.advertisement.AdvertisementAllTests;
import bcoop.network.p2p.protocol.ProtocolAllTests;
import bcoop.repos.ReposAllTests;
import bcoop.restoration.BackupDiscoveryAllTests;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import bcoop.util.UtilAllTests;
import bcoop.worker.WorkerAllTests;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class AllTests {

	public static Test suite() {
		setupLogging();

		TestSuite suite = new TestSuite("Test for bcoop");
		//$JUnit-BEGIN$
		suite.addTest(BackupAllTests.suite());
		suite.addTest(ProtocolAllTests.suite());
		suite.addTest(P2PAllTests.suite());
		suite.addTest(NetworkAllTests.suite());
		suite.addTest(BlockTrackerAllTests.suite());
		suite.addTest(BlockAllTests.suite());
		suite.addTest(ReposAllTests.suite());
		suite.addTest(AdvertisementAllTests.suite());
        suite.addTest(SchedulerTests.suite());
        suite.addTest(FileSelectionTests.suite());
        suite.addTest(UtilAllTests.suite());
        suite.addTest(ShellCommandsTests.suite());
        suite.addTest(BootFileAllTests.suite());
        suite.addTest(BackupDiscoveryAllTests.suite());
		suite.addTest(WorkerAllTests.suite());
		suite.addTest(IdentityAllTests.suite());
		suite.addTestSuite(CryptoTest.class);
        //$JUnit-END$
		return suite;
	}

	public static void setupLogging() {
		try {
			Configuration config = new Configuration("bcoop.xml");
			config.loadLocalSettings();
			DOMConfigurator.configure(config.getLog4JElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static BitCoopFile createTestFile(int fileSize, byte[] dataToWrite) throws IOException, FileNotFoundException {
        final int BUFFER_SIZE = 1000;
        BitCoopFile tempFile = BitCoopFile.createTempFile("bCoopUnitTest", null);
        tempFile.deleteOnExit();
        FileOutputStream fout = new FileOutputStream(tempFile);
        if(dataToWrite == null){
            dataToWrite = new byte[BUFFER_SIZE];
            for(int i=0; i< fileSize; i++){
                dataToWrite[i%BUFFER_SIZE] = (byte) i;
                if(i%BUFFER_SIZE == dataToWrite.length-1){
                    fout.write(dataToWrite);
                }
            }
            fout.write(dataToWrite, 0, fileSize%BUFFER_SIZE);
        }
        else{
            fout.write(dataToWrite);
        }
        fout.close();
        return tempFile;
    }

}
