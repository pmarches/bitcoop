package bcoop.block;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.annotations.CollectionOfElements;

@javax.persistence.Entity
public class TransactionBlock extends NumberedBlock {
	protected static final long serialVersionUID = -4371419131190232788L;
	
	@CollectionOfElements
	protected List<HeaderBlock> blocksOfTransaction = new Vector<HeaderBlock>();
	protected long transactionSize;
	protected long transactionStartTime;
	protected long transactionEndTime;
	protected String dataName;
	protected String scheduleName;

	public TransactionBlock(long blockId, String dataName, String scheduleName) {
		super(blockId);
		this.dataName = dataName;
		this.scheduleName = scheduleName;
		this.transactionStartTime = System.currentTimeMillis();
	}
	
	public void addHeaderBlockToTransaction(HeaderBlock newBlock){
		blocksOfTransaction.add(newBlock);
		this.transactionSize+=newBlock.getFileLength();
	}
	
	public long getTransactionSize() {
		//FIXME must add the size of the transaction block information also!
		return transactionSize;
	}

	public long getTransactionStartTime() {
		return transactionStartTime;
	}

	public String getDataName() {
		return dataName;
	}

	@Override
	public void getReferencedBlock(Vector<Long> referencedBlocks) {
		for(HeaderBlock currentHBlock : blocksOfTransaction){
			referencedBlocks.add(currentHBlock.getBlockId());
			currentHBlock.getReferencedBlock(referencedBlocks);
		}
	}

	public HeaderBlock getHeaderBlockForFile(String filename) {
		for(HeaderBlock currentHBlock : blocksOfTransaction){
			if(currentHBlock.getFilename().equals(filename)){
				return currentHBlock;
			}
		}
		return null;
	}
	
    public Vector<HeaderBlock> getHeaderBlockForFileRegEx(String filenameRegEx) {
        Vector<HeaderBlock> hBlockList = new Vector<HeaderBlock>();
        
        Pattern filenamePattern = Pattern.compile(filenameRegEx);
        Iterator it = blocksOfTransaction.iterator();
        while(it.hasNext()){
            Map.Entry entry = (Entry) it.next();
            Matcher matcher = filenamePattern.matcher((String) entry.getKey());
            if(matcher.matches()) hBlockList.add((HeaderBlock) entry.getValue());
        }
        return hBlockList;
    }

	@SuppressWarnings("unchecked")
	public Vector<HeaderBlock> getAllHeaderBlock() {
		Vector<HeaderBlock> clonedVector = new Vector<HeaderBlock>();
		clonedVector.addAll(blocksOfTransaction);
		return clonedVector;
	}
	
	public int getNumberOfHeaderBlocks(){
		return blocksOfTransaction.size();
	}

	public void endTransaction() {
		this.transactionEndTime = System.currentTimeMillis();
	}
	public long getTransactionEndTime(){
		return this.transactionEndTime;
	}

	public String getScheduleName() {
		return this.scheduleName;
	}

	@Override
	public int getStorageSizeOfBlock() {
		// FIXME calculate the real transaction size here!
		return 0;
	}
}
