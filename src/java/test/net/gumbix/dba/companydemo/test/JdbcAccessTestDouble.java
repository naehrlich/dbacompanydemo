package net.gumbix.dba.companydemo.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;

import net.gumbix.dba.companydemo.domain.Address;
import net.gumbix.dba.companydemo.domain.Car;
import net.gumbix.dba.companydemo.domain.CompanyCar;
import net.gumbix.dba.companydemo.domain.Department;
import net.gumbix.dba.companydemo.domain.Employee;
import net.gumbix.dba.companydemo.domain.Personnel;
import net.gumbix.dba.companydemo.domain.Project;
import net.gumbix.dba.companydemo.domain.ProjectStatus;
import net.gumbix.dba.companydemo.domain.StatusReport;
import net.gumbix.dba.companydemo.domain.Worker;
import net.gumbix.dba.companydemo.domain.WorksOn;
import net.gumbix.dba.companydemo.jdbc.JdbcAccess;
import net.gumbix.dba.companydemo.jdbc.JdbcIdGenerator;

public class JdbcAccessTestDouble extends JdbcAccess{
	
	private TestDataGenerator testDataGenerator;
	
	public JdbcAccessTestDouble(TestDataGenerator testDataGenerator) throws Exception{
		super("jdbc:hsqldb:mem:mymemdb;sql.syntax_mys=true", "firmenwelt", "firmenwelt10");
		this.testDataGenerator = testDataGenerator;
		this.setupMockDb();
	}	
	
	private void setupMockDb() throws Exception{
		this.createMockTables();
		testDataGenerator.createMockData(this);
	}
	
	private void createMockTables() throws SQLException{        
        String[] setupStatements = {
        		"create user firmenwelt password 'firmenwelt10';",
        		"create schema firmenwelt AUTHORIZATION DBA;",
        		"create table Ort (plz char(5) primary key not null,ortsname varchar(20));",        		
        		"create table Abteilung (abteilungsNr bigint not null "+/*auto_increment*/"IDENTITY,bezeichnung varchar(30));",        		
        		"create table Mitarbeiter (personalNr bigint not null "+/*auto_increment*/"IDENTITY,vorname varchar(30),nachname varchar(30),"+
        				"strasse varchar(30),hausNr varchar(5),plz char(5),gebDatum date,gehalt decimal(7, 2),"+
        				"abteilungsId bigint,funktion varchar(30),vorgesetzterNr bigint,"/*primary key (personalNr),"*/+
        				"foreign key (vorgesetzterNr) references Mitarbeiter (personalNr),"+
        				"foreign key (abteilungsId) references Abteilung(abteilungsNr),"+
        				"foreign key (plz) references Ort(plz));",
        		"create index nameIdx on Mitarbeiter(nachname);",        		
        		"create table Angestellter (personalNr bigint "+/*auto_increment*/"IDENTITY not null,telefonNr varchar(20),"+
        				"foreign key (personalNr) references Mitarbeiter (personalNr)on update cascade on delete cascade);",        		
        		"create table Arbeiter (personalNr bigint primary key not null,arbeitsplatz varchar(20),"+
        				"foreign key (personalNr) references Mitarbeiter (personalNr)"+
        				"on update cascade on delete cascade);",        				
        		"create table Auto (modell varchar(20) primary key not null,marke varchar(20));",        		
        		"create table Firmenwagen (nummernschild varchar(12) primary key not null,modell varchar(20),personalNr bigint,"+
        				"foreign key (modell) references Auto(modell),foreign key (personalNr) references Angestellter(personalNr));",        		
        		"CREATE TABLE ProjektStatus (statusId VARCHAR(15) NOT NULL,beschreibung VARCHAR(255) NULL,PRIMARY KEY (statusId));",        		
        		"create table Projekt (projektId char(3) primary key not null,bezeichnung varchar(30),"+
        				"naechsteStatusNummer integer not null,statusId varchar(15)  NOT NULL,"+
        				"foreign key (statusId) references ProjektStatus(statusId));",        				
				"create index bezeichnungIdx on Projekt(bezeichnung);",				
				"create table Statusbericht (projektId char(3) not null,fortlaufendeNr bigint not null,datum date,"+
						"inhalt text,primary key (projektId, fortlaufendeNr),foreign key (projektId) references Projekt(projektId));",						
				"create table MitarbeiterArbeitetAnProjekt (personalNr bigint not null,projektId char(3) not null,taetigkeit varchar(255),"+
						"prozAnteil decimal(5, 2),primary key (personalNr, projektId),"+
						"foreign key (personalNr) references Angestellter(personalNr),foreign key (projektId) references Projekt(projektId));",
						
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Ort to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Abteilung  to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Mitarbeiter to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Angestellter to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Arbeiter  to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Auto to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Firmenwagen to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON ProjektStatus to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Projekt to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON Statusbericht to public;",
				"GRANT SELECT, UPDATE, DELETE, INSERT ON MitarbeiterArbeitetAnProjekt to public;",
				
				"create view MitarbeiterAlleKlassen as "+
						"(select Mitarbeiter.*, Ort.ortsname, Arbeiter.personalNr as wPersNr, Arbeiter.arbeitsplatz, Angestellter.personalNr as aPersNr, Angestellter.telefonNr"+
						" from Mitarbeiter left outer join Arbeiter on Mitarbeiter.personalNr = Arbeiter.personalNr "+
						" left outer join Angestellter on Mitarbeiter.personalNr = Angestellter.personalNr"+
						" join Ort on Mitarbeiter.plz = Ort.plz);",
						
				"GRANT SELECT, UPDATE, DELETE, INSERT ON MitarbeiterAlleKlassen to public;",
        };
        
        for(int i=0;++i<setupStatements.length;){
        	Statement query = this.connection.createStatement();
        	query.execute(setupStatements[i]);
        	query.close();
        } 
	}
	
	@Override
	public void close() {
		super.close();
		Statement query;
		try {
			query = this.connection.createStatement();
	    	query.execute("SHUTDOWN;");
	    	query.close();
		} catch (SQLException e) {
		}

	}
	
	/*
	private void createMockData() throws Exception{
		// Create some car types:
		Car touran = new Car("Touran", "VW");
		super.storeCar(touran);
		Car passat = new Car("Passat", "VW");
		super.storeCar(passat);
		Car sklasse = new Car("S-Klasse", "Mercedes");
		super.storeCar(sklasse);

		// Create some company cars:
		CompanyCar companyCar1234 = new CompanyCar("MA-MA 1234", sklasse);
		super.storeCompanyCar(companyCar1234);
		CompanyCar companyCar1235 = new CompanyCar("MA-MA 1235", passat);
		super.storeCompanyCar(companyCar1235);
		CompanyCar companyCar1236 = new CompanyCar("MA-MA 1236", touran);
		super.storeCompanyCar(companyCar1236);
		CompanyCar companyCar1237 = new CompanyCar("MA-MA 1237", passat);
		super.storeCompanyCar(companyCar1237);
		CompanyCar companyCar1240 = new CompanyCar("MA-MA 1240", passat);
		super.storeCompanyCar(companyCar1240);
		CompanyCar companyCar1241 = new CompanyCar("MA-MA 1241", passat);
		super.storeCompanyCar(companyCar1241);

		// Pool-Car:
		CompanyCar companyCar1238 = new CompanyCar("MA-MA 1238", touran);
		super.storeCompanyCar(companyCar1238);

		// Create some departments:
		Department management = new Department(1, "Management");
		super.storeDepartment(management);
		Department einkauf = new Department(2, "Einkauf");
		super.storeDepartment(einkauf);
		Department verkauf = new Department(3, "Verkauf & Marketing");
		super.storeDepartment(verkauf);
		Department it = new Department(4, "IT");
		super.storeDepartment(it);
		Department entwicklung = new Department(5, "Forschung & Entwicklung");
		super.storeDepartment(entwicklung);
		Department produktion = new Department(6, "Produktion");
		super.storeDepartment(produktion);
		Department buchhaltung = new Department(7, "Buchhaltung");
		super.storeDepartment(buchhaltung);
		Department kundendienst = new Department(8, "Kundendienst");
		super.storeDepartment(kundendienst);
		Department qualitaetssicherung = new Department(9, "Qualitätssicherung");
		super.storeDepartment(qualitaetssicherung);

		// Create personnel:

		// Management:
		Employee employeeLohe = addEmployee("Lohe", "Fransiska",
            1967, 12, 01, 15000.0, "Chefstraße", "1a", "68305", "Mannheim",
            "+49 621 12345-100", management, "Vorstand", null, companyCar1234);

		Employee employeeLindemann = addEmployee("Lindemann", "Hans",
            1968, 1, 21, 4200.5, "Pappelallee", "1a", "10437", "Berlin",
            "+49 621 12345-110", management, "Personalreferent", employeeLohe, null);

		// Einkauf:
		Employee employeeGaenzler = addEmployee("Gänzler", "Bernd",
            1964, 1, 5, 5320.0, "Hauptstraße", "110b", "68163", "Mannheim",
            "+49 621 12345-200", einkauf, "Einkäufer", employeeLohe, null);

		// Verkauf:
		Employee employeeRichter = addEmployee("Richter", "Simone",
            1971, 6, 6, 6100.0, "Ahornweg", "2", "68163", "Mannheim",
            "+49 621 12345-300", verkauf, "Verkaufsleitung", employeeLohe, companyCar1237);

		Employee employeeReinhard = addEmployee("Reinhard", "Marcus",
            1973, 5, 20, 4210.1, "Hauptstraße", "11", "68163", "Mannheim",
            "+49 621 12345-310", verkauf, "Verkäufer", employeeRichter, null);

		Employee employeeUhl = addEmployee("Uhl", "Paul",
            1982, 4, 20, 4210.1, "Langestraße", "1", "68163", "Mannheim",
            "+49 621 12345-320", verkauf, "Verkäufer", employeeRichter, null);

		// Kundendienst:
		Employee employeeSimon = addEmployee("Simon", "Frank",
            1971, 10, 20, 5900.0, "Holzweg", "23", "68163", "Mannheim",
            "+49 621 12345-330", kundendienst, "Kundendienstleitung", employeeLohe, companyCar1240);

		Employee employeeNix = addEmployee("Nix", "Karl",
            1961, 9, 12, 3280.0, "Ritterstraße", "12", "68163", "Mannheim",
            "+49 621 12345-340", kundendienst, "Service-Mitarbeiter", employeeSimon, companyCar1241);

		// IT
		Employee employeeZiegler = addEmployee("Ziegler", "Peter",
            1967, 01, 13, 7100.0, "Ulmenweg", "34", "69115", "Heidelberg",
            "+49 621 12345-400", it, "IT-Leiter", employeeLohe, null);

		Employee employeeBauer = addEmployee("Bauer", "Thomas",
            1985, 02, 24, 4100.0, "Dorfstraße", "1a", "68309", "Mannheim",
            "+49 621 12345-410", it, "Sys.-Admin", employeeZiegler, null);

		// Produktion:
		Employee employeeMueller = addEmployee("Müller", "Walter", 1949, 02, 11,
            5000.0, "Flussweg", "23", "68113", "Mannheim", "+49 621 12345-500",
            produktion, "Produktionsleiter", employeeLohe, companyCar1235);

		Worker workerKleinschmidt = addWorker("Kleinschmidt", "August",
            1955, 7, 23, 3800.0, "Wasserturmstraße", "29", "69214", "Eppelheim",
            "Halle A/Platz 30", produktion, "Nachfüller", employeeMueller);

		Worker workerZiegler = addWorker("Ziegler", "Peter",
            1961, 11, 15, 3600.0, "Wasserweg", "4", "69115", "Heidelberg",
            "Halle A/Platz 31", produktion, "Auffüller", employeeMueller);

		Worker workerSchmidt = addWorker("Schmidt", "Hanna",
            1977, 10, 29, 3550.0, "Wasserweg", "16", "69115", "Heidelberg",
            "Halle A/Platz 32", produktion, "Auffüller", employeeMueller);

		Worker workerAlbrecht = addWorker("Albrecht", "Justin",
            1991, 9, 9, 1200.0, "Liesgewann", "6", "69115", "Heidelberg",
            "Halle A/Platz 33", produktion, "Azubi", workerSchmidt);

		// F&E:

		// Dr. cannot be assigned to a field. Bad!
		Employee employeeFischer = addEmployee("Fischer, Dr.", "Jan",
            1968, 04, 10, 6900.0, "Untere straße", "2", "68163", "Mannheim",
            "+49 621 12345-600", entwicklung, "F&E_Leiter", employeeLohe, companyCar1236);
		// TODO car was also modified...
		super.storeCompanyCar(companyCar1236);

		Employee employeeWalther = addEmployee("Walther, Dr.", "Sabrina",
            1978, 07, 16, 5990.0, "Hansaweg", "22", "68163", "Mannheim",
            "+49 621 12345-610", entwicklung, "CAD-Experte", employeeFischer, null);

		Employee employeeThorn = addEmployee("Thorn", "Max",
            1956, 01, 30, 5800.0, "Hauptstraße", "110a", "68163", "Mannheim",
            "+49 621 12345-620", entwicklung, "Ingenieur", employeeFischer, null);

		// Buchhaltung:
		Employee employeeFischer2 = addEmployee("Fischer", "Lutz",
            1959, 5, 7, 4900.0, "Ulmenweg", "18", "68163", "Mannheim",
            "+49 621 12345-700", buchhaltung, "Chefbuchhalter", employeeLohe, null);

		Employee employeeKlein = addEmployee("Klein", "Jennifer",
            1979, 1, 29, 3850.0, "Lindenweg", "12", "68305", "Mannheim",
            "+49 621 12345-710", buchhaltung, "Buchhalter", employeeFischer2, null);

		// Berater:
		Employee employeeWeiss = addEmployee("Weiß", "Gisela",
            1959, 8, 10, 6280.0, "Unter den Linden", "141", "12487", "Berlin",
            "+49 621 12345-599", null, "Berater", employeeLohe, null);

		// --------------------- Projects ---------------------------
    
		//ProjektStatus
		ProjectStatus nu = new ProjectStatus("New", "Neu");
		ProjectStatus inProcess = new ProjectStatus("InProcess", "In Bearbeitung");
		ProjectStatus blocked = new ProjectStatus("Blocked", "Blockiert");
		ProjectStatus cancelled = new ProjectStatus("Cancelled", "Abgebrochen");
		ProjectStatus finished = new ProjectStatus("Finished", "Abgeschlossen"); 
		
		super.storeProjectStatus(nu);
		super.storeProjectStatus(inProcess);
		super.storeProjectStatus(blocked);
		super.storeProjectStatus(cancelled);
		super.storeProjectStatus(finished);

		// Leute einstellen:
		Project hirePeople = new Project("LES", "Personal einstellen");
		hirePeople.setStatus(nu);
		super.storeProject(hirePeople);
		WorksOn hirePeopleLohe = new WorksOn(employeeLohe, hirePeople, 10,
            "Verträge ausstellen");
		super.storeWorksOn(hirePeopleLohe);

		StatusReport hirePeopleReport1 = new StatusReport(
            new GregorianCalendar(2011, 10, 17).getTime(),
            "Das ist der erste Statusbericht", hirePeople);
		super.storeStatusReport(hirePeopleReport1);
		StatusReport hirePeopleReport2 = new StatusReport(
            new GregorianCalendar(2011, 10, 18).getTime(),
            "Das ist noch ein Statusbericht", hirePeople);
		super.storeStatusReport(hirePeopleReport2);

		// Neues Produkt entwickeln:
		Project research = new Project("FOP", "Neues Produkt entwickeln");
		research.setStatus(inProcess);
		super.storeProject(research);
		WorksOn researchWalther = new WorksOn(employeeWalther, research, 50,
            "Modelle entwerfen");
		super.storeWorksOn(researchWalther);

		WorksOn researchThorn = new WorksOn(employeeThorn, research, 100,
            "Thermodynamik berechnen");
		super.storeWorksOn(researchThorn);

		StatusReport researchReport1 = new StatusReport(
            new GregorianCalendar(2012, 8, 17).getTime(),
            "Das ist der erste Statusbericht", research);
		super.storeStatusReport(researchReport1);
		StatusReport researchReport2 = new StatusReport(
            new GregorianCalendar(2012, 8, 28).getTime(),
            "Fortschritte beim Modell", research);
		super.storeStatusReport(researchReport2);

		// DB portieren:
		Project dbPort = new Project("DBP", "DB portieren");
		dbPort.setStatus(nu);
		super.storeProject(dbPort);
		WorksOn dbPortZiegler = new WorksOn(employeeZiegler, dbPort, 20,
            "Architektur entwerfen");
		super.storeWorksOn(dbPortZiegler);

		WorksOn dbPortBauer = new WorksOn(employeeBauer, dbPort, 70,
            "Skripte schreiben");
		super.storeWorksOn(dbPortBauer);

		// Security-Konzept:
		Project securityConcept = new Project("SEC", "Security-Konzept fuer Firma");
		securityConcept.setStatus(nu);
		super.storeProject(securityConcept);
		WorksOn securityConceptZiegler = new WorksOn(employeeZiegler, securityConcept, 40,
            "Security-Konzept entwerfen");
		super.storeWorksOn(securityConceptZiegler);

		WorksOn securityConceptBauer = new WorksOn(employeeBauer, securityConcept, 40,
            "Hacking");
		super.storeWorksOn(securityConceptBauer);

		WorksOn securityConceptWeiss = new WorksOn(employeeWeiss, securityConcept, 100,
            "SQL-Code-Injection-Beratung");
		super.storeWorksOn(securityConceptWeiss);

		System.out.println("Beispieldaten erzeugt.");
		
	}

/**
 * Helper method to simplify creating an employee.
 *
 * @param lastName
 * @param firstName
 * @param year
 * @param month
 * @param day
 * @param street
 * @param houseNumber
 * @param zip
 * @param city
 * @param phone
 * @param dep
 * @param position
 * @param boss
 * @param car
 * @return
 * @throws Exception
 *//*
	private Employee addEmployee(String lastName, String firstName,
                             int year, int month, int day, double salary,
                             String street, String houseNumber,
                             String zip, String city, String phone,
                             Department dep, String position, Personnel boss,
                             CompanyCar car) throws Exception {
		Employee employee = new Employee(lastName, firstName,
            new GregorianCalendar(year, month, day).getTime(),
            new Address(street, houseNumber, zip, city), phone);
		employee.setSalary(salary);
		employee.setCar(car);
		employee.setDepartment(dep);
    	employee.setPosition(position);
    	employee.setBoss(boss);
    	super.storePersonnel(employee);
    	return employee;
	}

/**
 * Helper method to simplify creating a worker.
 *
 * @param lastName
 * @param firstName
 * @param year
 * @param month
 * @param day
 * @param street
 * @param houseNumber
 * @param zip
 * @param city
 * @param workplace
 * @param dep
 * @param position
 * @param boss
 * @return
 * @throws Exception
 *//*
	private Worker addWorker(String lastName, String firstName,
                         int year, int month, int day, double salary,
                         String street, String houseNumber,
                         String zip, String city, String workplace,
                         Department dep, String position, Personnel boss) throws Exception {
		Worker worker = new Worker(lastName, firstName,
            new GregorianCalendar(year, month, day).getTime(),
            new Address(street, houseNumber, zip, city), workplace);
		worker.setSalary(salary);
		worker.setDepartment(dep);
		worker.setPosition(position);
		worker.setBoss(boss);
		super.storePersonnel(worker);
		return worker;
	}*/
}

