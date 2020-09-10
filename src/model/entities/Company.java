package model.entities;

/*
 * Date Created : 08-08-2020
 * @author : Yogeshwar Chaudhari
 */
public class Company {
	
	private String id;
	private String name;
	private String abn;
	private String webURL;
	private String address;

	// Constructors
	public Company(String id, String name, String abn, String webURL, String address) {
		this.id = id;
		this.name = name;
		this.abn = abn;
		this.webURL = webURL;
		this.address = address;
	}

	// Getters-Setters Starts Here
	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getAbn() {
		return abn;
	}
	
	
	public void setAbn(String abn) {
		this.abn = abn;
	}
	
	
	public String getWebURL() {
		return webURL;
	}
	
	
	public void setWebURL(String webURL) {
		this.webURL = webURL;
	}
	
	
	public String getAddress() {
		return address;
	}
	
	
	public void setAddress(String address) {
		this.address = address;
	}
	// Getters-Setters Ends Here

	
	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + ", abn=" + abn + ", webURL=" + webURL + ", address=" + address
				+ "]";
	}
	
	
	/*
	 * It returns the formatted string required for storing data into companies.txt file
	 * file format : companyId\nname\nabn\nwebURL\naddress
	 */
	public String getWriteFormattedRecord() {
		
		StringBuilder companyStr = new StringBuilder();
		companyStr.append(this.getId()		+"\n");
		companyStr.append(this.getName()	+"\n");
		companyStr.append(this.getAbn()		+"\n");
		companyStr.append(this.getWebURL()	+"\n");
		companyStr.append(this.getAddress()	+"\n");
		
		// Append an empty line as separation between the 2 company records
		companyStr.append("\n");				
		
		return companyStr.toString();
	}
}
