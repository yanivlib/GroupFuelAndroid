package com.mty.groupfuel.datamodel;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("User")
public class User extends ParseUser {
	
	public String getFirstName() {
        return getString("FirstName");
    }

    public void setFirstName(String value) {
        put("FirstName", value);
    }

	public String getLastName() {
        return getString("FirstName");
    }

    public void setLastName(String value) {
        put("LastName", value);
    }
	
    public Number getAge() {
        return getNumber("Age");
    }

    public void setAge(Number value) {
        put("Age", value);
    }
	
	public String getCountry() {
		return getString("Country");
	}
	
	public void setCountry(String value) {
		put("Country", value);
	}
	
	public boolean getGender() {
		return getBoolean("Gender");
	}
	
	public void setGender(boolean value) {
		put("Gender", value);
	}
	
	public Date getBirthDate() {
		return getDate("BirthDate");
	}
	
	public void setBirthDate(Date value) {
		put("BirthDate", value);
	}
}
