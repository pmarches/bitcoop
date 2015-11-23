/**
 * <p>Title: FileSet.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import java.io.Serializable;
import java.util.Vector;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author pmarches
 *
 */
public class NamedDataGroup implements Serializable{
    private static final long serialVersionUID = 4305032838508876916L;
    Vector<DataSelector> dataSelectors = new Vector<DataSelector>();
    final private String name;

    public NamedDataGroup(String name){
        this.name = name;
    }
    
    /**
     * @return
     */
    public String getName() {
        return name;
    }
    
    @SuppressWarnings("unused")
	private void setName(){
    		//The name is immutable, create a new Instance to change the name.
    		throw new NotImplementedException();
    }

    /**
     * @param selector
     */
    public void addSelector(DataSelector selector) {
        dataSelectors.add(selector);
    }
    
    public FileSelection expandToFiles(){
        FileSelection filesToBackup = new FileSelection(); 
        for(DataSelector dSelector : dataSelectors){
            if(dSelector instanceof IncludeDataSelector){
                dSelector.expandToFiles(filesToBackup);
            }
        }
        for(DataSelector dSelector : dataSelectors){
            if(dSelector instanceof ExcludeDataSelector){
                dSelector.expandToFiles(filesToBackup);
            }
        }
        return filesToBackup;
    }

    public DataSelector getDataSelector(int i) {
        return dataSelectors.get(i);
    }

}
