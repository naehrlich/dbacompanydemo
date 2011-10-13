package net.gumbix.dba.companydemo.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Marius Czardybon  (m.czardybon@gmx.net)
 */
public class Personnel {

    private long personnelNumber;
    private String lastName;
    private String firstName;
    private GregorianCalendar birthDate;
    private String street;
    private String houseNo;
    private Address address;
    private Department department;
    private String position;
    private Personnel boss;


    public Personnel() {
    }

    public Personnel(String lastName, String firstName, GregorianCalendar birthDate, Address adr) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = adr;
    }

    public long getPersonnelNumber() {
        return personnelNumber;
    }

    public void setPersonnelNumber(long personnelNumber) {
        this.personnelNumber = personnelNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public GregorianCalendar getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(GregorianCalendar birthDate) {
        this.birthDate = birthDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Personnel getBoss() {
        return boss;
    }

    public void setBoss(Personnel boss) {
        this.boss = boss;
    }

    public String toString() {
        return firstName + " " + lastName + " (" + personnelNumber + ")";
    }

    public String toFullString() {
        String base =
                "Name:       " + firstName + " " + lastName + "\n" +
                "Geb.-Datum: " + birthDate.get(Calendar.YEAR) + "\n" +
                "Adresse:    " + street + " " + houseNo +
                        ", " + address.getZip() + " " + address.getCity();
        String depString = "";
        if (department != null) {
            depString =  "\nAbt.:     " + department;
            depString += "\nFunktion: " + position;
        }
        String bossString = "";
        if (boss != null) {
            bossString = "\nChef: " + boss;
        }
        return base + depString + bossString;
    }
}