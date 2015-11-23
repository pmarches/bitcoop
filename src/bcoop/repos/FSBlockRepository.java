/**
 * <p>Title: BlockRepository.java</p>
 * <p>Description: Implementation on the filesystem of a block repository</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import org.apache.log4j.Logger;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.block.TransactionBlock;
import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class FSBlockRepository extends BlockRepository {
	
	private String directoryPath;
	Vector<Long> headerIds;
	Vector<Long> transactionIds;
	private long usedSpace=-1;
	
	public FSBlockRepository(String directoryPath) {
		this((FSRepositoryManager) null, directoryPath);
	}
	
	/**
	 * @param repositoryManager 
	 * @param peerName
	 * @throws FileNotFoundException
	 * 
	 */
	public FSBlockRepository(FSRepositoryManager repositoryManager, String directoryPath) {
		super(repositoryManager);
		
		if(directoryPath == null){
			throw new RuntimeException("DirectoryPath cannot be null");
		}
		this.directoryPath = directoryPath;
		this.usedSpace = getUsedSpace();
		try {
			BitCoopFile dirFile = new BitCoopFile(directoryPath);
			dirFile.mkdirs();
			openIndex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public long getUsedSpace() {
		if(usedSpace < 0){
			usedSpace = getUsedSpace(new BitCoopFile(directoryPath)); 
		}
		return usedSpace;
	}
	
	/*junit*/ long getUsedSpace(BitCoopFile rootFile){
		if(rootFile.isFile()) return rootFile.length();
		
		long size = 0;
		BitCoopFile[] files = rootFile.listFiles();
		if(files == null) return 0;
		for(BitCoopFile file : files){
			size += getUsedSpace(file);
		}
		return size;
	}
	
	private void openIndex()throws IOException, ClassNotFoundException {
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getHeaderIdFilePath()));
			allowedSpace = ois.readLong();
			headerIds = new Vector<Long>();
			int nbHeader = ois.readInt();
			for(int i=0; i<nbHeader; i++){
				headerIds.add(ois.readLong());
			}
			transactionIds = new Vector<Long>();
			int nbTransaction = ois.readInt();
			for(int i=0; i<nbTransaction; i++){
				transactionIds.add(ois.readLong());
			}
			ois.close();
		}
		catch(FileNotFoundException e){
			headerIds = new Vector<Long>();
			transactionIds = new Vector<Long>();
			allowedSpace = 0;
		}
		
	}
	
	public synchronized void saveIndex(){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getHeaderIdFilePath()));
			oos.writeLong(allowedSpace);
			oos.writeInt(headerIds.size());
			for(int i=0; i<headerIds.size(); i++){
				oos.writeLong(headerIds.get(i));
			}
			oos.writeInt(transactionIds.size());
			for(int i=0; i<transactionIds.size(); i++){
				oos.writeLong(transactionIds.get(i));
			}
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getHeaderIdFilePath() {
		return directoryPath+"/headerIds";
	}
	
	/*junit*/ String getFilePath(long blockId){
		String hexId = Long.toHexString(blockId);
		int len = hexId.length();
		if(len%2 == 1){
			hexId = "0"+hexId;
			len++;
		}
		StringBuffer path = new StringBuffer();
		path.append(directoryPath);
		for(int i=0; i< len; i+=2){
			path.append("/");
			path.append(hexId.substring(i, i+2));
		}
		
		return path.toString();
	}
	
	/*junit*/ BitCoopFile getFileForId(long blockId) {
		BitCoopFile parent = new BitCoopFile(getFilePath(blockId));
		parent.mkdirs();
		return new BitCoopFile(parent, "data");
	}
	
	public synchronized void storeBlock(NumberedBlock newBlock) {
		try {
			Logger.getLogger(this.getClass()).debug("Storing block "+Long.toHexString(newBlock.getBlockId()));
			//Update the index
			if(newBlock instanceof HeaderBlock){
				headerIds.add(newBlock.getBlockId());
			}
			else if(newBlock instanceof TransactionBlock){
				transactionIds.add(newBlock.getBlockId());
			}
			
			FileOutputStream fout = new FileOutputStream(getFileForId(newBlock.getBlockId()));
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(newBlock);
			usedSpace+=fout.getChannel().position();
			oos.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public synchronized NumberedBlock getBlock(Long blockId) {
		try{
			FileInputStream fis = new FileInputStream(getFileForId(blockId));
			ObjectInputStream ois = new ObjectInputStream(fis);
			NumberedBlock block = (NumberedBlock) ois.readObject();
			fis.close();
			return block;
		}
		catch(IOException e){
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void destroyRepository() {
		BitCoopFile root = new BitCoopFile(directoryPath);
		deleteTree(root);
	}
	
	private void deleteTree(BitCoopFile root) {
		BitCoopFile childs[] = root.listFiles();
		if(childs != null){
			for(BitCoopFile child : childs){
				deleteTree(child);
			}
		}
		if(root.delete() == false){
			Logger.getLogger(this.getClass()).error("Delete failed for file "+root);
		}
	}
	
	public Vector<Long> getAllHeaderBlockId() {
		return headerIds;
	}
	
	public void removeBlock(long blockId) {
		transactionIds.remove(blockId);
		headerIds.remove(blockId);
		
		BitCoopFile fileToDelete = getFileForId(blockId);
		if(fileToDelete != null){
			fileToDelete.delete();
		}
	}
	
	@Override
	public Vector<Long> getAllTransactionBlockId() {
		return transactionIds;
	}
}
