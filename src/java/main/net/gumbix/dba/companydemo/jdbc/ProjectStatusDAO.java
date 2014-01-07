package net.gumbix.dba.companydemo.jdbc;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.WeakHashMap;

import net.gumbix.dba.companydemo.db.ObjectNotFoundException;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.domain.ProjectStatusEnum;
import net.gumbix.dba.companydemo.domain.StatusReport;
import net.gumbix.dba.companydemo.domain.WorksOn;

public class ProjectStatusDAO extends AbstractDAO {
	
	public WeakHashMap<String, ProjectStatus> cache
    	= new WeakHashMap<String, ProjectStatus>();

	public ProjectStatusDAO(JdbcAccess access) {
		super(access);
	}

	public ProjectStatus load(ProjectStatusEnum projectStatus) throws Exception {
		String statusId = projectStatus.toString();
		ResultSet rs =
                executeSQLQuery("select * from ProjektStatus where statusId = '" + statusId + "'");

        if (rs.next()) {
        	ProjectStatus status = createAndCache(statusId, new ProjectStatus(statusId, rs.getString("beschreibung")));
            return status;
        } else {
            throw new ObjectNotFoundException(ProjectStatus.class, projectStatus + "");
        }
	}
	
	public void store(ProjectStatus projectStatus) throws Exception {
        PreparedStatement pstmt;
        try {
        	load(projectStatus.getStatus());
            // update
            pstmt = prepareStatement("update ProjektStatus set beschreibung = ?, " +
                    " where statusId = ?");
            pstmt.setString(1, projectStatus.getDescription());
            pstmt.setString(2, projectStatus.getStatusId());            
            pstmt.execute();
        } catch (ObjectNotFoundException e) {
            // new record
            pstmt = prepareStatement("insert into ProjektStatus values (?, ?)");
            pstmt.setString(1, projectStatus.getStatusId());
            pstmt.setString(2, projectStatus.getDescription());
            pstmt.execute();
        }
    }
	
	private ProjectStatus createAndCache(String statusId, ProjectStatus projectStatus) {
        cache.put(statusId, projectStatus);
        return projectStatus;
    }


}
