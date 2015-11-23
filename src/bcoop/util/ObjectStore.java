package bcoop.util;

import java.util.Properties;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.block.MetaDataBlock;
import bcoop.block.NumberedBlock;
import bcoop.block.TransactionBlock;
import bcoop.blocktracker.BlockToPeerTracking;
import bcoop.blocktracker.history.BaseHistory;
import bcoop.blocktracker.history.TransactionHistory;
import bcoop.identity.Identity;
import bcoop.identity.LocalIdentity;

public class ObjectStore {
	Session session;
	AnnotationConfiguration annotatedConfig;
	
	public ObjectStore(){
		try {
			Class.forName("org.hsqldb.jdbcDriver");

			Properties extraProperties = new Properties();
			extraProperties.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
			extraProperties.setProperty("hibernate.connection.url", "jdbc:hsqldb:"+getDbLocation());
			extraProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
			extraProperties.setProperty("hbm2ddl.auto", "create");
			
			annotatedConfig = new AnnotationConfiguration();
			annotatedConfig.addProperties(extraProperties);
			
			annotatedConfig.addAnnotatedClass(Identity.class);
			annotatedConfig.addAnnotatedClass(LocalIdentity.class);
			annotatedConfig.addAnnotatedClass(NumberedBlock.class);
			annotatedConfig.addAnnotatedClass(DataBlock.class);
			annotatedConfig.addAnnotatedClass(HeaderBlock.class);
			annotatedConfig.addAnnotatedClass(MetaDataBlock.class);
			annotatedConfig.addAnnotatedClass(TransactionBlock.class);
			//annotatedConfig.addAnnotatedClass(BaseHistory.class);
			//annotatedConfig.addAnnotatedClass(TransactionHistory.class);
 
			SessionFactory sessionFactory = annotatedConfig.buildSessionFactory();
			session = sessionFactory.openSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createSchema(){
		SchemaExport se = new SchemaExport(annotatedConfig);
		se.create(true, true);
	}

	protected String getDbLocation() {
		return "file:testData/testdb";
	}

	public void save(Object objToSave) {
		Transaction transaction = session.beginTransaction();
		session.save(objToSave);
		transaction.commit();
	}
	
	public void close(){
		SQLQuery shutdownCmd = session.createSQLQuery("SHUTDOWN");
		shutdownCmd.executeUpdate();
		session.close();
	}

	public NumberedBlock getBlock(long dataBlock) {
		return (NumberedBlock) session.load(NumberedBlock.class, dataBlock);
	}

	public Identity getIdentity(Long uniqueId) {
		return (Identity) session.load(Identity.class, uniqueId);
	}

	public LocalIdentity getLocalIdentity() {
		return null;
	}

}
