/*
A full-blown database demo developed at the
Mannheim University of Applied Sciences.

Copyright (C) 2011  the authors listed below.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package net.gumbix.dba.companydemo.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.gumbix.dba.companydemo.db.DBAccess;

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Marius Czardybon (m.czardybon@gmx.net)
 * @author Maximilian Nährlich (maximilian.naehrlich@stud.hs-mannheim.de )
 */
public class Project {

    private String projectId;
    private String description;
    private List<StatusReport> statusReports = new ArrayList<StatusReport>();
    private Set<WorksOn> employees = new HashSet<WorksOn>();
    // private long nextStatusReportNumber = 1;
    public long nextStatusReportNumber = 1;  // TODO, reflection does not work yet.
    private ProjectStatus status;

	public Project() {}

    public Project(String projectId, String description) {
        this.projectId = projectId;
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    // TODO Hibernate
    private void setProjectId(String id) {
        projectId = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StatusReport> getStatusReports() {
        return statusReports;
    }

    // TODO Hibernate
    private void setStatusReports(List<StatusReport> statusReports) {
        this.statusReports = statusReports;
    }
    

    public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}


    /**
     * Add a status report.
     * @param statusReport
     * @return False if report was already added, true if not.
     */
    public boolean addStatusReport(StatusReport statusReport) {
        if (!statusReports.contains(statusReport)) {
            statusReports.add(statusReport);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeStatusReport(StatusReport statusReport) {
        return statusReports.remove(statusReport);
    }

    public Set<WorksOn> getEmployees() {
        return employees;
    }

    private void setEmployees(Set<WorksOn> employees) {
        this.employees = employees;
    }

    public boolean addEmployee(WorksOn worksOn) {
        return employees.add(worksOn);
    }

    public long getNextStatusReportNumber() {
        nextStatusReportNumber++;
        return nextStatusReportNumber;
    }

    // TODO Hibernate
    private void setNextStatusReportNumber(long number) {
        nextStatusReportNumber = number;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Project)) {
            return false;
        } else {
            Project otherObject = (Project) other;
            return getProjectId().equals(otherObject.getProjectId());
        }
    }

    public String toString() {
        return description + " (" + projectId + ")";
    }

    public String toFullString() {
        return toString();
    }
    
    public List<ProjectStatusEnum> getNextStatus(){
    	ProjectStatusEnum currentStatus = this.getStatus().getStatus();
		List<ProjectStatusEnum> nextStatus = new ArrayList<>();
		
		switch(currentStatus){
			case New:
			case Cancelled:
			case Blocked:				
				nextStatus.add(ProjectStatusEnum.InProcess);
				break;
			case Finished:
				break;
			case InProcess:
				nextStatus.add(ProjectStatusEnum.Finished);
				nextStatus.add(ProjectStatusEnum.Blocked);
				nextStatus.add(ProjectStatusEnum.Cancelled);
				break;
		}
		
		return nextStatus;
	}
    
    public void setNextStatus(ProjectStatus nextStatus) throws Exception, SQLException{
		//test if nextStatis is valid
		boolean isValid = false;
		List<ProjectStatusEnum> posNextStatus = this.getNextStatus();
		if(posNextStatus == null){
			throw new Exception();
		}
		for(ProjectStatusEnum s : posNextStatus){
			if(isValid = (s == nextStatus.getStatus())){
				break;
			}
		}
		if(!isValid){
			throw new Exception("Ungueltiger neuer Status");
		}
		
		if(nextStatus.getStatus() == ProjectStatusEnum.InProcess || nextStatus.getStatus() == ProjectStatusEnum.Blocked){
			//Simply set the new status, there no further requirements
			this.setStatus(nextStatus);
		}
		
		if(nextStatus.getStatus() == ProjectStatusEnum.Finished){
			//remove all Employees working on this project		
			this.employees.clear();			
			//set next status
			this.setStatus(nextStatus);
		}
		
		if(nextStatus.getStatus() == ProjectStatusEnum.Cancelled){
			//if anyone is working on this project abort status change (its no allowed then)
			//Set<WorksOn> worksOn = db.loadWorksOn(project);
			if(this.employees.size() != 0){
				throw new Exception("Abbruch nicht moeglich: Es Arbeiten noch Personen am Projekt "+this.getProjectId());
			}
			this.setStatus(nextStatus);
		}

	}
}
